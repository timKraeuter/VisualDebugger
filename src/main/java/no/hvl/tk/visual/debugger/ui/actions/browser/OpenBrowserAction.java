package no.hvl.tk.visual.debugger.ui.actions.browser;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.server.ServerConstants;
import no.hvl.tk.visual.debugger.ui.VisualDebuggerNotifications;
import org.jetbrains.annotations.NotNull;

public class OpenBrowserAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    if (SharedState.getUiServer() == null || !SharedState.getUiServer().isStarted()) {
      VisualDebuggerNotifications.notifyServerNotRunning();
    }
    BrowserUtil.browse(ServerConstants.UI_SERVER_URL);
  }
}
