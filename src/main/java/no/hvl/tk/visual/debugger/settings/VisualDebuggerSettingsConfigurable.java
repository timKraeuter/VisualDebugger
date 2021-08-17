package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.options.Configurable;
import no.hvl.tk.visual.debugger.SharedState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class VisualDebuggerSettingsConfigurable implements Configurable {

    private VisualDebuggerSettingsComponent mySettingsComponent;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Visual Debugger Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.mySettingsComponent = new VisualDebuggerSettingsComponent();
        return this.mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        final AppSettingsState settings = AppSettingsState.getInstance();
        return !(Integer.parseInt(this.mySettingsComponent.getVisualizationDepthText()) == settings.visualisationDepth);
    }

    @Override
    public void apply() {
        final AppSettingsState settings = AppSettingsState.getInstance();
        final int newDepth = Integer.parseInt(this.mySettingsComponent.getVisualizationDepthText());
        VisualDebuggerSettingsConfigurable.changedDepthAndRestartDebuggerIfNeeded(settings, newDepth);
    }

    private static void changedDepthAndRestartDebuggerIfNeeded(final AppSettingsState settings, final int newDepth) {
        if (newDepth != settings.visualisationDepth) {
            settings.visualisationDepth = newDepth;
            if (SharedState.getDebugListener() != null) {
                SharedState.getDebugListener().startVisualDebugging();
            }
        }
    }

    @Override
    public void reset() {
        final AppSettingsState settings = AppSettingsState.getInstance();
        this.mySettingsComponent.setVisualizationDepthText(settings.visualisationDepth.toString());
    }

    @Override
    public void disposeUIResources() {
        this.mySettingsComponent = null;
    }
}
