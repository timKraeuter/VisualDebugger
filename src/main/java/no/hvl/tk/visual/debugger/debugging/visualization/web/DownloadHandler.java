package no.hvl.tk.visual.debugger.debugging.visualization.web;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;

public class DownloadHandler implements CefDownloadHandler {

  private final Project project;

  public DownloadHandler(Project project) {
    this.project = project;
  }

  @Override
  public void onBeforeDownload(
      CefBrowser browser,
      CefDownloadItem downloadItem,
      String suggestedName,
      CefBeforeDownloadCallback callback) {
  }

  @Override
  public void onDownloadUpdated(
      CefBrowser browser,
      CefDownloadItem downloadItem,
      CefDownloadItemCallback callback) {
    if (downloadItem.isComplete()) {
      // TODO: Download XML or SVG
      NotificationGroup notificationGroup = NotificationGroupManager.getInstance()
          .getNotificationGroup("Download Debugger Diagram");
      notificationGroup.createNotification("Download completed to x.", MessageType.INFO)
          .notify(project);
    }
  }
}
