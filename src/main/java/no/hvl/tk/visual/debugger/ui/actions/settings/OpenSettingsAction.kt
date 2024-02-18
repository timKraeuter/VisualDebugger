package no.hvl.tk.visual.debugger.ui.actions.settings

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.options.ShowSettingsUtil
import no.hvl.tk.visual.debugger.settings.VisualDebuggerSettingsConfigurable

class OpenSettingsAction : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val project = e.getData(CommonDataKeys.PROJECT)
    ShowSettingsUtil.getInstance()
        .showSettingsDialog(project, VisualDebuggerSettingsConfigurable::class.java)
  }
}
