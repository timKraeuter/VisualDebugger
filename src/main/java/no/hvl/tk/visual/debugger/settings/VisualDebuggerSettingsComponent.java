package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class VisualDebuggerSettingsComponent {

    private static final String GREATER_THAN_0 = "The initial visualization depth must be greater than 0.";
    private static final String NUMBER_GREATER_THAN_0 = "The initial visualization depth must be a number greater than 0.";
    private static final String NOT_BE_EMPTY = "The initial visualization depth should not be empty.";

    private final JPanel myMainPanel;
    private final JBTextField visualizationDepthField = new JBTextField();

    public VisualDebuggerSettingsComponent(final Project project) {
        this.myMainPanel = FormBuilder.createFormBuilder()
                                      .addLabeledComponent(new JBLabel("Initial visualization depth: "), this.visualizationDepthField, 1, false)
                                      .addComponentFillVertically(new JPanel(), 0)
                                      .getPanel();

        this.addInputFieldValidator(project);
    }

    private void addInputFieldValidator(final Project project) {
        new ComponentValidator(project).withValidator(() -> {
            final String enteredDepth = VisualDebuggerSettingsComponent.this.visualizationDepthField.getText();
            if (StringUtil.isEmpty(enteredDepth)) {
                return new ValidationInfo(
                        VisualDebuggerSettingsComponent.NOT_BE_EMPTY,
                        VisualDebuggerSettingsComponent.this.visualizationDepthField);
            }
            try {
                final int depth = Integer.parseInt(enteredDepth);
                if (depth < 0) {
                    return new ValidationInfo(
                            VisualDebuggerSettingsComponent.GREATER_THAN_0,
                            VisualDebuggerSettingsComponent.this.visualizationDepthField);
                }
                return null; // Only integers are allowed.
            } catch (final NumberFormatException nfe) {
                return new ValidationInfo(
                        VisualDebuggerSettingsComponent.NUMBER_GREATER_THAN_0,
                        VisualDebuggerSettingsComponent.this.visualizationDepthField);
            }
        }).installOn(this.visualizationDepthField);

        this.visualizationDepthField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                ComponentValidator.getInstance(VisualDebuggerSettingsComponent.this.visualizationDepthField)
                                  .ifPresent(ComponentValidator::revalidate);
            }
        });
    }

    public JPanel getPanel() {
        return this.myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return this.visualizationDepthField;
    }

    @NotNull
    public String getVisualizationDepthText() {
        return this.visualizationDepthField.getText();
    }

    public void setVisualizationDepthText(@NotNull final String newText) {
        this.visualizationDepthField.setText(newText);
    }


}
