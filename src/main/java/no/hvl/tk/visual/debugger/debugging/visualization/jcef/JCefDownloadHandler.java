package no.hvl.tk.visual.debugger.debugging.visualization.jcef;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;
import org.jetbrains.annotations.NotNull;

public class JCefDownloadHandler implements CefDownloadHandler {

  private static final Logger LOGGER = Logger.getInstance(JCefDownloadHandler.class);

  private final Project project;

  public JCefDownloadHandler(Project project) {
    this.project = project;
  }

  @Override
  public void onBeforeDownload(
      CefBrowser browser,
      CefDownloadItem downloadItem,
      String suggestedName,
      CefBeforeDownloadCallback callback) {

    String filePath = saveFile(downloadItem, suggestedName);

    VisualDebuggerNotifications.notifyDownloadCompleteUser(filePath);
  }

  @NotNull
  private String saveFile(CefDownloadItem downloadItem, String suggestedName) {
    String content = getFileContentFromURL(downloadItem.getURL());

    String filePath = this.getDestinationDir() + File.separator + suggestedName;
    try {
      Files.writeString(Path.of(filePath), content);
    } catch (IOException e) {
      LOGGER.error(e);
    }
    return filePath;
  }

  @NotNull
  private static String getFileContentFromURL(String url) {
    String encodingPrefix = "UTF-8,";
    int contentStart = url.indexOf(encodingPrefix) + encodingPrefix.length();
    String dataUTF8 = url.substring(contentStart);
    return URLDecoder.decode(dataUTF8, StandardCharsets.UTF_8);
  }

  @Override
  public void onDownloadUpdated(
      CefBrowser browser, CefDownloadItem downloadItem, CefDownloadItemCallback callback) {
    // NO OP
  }

  private String getDestinationDir() {
    return project.getBasePath();
  }
}
