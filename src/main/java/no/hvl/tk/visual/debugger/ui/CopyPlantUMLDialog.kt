package no.hvl.tk.visual.debugger.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import net.sourceforge.plantuml.FileFormat;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import org.jetbrains.annotations.Nullable;

public class CopyPlantUMLDialog extends DialogWrapper {
  private static final Logger LOGGER = Logger.getInstance(CopyPlantUMLDialog.class);

  public CopyPlantUMLDialog() {
    super(true);
    this.init();
    this.setTitle("Copy PlantUML Diagram");
  }

  @Override
  protected @Nullable JComponent createCenterPanel() {
    final var dialogPanel = new JPanel(new GridBagLayout());

    // Show plantUML String.
    final var plantUMLStringLabel = new JLabel("PlantUML-Input:");
    final var plantUMLStringButton = new JButton("Copy");
    plantUMLStringButton.addActionListener(
        actionEvent -> CopyPlantUMLDialog.copyToClipBoard(SharedState.lastPlantUMLDiagram));
    plantUMLStringLabel.setLabelFor(plantUMLStringButton);

    dialogPanel.add(plantUMLStringLabel);
    dialogPanel.add(plantUMLStringButton);

    // Show plantUML svg data.
    final var svgLabel = new JLabel("PlantUML-SVG:");
    final var c2 = new GridBagConstraints();
    c2.gridy = 1;
    dialogPanel.add(svgLabel, c2);

    if (SharedState.lastPlantUMLDiagram.isEmpty()) {
      final var noDiagramLabel = new JLabel("No diagram loaded.");
      svgLabel.setLabelFor(noDiagramLabel);
      dialogPanel.add(noDiagramLabel, c2);
    } else {
      final var svgCopyButton = new JButton("Copy");
      svgCopyButton.addActionListener(
          actionEvent -> CopyPlantUMLDialog.copyToClipBoard(CopyPlantUMLDialog.getSVGData()));
      svgLabel.setLabelFor(svgCopyButton);
      dialogPanel.add(svgCopyButton, c2);
    }
    return dialogPanel;
  }

  private static void copyToClipBoard(final String content) {
    Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(content), null);
  }

  private static String getSVGData() {
    try {
      return new String(
          PlantUmlDebuggingVisualizer.toImage(SharedState.lastPlantUMLDiagram, FileFormat.SVG),
          StandardCharsets.UTF_8);
    } catch (final IOException e) {
      LOGGER.error(e);
    }
    return "Error loading SVG-Data. Check IDE log.";
  }
}
