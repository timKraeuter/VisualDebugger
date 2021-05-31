package no.hvl.tk.visual.debugger.actions.print;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import net.sourceforge.plantuml.FileFormat;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PrintDialog extends DialogWrapper {
    private static final Logger LOGGER = Logger.getInstance(PrintDialog.class);

    public PrintDialog() {
        super(true);
        this.init();
        this.setTitle("Visual Debugger Diagram Print");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        final JPanel dialogPanel = new JPanel(new GridBagLayout());

        // Show plantUML String.
        final JLabel plantUMLStringLabel = new JLabel("PlantUML-Input:");
        final JButton plantUMLStringButton = new JButton("Copy");
        plantUMLStringButton.addActionListener(
                actionEvent -> PrintDialog.copyToClipBoard(SharedState.last_plantuml_diagram));
        plantUMLStringLabel.setLabelFor(plantUMLStringButton);

        dialogPanel.add(plantUMLStringLabel);
        dialogPanel.add(plantUMLStringButton);

        // Show plantUML svg data.
        final JLabel svgLabel = new JLabel("SVG:");
        final GridBagConstraints c2 = new GridBagConstraints();
        c2.gridy = 1;
        dialogPanel.add(svgLabel, c2);

        if (SharedState.last_plantuml_diagram.isEmpty()) {
            final JLabel noDiagramLabel = new JLabel("No diagram loaded.");
            svgLabel.setLabelFor(noDiagramLabel);
            dialogPanel.add(noDiagramLabel, c2);
        } else {
            final JButton svgCopyButton = new JButton("Copy");
            svgCopyButton.addActionListener(
                    actionEvent -> PrintDialog.copyToClipBoard(PrintDialog.getSVGData()));
            svgLabel.setLabelFor(svgCopyButton);
            dialogPanel.add(svgCopyButton, c2);
        }
        return dialogPanel;
    }

    private static void copyToClipBoard(final String content) {
        Toolkit.getDefaultToolkit()
               .getSystemClipboard()
               .setContents(
                       new StringSelection(content),
                       null
               );
    }

    private static String getSVGData() {
        try {
            return new String(
                    PlantUmlDebuggingVisualizer.toImage(SharedState.last_plantuml_diagram, FileFormat.SVG),
                    StandardCharsets.UTF_8);
        } catch (final IOException e) {
            LOGGER.error(e);
        }
        return "Error loading SVG-Data. Check IDE log.";
    }
}
