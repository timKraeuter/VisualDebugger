package no.hvl.tk.visual.debugger.ui.actions.stop

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import no.hvl.tk.visual.debugger.SharedState

class StopVisualDebuggerAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    SharedState.debuggingActive = false
    SharedState.debugListener?.let {
      it.getOrCreateDebuggingInfoVisualizer().debuggingDeactivated()
      it.resetUIAndAddActivateDebuggingButton()
    }
  }
}
