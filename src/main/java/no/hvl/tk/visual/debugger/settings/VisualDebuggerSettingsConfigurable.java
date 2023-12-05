package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import jakarta.websocket.Session;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  @Nullable @Override
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
        || isModified(
            settingsComponent.getVisualizationDepthText(), settings.getVisualisationDepth())
        || isModified(settingsComponent.getSavedDebugStepsText(), settings.getSavedDebugSteps())
        || settingsComponent.getColoredDiffValue() != settings.isColoredDiff()
        || settingsComponent.getShowNullValues() != settings.isShowNullValues();
  }

  private boolean visualizerOptionChanged(final PluginSettingsState settings) {
    return settings.getVisualizerOption()
        != this.settingsComponent.getDebuggingVisualizerOptionChoice();
  }

  private boolean isModified(String newDepthText, Integer currentDepth) {
    return !newDepthText.equals(currentDepth.toString());
  }

  @Override
  public void apply() {
    final PluginSettingsState settings = PluginSettingsState.getInstance();
    settings.setVisualizerOption(this.settingsComponent.getDebuggingVisualizerOptionChoice());

    final int newDepth = Integer.parseInt(this.settingsComponent.getVisualizationDepthText());
    VisualDebuggerSettingsConfigurable.changedDepthAndRestartDebuggerIfNeeded(settings, newDepth);

    final int newDebugSteps = Integer.parseInt(this.settingsComponent.getSavedDebugStepsText());
    settings.setSavedDebugSteps(newDebugSteps);

    settings.setColoredDiff(settingsComponent.getColoredDiffValue());

    settings.setShowNullValues(settingsComponent.getShowNullValues());

    sendUpdatedConfig();
  }

  private void sendUpdatedConfig() {
    for (Session client : SharedState.getWebsocketClients()) {
      VisualDebuggingAPIServerStarter.sendUIConfig(client);
    }
  }

  private static void changedDepthAndRestartDebuggerIfNeeded(
      final PluginSettingsState settings, final int newDepth) {
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
    this.settingsComponent.setSavedDebugStepsText(settings.getSavedDebugSteps().toString());
    this.settingsComponent.chooseDebuggingVisualizerOption(settings.getVisualizerOption());
    this.settingsComponent.setColoredDiffValue(settings.isColoredDiff());
    this.settingsComponent.setShowNullValues(settings.isShowNullValues());
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
