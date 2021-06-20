package no.hvl.tk.visual.debugger.actions.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import no.hvl.tk.visual.debugger.SharedState;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends DialogWrapper {
    private static final Logger LOGGER = Logger.getInstance(SettingsDialog.class);

    private JBTextField depthField;

    public SettingsDialog() {
        super(true);
        this.init();
        this.setTitle("Visual Debugger Settings");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final var dialogPanel = new JPanel(new GridBagLayout());

        final var label = new JLabel("Visualization depth:");
        this.depthField = new JBTextField(String.valueOf(SharedState.getVisualizationDepth()));
        label.setLabelFor(this.depthField);

        dialogPanel.add(label);
        dialogPanel.add(this.depthField);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        final String text = this.depthField.getText();
        try {
            final int newDepth = Integer.parseInt(text);
            if (SharedState.getVisualizationDepth() != newDepth) {
                SharedState.setVisualizationDepth(newDepth);
                SharedState.getDebugListener().startVisualDebugging();
            }
        } catch (final NumberFormatException e) {
            LOGGER.error(e);
        }
        super.doOKAction();
    }
}
