package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import no.hvl.tk.visual.debugger.SharedState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class VisualDebuggerSettingsConfigurable implements SearchableConfigurable {

  private VisualDebuggerSettingsComponent settingsComponent;
  private @Nullable Disposable settingsDisposable = null;

  @Override
  public @NotNull String getId() {
    return "no.hvl.tk.visualDebugger.settings";
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
    if (settingsDisposable != null) {
      Disposer.dispose(settingsDisposable);
    }
    settingsDisposable = Disposer.newDisposable();
    this.settingsComponent = new VisualDebuggerSettingsComponent(settingsDisposable);
    return this.settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    final PluginSettingsState settings = PluginSettingsState.getInstance();
    return this.visualizerOptionChanged(settings)
        || isDepthModified(settingsComponent.getVisualizationDepthText(),
        settings.getVisualisationDepth())
        || isDepthModified(settingsComponent.getLoadingDepthText(), settings.getLoadingDepth());
  }

  private boolean visualizerOptionChanged(final PluginSettingsState settings) {
    return settings.getVisualizerOption()
        != this.settingsComponent.getDebuggingVisualizerOptionChoice();
  }

  private boolean isDepthModified(String newDepthText, Integer currentDepth) {
    try {
      final int newDepth = Integer.parseInt(newDepthText);
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
    final PluginSettingsState settings = PluginSettingsState.getInstance();
    settings.setVisualizerOption(this.settingsComponent.getDebuggingVisualizerOptionChoice());

    final int newDepth = Integer.parseInt(this.settingsComponent.getVisualizationDepthText());
    VisualDebuggerSettingsConfigurable.changedDepthAndRestartDebuggerIfNeeded(settings, newDepth);

    final int newLoadingDepth = Integer.parseInt(this.settingsComponent.getLoadingDepthText());
    settings.setLoadingDepth(newLoadingDepth);
  }

  private static void changedDepthAndRestartDebuggerIfNeeded(final PluginSettingsState settings,
      final int newDepth) {
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
    if (settingsDisposable == null) {
      return;
    }
    Disposer.dispose(settingsDisposable);
    settingsDisposable = null;

    this.settingsComponent = null;
  }
}
