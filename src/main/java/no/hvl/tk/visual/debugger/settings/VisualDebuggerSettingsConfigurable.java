package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import no.hvl.tk.visual.debugger.SharedState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class VisualDebuggerSettingsConfigurable implements Configurable {
    private VisualDebuggerSettingsComponent settingsComponent;
    private final Project project;

    public VisualDebuggerSettingsConfigurable(final Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Visual Debugger Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.settingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        this.settingsComponent = new VisualDebuggerSettingsComponent(this.project);
        return this.settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        final PluginSettingsState settings = PluginSettingsState.getInstance();
        return this.visualizerOptionChanged(settings) || this.isDepthModified(settings);
    }

    private boolean visualizerOptionChanged(final PluginSettingsState settings) {
        return settings.getVisualizerOption() != this.settingsComponent.getDebuggingVisualizerOptionChoice();
    }

    private boolean isDepthModified(final PluginSettingsState settings) {
        try {
            final int newDepth = Integer.parseInt(this.settingsComponent.getVisualizationDepthText());
            if (newDepth < 0) {
                return false;
            }
            return newDepth != settings.getVisualisationDepth();
        } catch (final NumberFormatException nfe) {
            // Ignore this exception and update since there is a validation error shown in the field!
            return false;
        }
    }

    @Override
    public void apply() {
        final PluginSettingsState settings = PluginSettingsState.getInstance();
        settings.setVisualizerOption(this.settingsComponent.getDebuggingVisualizerOptionChoice());

        final int newDepth = Integer.parseInt(this.settingsComponent.getVisualizationDepthText());
        VisualDebuggerSettingsConfigurable.changedDepthAndRestartDebuggerIfNeeded(settings, newDepth);

        final int newLoadingDepth = Integer.parseInt(this.settingsComponent.getLoadingDepthText());
        settings.setLoadingDepth(newLoadingDepth);
    }

    private static void changedDepthAndRestartDebuggerIfNeeded(final PluginSettingsState settings, final int newDepth) {
        if (newDepth != settings.getVisualisationDepth()) {
            settings.setVisualisationDepth(newDepth);
            if (SharedState.getDebugListener() != null) {
                SharedState.getDebugListener().reprintDiagram();
            }
        }
    }

    @Override
    public void reset() {
        final PluginSettingsState settings = PluginSettingsState.getInstance();
        this.settingsComponent.setVisualizationDepthText(settings.getVisualisationDepth().toString());
        this.settingsComponent.setLoadingDepthText(settings.getLoadingDepth().toString());
        this.settingsComponent.chooseDebuggingVisualizerOption(settings.getVisualizerOption());
    }

    @Override
    public void disposeUIResources() {
        this.settingsComponent = null;
    }
}
