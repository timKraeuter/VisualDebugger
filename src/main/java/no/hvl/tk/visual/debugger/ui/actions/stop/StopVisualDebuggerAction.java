package no.hvl.tk.visual.debugger.ui.actions.stop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.SharedState;
import org.jetbrains.annotations.NotNull;

public class StopVisualDebuggerAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    SharedState.setDebuggingActive(false);
    SharedState.getDebugListener().getOrCreateDebuggingInfoVisualizer().debuggingDeactivated();
    SharedState.getDebugListener().resetUIAndAddActivateDebuggingButton();
  }
}
