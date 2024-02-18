package no.hvl.tk.visual.debugger.ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import net.sourceforge.plantuml.FileFormat
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer

class CopyPlantUMLDialog : DialogWrapper(true) {
  init {
    this.init()
    this.title = "Copy PlantUML Diagram"
  }

  override fun createCenterPanel(): JComponent {
    val dialogPanel = JPanel(GridBagLayout())

    // Show plantUML String.
    val plantUMLStringLabel = JLabel("PlantUML-Input:")
    val plantUMLStringButton = JButton("Copy")
    plantUMLStringButton.addActionListener { copyToClipBoard(SharedState.lastPlantUMLDiagram) }
    plantUMLStringLabel.labelFor = plantUMLStringButton

    dialogPanel.add(plantUMLStringLabel)
    dialogPanel.add(plantUMLStringButton)

    // Show plantUML svg data.
    val svgLabel = JLabel("PlantUML-SVG:")
    val c2 = GridBagConstraints()
    c2.gridy = 1
    dialogPanel.add(svgLabel, c2)

    if (SharedState.lastPlantUMLDiagram.isEmpty()) {
      val noDiagramLabel = JLabel("No diagram loaded.")
      svgLabel.labelFor = noDiagramLabel
      dialogPanel.add(noDiagramLabel, c2)
    } else {
      val svgCopyButton = JButton("Copy")
      svgCopyButton.addActionListener { copyToClipBoard(sVGData) }
      svgLabel.labelFor = svgCopyButton
      dialogPanel.add(svgCopyButton, c2)
    }
    return dialogPanel
  }

  companion object {
    private val LOGGER = Logger.getInstance(CopyPlantUMLDialog::class.java)

    private fun copyToClipBoard(content: String) {
      Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(content), null)
    }

    private val sVGData: String
      get() {
        try {
          return String(
              PlantUmlDebuggingVisualizer.toImage(SharedState.lastPlantUMLDiagram, FileFormat.SVG),
              StandardCharsets.UTF_8)
        } catch (e: IOException) {
          LOGGER.error(e)
        }
        return "Error loading SVG-Data. Check IDE log."
      }
  }
}
