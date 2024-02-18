package no.hvl.tk.visual.debugger.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import no.hvl.tk.visual.debugger.server.endpoint.UIConfig

/** Persistently saved settings of the plugin. */
@State(
    name = "no.hvl.tk.visual.debugger.settings.AppSettingsState",
    storages = [Storage("visualDebuggerPluginSettings.xml")])
class PluginSettingsState : PersistentStateComponent<PluginSettingsState?> {
  @JvmField var visualizerOption: DebuggingVisualizerOption = DebuggingVisualizerOption.WEB_UI
  @JvmField var visualisationDepth: Int = 0
  @JvmField var savedDebugSteps: Int = 3

  var isColoredDiff: Boolean = true

  var isShowNullValues: Boolean = false

  override fun getState(): PluginSettingsState {
    return this
  }

  override fun loadState(state: PluginSettingsState) {
    XmlSerializerUtil.copyBean(state, this)
  }

  val uIConfig: UIConfig
    get() = UIConfig(this.savedDebugSteps, this.isColoredDiff)

  companion object {
    @JvmStatic
    val settings: PluginSettingsState
      get() = ApplicationManager.getApplication().getService(PluginSettingsState::class.java)
  }
}
