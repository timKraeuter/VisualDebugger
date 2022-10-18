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
public class VisualDebuggerSettingsState implements PersistentStateComponent<VisualDebuggerSettingsState> {

    private DebuggingVisualizerOption visualizerOption = DebuggingVisualizerOption.WEB_UI;
    private Integer visualisationDepth = 3;
    private Integer loadingDepth = 5;

    public static VisualDebuggerSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(VisualDebuggerSettingsState.class);
    }

    @Nullable
    @Override
    public VisualDebuggerSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull final VisualDebuggerSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public DebuggingVisualizerOption getVisualizerOption() {
        return this.visualizerOption;
    }

    public void setVisualizerOption(final DebuggingVisualizerOption visualizerOption) {
        this.visualizerOption = visualizerOption;
    }

    public Integer getVisualisationDepth() {
        return this.visualisationDepth;
    }

    public void setVisualisationDepth(final Integer visualisationDepth) {
        this.visualisationDepth = visualisationDepth;
    }

    public Integer getLoadingDepth() {
        return loadingDepth;
    }

    public void setLoadingDepth(Integer loadingDepth) {
        this.loadingDepth = loadingDepth;
    }
}
