package no.hvl.tk.visual.debugger.ui;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.ui.MessageType;

public class VisualDebuggerNotifications {

  private VisualDebuggerNotifications() {}

  private static final NotificationGroup NOTIFICATION_GROUP =
      NotificationGroupManager.getInstance().getNotificationGroup("VisualDebugger.NotifyDownload");

  public static void notifyDownloadStarted(String suggestedName) {
    NOTIFICATION_GROUP
        .createNotification(
            String.format("Download of %s started.", suggestedName), MessageType.INFO)
        .setIcon(VisualDebuggerIcons.VD_ICON)
        .setImportant(false)
        .notify(null);
  }
}
