package no.hvl.tk.visual.debugger.debugging.visualization.cef;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import java.awt.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications;

/**
 * Handles downloads triggered from the embedded JCEF browser.
 *
 * <p>The browser UI exports diagrams as {@code data:} URLs on anchor elements. JCEF does not
 * reliably raise a native download for {@code data:} URL clicks, so instead of relying on {@link
 * org.cef.handler.CefDownloadHandler} we intercept the click in JavaScript, forward the file name
 * and content to the IDE, and save it using a regular IntelliJ save dialog.
 *
 * <p>All communication with the injected JavaScript happens over a single {@code JBCefJSQuery}
 * bridge. Every message is prefixed with a type so that the JavaScript side can also report
 * diagnostic information that ends up in the IDE log (the browser console is not visible in
 * idea.log for us):
 *
 * <ul>
 *   <li>{@code log:<message>} &mdash; a diagnostic message logged on the Java side.
 *   <li>{@code download:<fileName>\n<content>} &mdash; a file to save.
 * </ul>
 */
public final class BrowserDownloadHandler {

  private static final Logger LOGGER = Logger.getInstance(BrowserDownloadHandler.class);

  private static final String LOG_PREFIX = "log:";
  private static final String DOWNLOAD_PREFIX = "download:";

  /** Separates the file name from the file content in a {@code download:} message. */
  private static final char PAYLOAD_SEPARATOR = '\n';

  private BrowserDownloadHandler() {}

  /**
   * Builds the JavaScript that is injected into the embedded browser to intercept download button
   * clicks. The interceptor prevents the (broken) native download, decodes the {@code data:} URL
   * content, and forwards everything &mdash; including diagnostic logs &mdash; to the IDE via the
   * given JCEF query call.
   *
   * @param jsQueryCall the JavaScript snippet produced by {@code JBCefJSQuery.inject("vdPayload")}
   */
  public static String buildInjectionScript(final String jsQueryCall) {
    return """
        (function () {
          function vdSend(vdPayload) { %s; }
          try {
            vdSend('log:Installing diagram download interceptor.');
            function bind(id) {
              var el = document.getElementById(id);
              if (!el) {
                vdSend('log:Download element not found: ' + id);
                return;
              }
              if (el.__vdBound) { return; }
              el.__vdBound = true;
              vdSend('log:Bound download interceptor to ' + id);
              el.addEventListener('click', function (e) {
                try {
                  var href = el.getAttribute('href') || '';
                  vdSend('log:Download click on ' + id + '; href prefix: ' + href.substring(0, 30));
                  if (href.indexOf('data:') !== 0) {
                    vdSend('log:Ignoring click; href is not a data: URL.');
                    return;
                  }
                  e.preventDefault();
                  var name = el.getAttribute('download') || 'diagram';
                  var content = decodeURIComponent(href.substring(href.indexOf(',') + 1));
                  vdSend('log:Forwarding download ' + name + ' (' + content.length + ' chars).');
                  vdSend('download:' + name + '\\n' + content);
                } catch (err) {
                  vdSend('log:Error while handling download click: ' + err);
                }
              }, true);
            }
            bind('js-download-board');
            bind('js-download-svg');
          } catch (err) {
            vdSend('log:Failed to install download interceptor: ' + err);
          }
        })();
        """
        .formatted(jsQueryCall);
  }

  /**
   * Dispatches a message received from the injected JavaScript over the JCEF bridge.
   *
   * @param message the raw message; either a {@code log:} or {@code download:} payload
   * @param parent the parent component for the save dialog
   */
  public static void handleMessage(final String message, final Component parent) {
    if (message == null) {
      LOGGER.warn("Received a null message from the embedded browser.");
      return;
    }
    if (message.startsWith(LOG_PREFIX)) {
      LOGGER.info("[embedded-browser] " + message.substring(LOG_PREFIX.length()));
      return;
    }
    if (message.startsWith(DOWNLOAD_PREFIX)) {
      saveDownload(message.substring(DOWNLOAD_PREFIX.length()), parent);
      return;
    }
    LOGGER.warn("Received an unknown message from the embedded browser: " + message);
  }

  /**
   * Saves a download forwarded from the embedded browser. Shows an IDE save dialog (on the EDT) and
   * writes the content to the chosen location.
   *
   * @param payload the file name and content, separated by a newline
   * @param parent the parent component for the save dialog
   */
  private static void saveDownload(final String payload, final Component parent) {
    final int separatorIndex = payload.indexOf(PAYLOAD_SEPARATOR);
    if (separatorIndex < 0) {
      LOGGER.warn("Received malformed download payload from the embedded browser (no separator).");
      return;
    }
    final String fileName = payload.substring(0, separatorIndex);
    final String content = payload.substring(separatorIndex + 1);
    LOGGER.info("Preparing to save download '" + fileName + "' (" + content.length() + " chars).");

    ApplicationManager.getApplication()
        .invokeLater(() -> showDialogAndSave(fileName, content, parent));
  }

  private static void showDialogAndSave(
      final String fileName, final String content, final Component parent) {
    final FileSaverDescriptor descriptor =
        new FileSaverDescriptor("Save Diagram", "Save the object diagram", extensionOf(fileName));
    final FileSaverDialog dialog =
        FileChooserFactory.getInstance().createSaveFileDialog(descriptor, parent);
    final VirtualFileWrapper wrapper = dialog.save(fileName);
    if (wrapper == null) {
      LOGGER.info("Download save dialog was cancelled by the user.");
      return; // User cancelled.
    }
    try {
      Files.writeString(wrapper.getFile().toPath(), content, StandardCharsets.UTF_8);
      LOGGER.info("Saved diagram download to " + wrapper.getFile().getAbsolutePath());
      VisualDebuggerNotifications.notifyDownloadStarted(fileName);
    } catch (final IOException e) {
      LOGGER.warn("Failed to save the downloaded diagram file.", e);
    }
  }

  private static String extensionOf(final String fileName) {
    final int dotIndex = fileName.lastIndexOf('.');
    return dotIndex >= 0 ? fileName.substring(dotIndex + 1) : "";
  }
}
