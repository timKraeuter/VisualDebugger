package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class VisualDebuggerSettingsConfigurable implements Configurable {
    private VisualDebuggerSettingsComponent settingsComponent;

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
        this.settingsComponent = new VisualDebuggerSettingsComponent();
        return this.settingsComponent.getComponent();
    }

    @Override
    public boolean isModified() {
        final VisualDebuggerSettingsState settings = VisualDebuggerSettingsState.getInstance();
        return this.visualizerOptionChanged(settings) || this.isVisualizationDepthModified(settings) || this.isLoadingDepthModified(settings);
    }

    private boolean visualizerOptionChanged(final VisualDebuggerSettingsState settings) {
        return settings.getVisualizerOption() != this.settingsComponent.getDebuggingVisualizerOptionChoice();
    }

    private boolean isVisualizationDepthModified(final VisualDebuggerSettingsState settings) {
        return depthModified(this.settingsComponent.getVisualizationDepthText(), settings.getVisualisationDepth());
    }

    private boolean isLoadingDepthModified(VisualDebuggerSettingsState settings) {
        return depthModified(this.settingsComponent.getLoadingDepthText(), settings.getLoadingDepth());
    }

    private boolean depthModified(String newDepthValue, Integer currentDepth) {
        try {
            final int newDepth = Integer.parseInt(newDepthValue);
            if (newDepth < 0) {
                return false;
            }
            return newDepth != currentDepth;
        } catch (final NumberFormatException nfe) {
            // Ignore this exception and update since there is a validation error shown in the field!
            return false;
        }
    }

    @Override
    public void apply() {
        final VisualDebuggerSettingsState settings = VisualDebuggerSettingsState.getInstance();
        this.settingsComponent.applyEditorTo(settings);
    }

    @Override
    public void reset() {
        final VisualDebuggerSettingsState settings = VisualDebuggerSettingsState.getInstance();
        this.settingsComponent.resetEditorFrom(settings);
    }

    @Override
    public void disposeUIResources() {
        this.settingsComponent = null;
    }
}
