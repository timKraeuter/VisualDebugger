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
 */
public final class BrowserDownloadHandler {

  private static final Logger LOGGER = Logger.getInstance(BrowserDownloadHandler.class);

  /** Separates the file name from the file content in the payload sent from JavaScript. */
  private static final char PAYLOAD_SEPARATOR = '\n';

  private BrowserDownloadHandler() {}

  /**
   * Builds the JavaScript that is injected into the embedded browser to intercept download button
   * clicks. The interceptor prevents the (broken) native download, decodes the {@code data:} URL
   * content, and forwards it to the IDE via the given JCEF query call.
   *
   * @param jsQueryCall the JavaScript snippet produced by {@code JBCefJSQuery.inject("vdPayload")}
   */
  public static String buildInjectionScript(final String jsQueryCall) {
    return """
        (function () {
          function bind(id) {
            var el = document.getElementById(id);
            if (!el || el.__vdBound) { return; }
            el.__vdBound = true;
            el.addEventListener('click', function (e) {
              var href = el.getAttribute('href') || '';
              if (href.indexOf('data:') !== 0) { return; }
              e.preventDefault();
              var name = el.getAttribute('download') || 'diagram';
              var content = decodeURIComponent(href.substring(href.indexOf(',') + 1));
              var vdPayload = name + '\\n' + content;
              %s;
            }, true);
          }
          bind('js-download-board');
          bind('js-download-svg');
        })();
        """
        .formatted(jsQueryCall);
  }

  /**
   * Saves a download forwarded from the embedded browser. Shows an IDE save dialog (on the EDT) and
   * writes the content to the chosen location.
   *
   * @param payload the file name and content, separated by a newline
   * @param parent the parent component for the save dialog
   */
  public static void saveDownload(final String payload, final Component parent) {
    final int separatorIndex = payload.indexOf(PAYLOAD_SEPARATOR);
    if (separatorIndex < 0) {
      LOGGER.warn("Received malformed download payload from the embedded browser.");
      return;
    }
    final String fileName = payload.substring(0, separatorIndex);
    final String content = payload.substring(separatorIndex + 1);

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
      return; // User cancelled.
    }
    try {
      Files.writeString(wrapper.getFile().toPath(), content, StandardCharsets.UTF_8);
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
