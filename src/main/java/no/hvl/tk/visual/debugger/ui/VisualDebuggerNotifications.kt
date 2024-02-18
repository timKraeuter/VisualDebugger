package no.hvl.tk.visual.debugger.ui

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.ui.MessageType

object VisualDebuggerNotifications {
  private val NOTIFICATION_GROUP: NotificationGroup =
      NotificationGroupManager.getInstance().getNotificationGroup("VisualDebugger.NotifyDownload")

  @JvmStatic
  fun notifyDownloadStarted(suggestedName: String?) {
    NOTIFICATION_GROUP.createNotification(
            String.format("Download of %s started.", suggestedName), MessageType.INFO)
        .setIcon(VisualDebuggerIcons.VD_ICON)
        .setImportant(false)
        .notify(null)
  }

  @JvmStatic
  fun notifyServerNotRunning() {
    NOTIFICATION_GROUP.createNotification(
            "Browser Visualizer is currently not running. You can start it in the Visual Debugger panel.",
            MessageType.INFO)
        .setIcon(VisualDebuggerIcons.VD_ICON)
        .setImportant(false)
        .notify(null)
  }
}
