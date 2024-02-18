package no.hvl.tk.visual.debugger.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.util.function.Supplier
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

class VisualDebuggerSettingsComponent(disposable: Disposable) {
  val panel: JPanel
  private val visualizationDepthField = JBTextField()
  private val savedDebugStepsField = JBTextField()
  private val coloredDiffCheckBox = JBCheckBox()
  private val showNullValuesCheckBox = JBCheckBox()
  private val visualizerOptionsCombobox = ComboBox(DebuggingVisualizerOption.entries.toTypedArray())

  init {
    this.panel =
        FormBuilder.createFormBuilder()
            .addLabeledComponent(
                JBLabel("Choose visualizer: "), this.visualizerOptionsCombobox, 1, false)
            .addLabeledComponent(
                JBLabel("Initial visualization depth: "), this.visualizationDepthField, 2, false)
            .addLabeledComponent(
                JBLabel("Number of debug history steps: "), this.savedDebugStepsField, 3, false)
            .addLabeledComponent(
                JBLabel("Color debug changes: "), this.coloredDiffCheckBox, 4, false)
            .addSeparator(5)
            .addLabeledComponent(
                JBLabel("Show null values: "), this.showNullValuesCheckBox, 6, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel

    this.addInputFieldValidators(disposable)
  }

  private fun addInputFieldValidators(disposable: Disposable) {
    ComponentValidator(disposable)
        .withValidator(
            Supplier {
              validateNumberField(this@VisualDebuggerSettingsComponent.visualizationDepthField)
            })
        .installOn(this.visualizationDepthField)
    visualizationDepthField.document.addDocumentListener(
        object : DocumentAdapter() {
          override fun textChanged(e: DocumentEvent) {
            ComponentValidator.getInstance(
                    this@VisualDebuggerSettingsComponent.visualizationDepthField)
                .ifPresent { obj: ComponentValidator -> obj.revalidate() }
          }
        })

    ComponentValidator(disposable)
        .withValidator(
            Supplier {
              validateNumberField(this@VisualDebuggerSettingsComponent.savedDebugStepsField)
            })
        .installOn(this.savedDebugStepsField)
    savedDebugStepsField.document.addDocumentListener(
        object : DocumentAdapter() {
          override fun textChanged(e: DocumentEvent) {
            ComponentValidator.getInstance(
                    this@VisualDebuggerSettingsComponent.savedDebugStepsField)
                .ifPresent { obj: ComponentValidator -> obj.revalidate() }
          }
        })
  }

  val preferredFocusedComponent: JComponent
    get() = this.visualizationDepthField

  var visualizationDepthText: String
    get() = visualizationDepthField.text
    set(visualizationDepth) {
      visualizationDepthField.text = visualizationDepth
    }

  val debuggingVisualizerOptionChoice: DebuggingVisualizerOption
    get() = visualizerOptionsCombobox.item

  fun chooseDebuggingVisualizerOption(option: DebuggingVisualizerOption) {
    visualizerOptionsCombobox.item = option
  }

  var savedDebugStepsText: String
    get() = savedDebugStepsField.text
    set(loadingDepth) {
      savedDebugStepsField.text = loadingDepth
    }

  var coloredDiffValue: Boolean
    get() = coloredDiffCheckBox.isSelected
    set(coloredDiffValue) {
      coloredDiffCheckBox.isSelected = coloredDiffValue
    }

  var showNullValues: Boolean
    get() = showNullValuesCheckBox.isSelected
    set(coloredDiffValue) {
      showNullValuesCheckBox.isSelected = coloredDiffValue
    }

  companion object {
    const val NUMBER_GREATER_EQUALS_0: String = "Must be a number greater or equal to 0."

    @JvmStatic
    fun validateNumberField(depthField: JBTextField): ValidationInfo? {
      val enteredDepth = depthField.text
      if (StringUtil.isEmpty(enteredDepth) || !StringUtil.isNotNegativeNumber(enteredDepth)) {
        return ValidationInfo(NUMBER_GREATER_EQUALS_0, depthField)
      }
      val depth = enteredDepth.toInt()
      if (depth < 0) {
        return ValidationInfo(NUMBER_GREATER_EQUALS_0, depthField)
      }
      // Means everything is ok.
      return null
    }
  }
}
