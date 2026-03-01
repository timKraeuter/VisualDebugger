package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VisualDebuggerSettingsComponent {
  static final String NUMBER_GREATER_EQUALS_0 = "Must be a number greater or equal to 0.";
  static final String PORT_VALIDATION_MESSAGE = "Must be a number between 1024 and 65535.";

  private final JPanel myMainPanel;
  private final JBTextField visualizationDepthField = new JBTextField();
  private final JBTextField savedDebugStepsField = new JBTextField();
  private final JBCheckBox coloredDiffCheckBox = new JBCheckBox();
  private final JBCheckBox showNullValuesCheckBox = new JBCheckBox();
  private final JBTextField uiServerPortField = new JBTextField();
  private final JBTextField apiServerPortField = new JBTextField();
  private final ComboBox<DebuggingVisualizerOption> visualizerOptionsCombobox =
      new ComboBox<>(DebuggingVisualizerOption.values());

  public VisualDebuggerSettingsComponent(final Disposable disposable) {
    this.myMainPanel =
        FormBuilder.createFormBuilder()
            .addLabeledComponent(
                new JBLabel("Choose visualizer: "), this.visualizerOptionsCombobox, 1, false)
            .addLabeledComponent(
                new JBLabel("Initial visualization depth: "),
                this.visualizationDepthField,
                2,
                false)
            .addLabeledComponent(
                new JBLabel("Number of debug history steps: "), this.savedDebugStepsField, 3, false)
            .addLabeledComponent(
                new JBLabel("Color debug changes: "), this.coloredDiffCheckBox, 4, false)
            .addSeparator(5)
            .addLabeledComponent(
                new JBLabel("Show null values: "), this.showNullValuesCheckBox, 6, false)
            .addSeparator(7)
            .addLabeledComponent(new JBLabel("UI server port: "), this.uiServerPortField, 8, false)
            .addLabeledComponent(
                new JBLabel("WebSocket API server port: "), this.apiServerPortField, 9, false)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();

    this.addInputFieldValidators(disposable);
  }

  private void addInputFieldValidators(final Disposable disposable) {
    new ComponentValidator(disposable)
        .withValidator(
            () -> validateNumberField(VisualDebuggerSettingsComponent.this.visualizationDepthField))
        .installOn(this.visualizationDepthField);
    this.visualizationDepthField
        .getDocument()
        .addDocumentListener(
            new DocumentAdapter() {
              @Override
              protected void textChanged(@NotNull final DocumentEvent e) {
                ComponentValidator.getInstance(
                        VisualDebuggerSettingsComponent.this.visualizationDepthField)
                    .ifPresent(ComponentValidator::revalidate);
              }
            });

    new ComponentValidator(disposable)
        .withValidator(
            () -> validateNumberField(VisualDebuggerSettingsComponent.this.savedDebugStepsField))
        .installOn(this.savedDebugStepsField);
    this.savedDebugStepsField
        .getDocument()
        .addDocumentListener(
            new DocumentAdapter() {
              @Override
              protected void textChanged(@NotNull final DocumentEvent e) {
                ComponentValidator.getInstance(
                        VisualDebuggerSettingsComponent.this.savedDebugStepsField)
                    .ifPresent(ComponentValidator::revalidate);
              }
            });

    new ComponentValidator(disposable)
        .withValidator(
            () -> validatePortField(VisualDebuggerSettingsComponent.this.uiServerPortField))
        .installOn(this.uiServerPortField);
    this.uiServerPortField
        .getDocument()
        .addDocumentListener(
            new DocumentAdapter() {
              @Override
              protected void textChanged(@NotNull final DocumentEvent e) {
                ComponentValidator.getInstance(
                        VisualDebuggerSettingsComponent.this.uiServerPortField)
                    .ifPresent(ComponentValidator::revalidate);
              }
            });

    new ComponentValidator(disposable)
        .withValidator(
            () -> validatePortField(VisualDebuggerSettingsComponent.this.apiServerPortField))
        .installOn(this.apiServerPortField);
    this.apiServerPortField
        .getDocument()
        .addDocumentListener(
            new DocumentAdapter() {
              @Override
              protected void textChanged(@NotNull final DocumentEvent e) {
                ComponentValidator.getInstance(
                        VisualDebuggerSettingsComponent.this.apiServerPortField)
                    .ifPresent(ComponentValidator::revalidate);
              }
            });
  }

  @Nullable static ValidationInfo validateNumberField(JBTextField depthField) {
    final String enteredDepth = depthField.getText();
    if (StringUtil.isEmpty(enteredDepth) || !StringUtil.isNotNegativeNumber(enteredDepth)) {
      return new ValidationInfo(
          VisualDebuggerSettingsComponent.NUMBER_GREATER_EQUALS_0, depthField);
    }
    int depth = Integer.parseInt(enteredDepth);
    if (depth < 0) {
      return new ValidationInfo(
          VisualDebuggerSettingsComponent.NUMBER_GREATER_EQUALS_0, depthField);
    }
    // Means everything is ok.
    return null;
  }

  @Nullable static ValidationInfo validatePortField(JBTextField portField) {
    final String enteredPort = portField.getText();
    if (StringUtil.isEmpty(enteredPort) || !StringUtil.isNotNegativeNumber(enteredPort)) {
      return new ValidationInfo(VisualDebuggerSettingsComponent.PORT_VALIDATION_MESSAGE, portField);
    }
    int port = Integer.parseInt(enteredPort);
    if (port < 1024 || port > 65535) {
      return new ValidationInfo(VisualDebuggerSettingsComponent.PORT_VALIDATION_MESSAGE, portField);
    }
    // Means everything is ok.
    return null;
  }

  public JPanel getPanel() {
    return this.myMainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return this.visualizationDepthField;
  }

  @NotNull public String getVisualizationDepthText() {
    return this.visualizationDepthField.getText();
  }

  public void setVisualizationDepthText(@NotNull final String visualizationDepth) {
    this.visualizationDepthField.setText(visualizationDepth);
  }

  public DebuggingVisualizerOption getDebuggingVisualizerOptionChoice() {
    return this.visualizerOptionsCombobox.getItem();
  }

  public void chooseDebuggingVisualizerOption(final DebuggingVisualizerOption option) {
    this.visualizerOptionsCombobox.setItem(option);
  }

  public void setSavedDebugStepsText(@NotNull final String loadingDepth) {
    this.savedDebugStepsField.setText(loadingDepth);
  }

  @NotNull public String getSavedDebugStepsText() {
    return savedDebugStepsField.getText();
  }

  public void setColoredDiffValue(final boolean coloredDiffValue) {
    this.coloredDiffCheckBox.setSelected(coloredDiffValue);
  }

  public boolean getColoredDiffValue() {
    return coloredDiffCheckBox.isSelected();
  }

  public void setShowNullValues(final boolean coloredDiffValue) {
    this.showNullValuesCheckBox.setSelected(coloredDiffValue);
  }

  public boolean getShowNullValues() {
    return showNullValuesCheckBox.isSelected();
  }

  @NotNull public String getUiServerPortText() {
    return uiServerPortField.getText();
  }

  public void setUiServerPortText(@NotNull final String port) {
    this.uiServerPortField.setText(port);
  }

  @NotNull public String getApiServerPortText() {
    return apiServerPortField.getText();
  }

  public void setApiServerPortText(@NotNull final String port) {
    this.apiServerPortField.setText(port);
  }
}
