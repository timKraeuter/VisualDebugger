package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Persistently saved settings of the plugin.
 */
@State(
        name = "no.hvl.tk.visual.debugger.settings.AppSettingsState",
        storages = {@Storage("visualDebuggerPluginSettings.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public DebuggingVisualizerOption visualizerOption = DebuggingVisualizerOption.WEB_UI;
    public Integer visualisationDepth = 5;

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull final AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
