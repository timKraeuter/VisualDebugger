package no.hvl.tk.visual.debugger.ui;

import com.intellij.notification.BrowseNotificationAction;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.ui.MessageType;

public class VisualDebuggerNotifications {

    private VisualDebuggerNotifications() {
    }

    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance()
        .getNotificationGroup("VisualDebugger.NotifyDownload");

    public static void notifyDownloadCompleteUser(String fileDestination) {
        NOTIFICATION_GROUP
            .createNotification("Download complete.", MessageType.INFO)
            .setIcon(VisualDebuggerIcons.VD_ICON)
            .setImportant(false)
            .addAction(new BrowseNotificationAction(fileDestination, fileDestination))
            .notify(null);
    }
}