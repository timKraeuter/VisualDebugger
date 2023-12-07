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

  public static void notifyServerNotRunning() {
    NOTIFICATION_GROUP
        .createNotification(
            "Browser Visualizer is currently not running. You can start it in the Visual Debugger panel.",
            MessageType.INFO)
        .setIcon(VisualDebuggerIcons.VD_ICON)
        .setImportant(false)
        .notify(null);
  }
}
