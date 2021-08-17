package no.hvl.tk.visual.debugger.settings;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class VisualDebuggerSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField visualizationDepthField = new JBTextField();

    public VisualDebuggerSettingsComponent() {
        this.myMainPanel = FormBuilder.createFormBuilder()
                                      .addLabeledComponent(new JBLabel("Initial visualization depth: "), this.visualizationDepthField, 1, false)
                                      .addComponentFillVertically(new JPanel(), 0)
                                      .getPanel();
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
