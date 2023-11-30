package no.hvl.tk.visual.debugger.debugging.visualization.web;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;
import org.jetbrains.annotations.NotNull;

public class WebDownloadHandler implements CefDownloadHandler {

  private final Project project;

  public WebDownloadHandler(Project project) {
    this.project = project;
  }

  @Override
  public void onBeforeDownload(
      CefBrowser browser,
      CefDownloadItem downloadItem,
      String suggestedName,
      CefBeforeDownloadCallback callback) {

    String destinationDir = saveFile(downloadItem, suggestedName);

    notifyUser(destinationDir);
  }

  private void notifyUser(String destinationDir) {
    NotificationGroup notificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("Download Debugger Diagram");
    String content = String.format("<a href=\"%s\">%s</a>", destinationDir, destinationDir);
    notificationGroup
        .createNotification(content, MessageType.INFO)
        .setTitle("Download complete.")
        .notify(project);
  }

  @NotNull
  private String saveFile(CefDownloadItem downloadItem, String suggestedName) {
    String content = getFileContentFromURL(downloadItem.getURL());

    String filePath = this.getDestinationDir()  + File.separator + suggestedName;
    try {
      Files.writeString(Path.of(filePath), content);
    } catch (IOException e) {
      throw new RuntimeException(e);
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
  }

  private String getDestinationDir() {
    return project.getBasePath();
  }
}
