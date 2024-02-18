package no.hvl.tk.visual.debugger.ui.actions.browser

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import no.hvl.tk.visual.debugger.SharedState.uiServer
import no.hvl.tk.visual.debugger.server.ServerConstants
import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications.notifyServerNotRunning

class OpenBrowserAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    if (uiServer == null || !uiServer!!.isStarted) {
      notifyServerNotRunning()
    }
    BrowserUtil.browse(ServerConstants.UI_SERVER_URL)
  }
}
