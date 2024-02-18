package no.hvl.tk.visual.debugger.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.Disposer
import javax.swing.JComponent
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.SharedState.getWebsocketClients
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter
import no.hvl.tk.visual.debugger.settings.PluginSettingsState.Companion.settings
import org.jetbrains.annotations.Nls

class VisualDebuggerSettingsConfigurable : SearchableConfigurable {
  private var settingsComponent: VisualDebuggerSettingsComponent? = null
  private var settingsDisposable: Disposable? = null

  override fun getId(): String {
    return "no.hvl.tk.visualDebugger.settings"
  }

  override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
    return "Visual Debugger Settings"
  }

  override fun getPreferredFocusedComponent(): JComponent {
    return settingsComponent!!.preferredFocusedComponent
  }

  override fun createComponent(): JComponent {
    if (settingsDisposable != null) {
      Disposer.dispose(settingsDisposable!!)
    }
    settingsDisposable = Disposer.newDisposable()
    this.settingsComponent = VisualDebuggerSettingsComponent(settingsDisposable!!)
    return settingsComponent!!.panel
  }

  override fun isModified(): Boolean {
    return (this.visualizerOptionChanged(settings) ||
        isModified(settingsComponent!!.visualizationDepthText, settings.visualisationDepth) ||
        isModified(settingsComponent!!.savedDebugStepsText, settings.savedDebugSteps) ||
        (settingsComponent!!.coloredDiffValue != settings.isColoredDiff) ||
        (settingsComponent!!.showNullValues != settings.isShowNullValues))
  }

  private fun visualizerOptionChanged(settings: PluginSettingsState): Boolean {
    return settings.visualizerOption != settingsComponent!!.debuggingVisualizerOptionChoice
  }

  private fun isModified(newDepthText: String, currentDepth: Int): Boolean {
    return newDepthText != currentDepth.toString()
  }

  override fun apply() {
    settings.visualizerOption = settingsComponent!!.debuggingVisualizerOptionChoice

    val newDepth = settingsComponent!!.visualizationDepthText.toInt()
    changedDepthAndRestartDebuggerIfNeeded(settings, newDepth)

    val newDebugSteps = settingsComponent!!.savedDebugStepsText.toInt()
    settings.savedDebugSteps = newDebugSteps

    settings.isColoredDiff = settingsComponent!!.coloredDiffValue

    settings.isShowNullValues = settingsComponent!!.showNullValues

    sendUpdatedConfig()
  }

  private fun sendUpdatedConfig() {
    for (client in getWebsocketClients()) {
      VisualDebuggingAPIServerStarter.sendUIConfig(client)
    }
  }

  override fun reset() {
    settingsComponent!!.visualizationDepthText = settings.visualisationDepth.toString()
    settingsComponent!!.savedDebugStepsText = settings.savedDebugSteps.toString()
    settingsComponent!!.chooseDebuggingVisualizerOption(settings.visualizerOption)
    settingsComponent!!.coloredDiffValue = settings.isColoredDiff
    settingsComponent!!.showNullValues = settings.isShowNullValues
  }

  override fun disposeUIResources() {
    if (settingsDisposable == null) {
      return
    }
    Disposer.dispose(settingsDisposable!!)
    settingsDisposable = null

    this.settingsComponent = null
  }
}

private fun changedDepthAndRestartDebuggerIfNeeded(settings: PluginSettingsState, newDepth: Int) {
  if (newDepth != settings.visualisationDepth) {
    settings.visualisationDepth = newDepth
    if (SharedState.debugListener != null) {
      SharedState.debugListener!!.reprintDiagram()
    }
  }
}
