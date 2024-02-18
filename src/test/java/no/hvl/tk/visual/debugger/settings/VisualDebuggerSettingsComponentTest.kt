package no.hvl.tk.visual.debugger.settings

import com.intellij.ui.components.JBTextField
import no.hvl.tk.visual.debugger.settings.VisualDebuggerSettingsComponent.Companion.validateNumberField
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class VisualDebuggerSettingsComponentTest {
  @ParameterizedTest
  @ValueSource(strings = ["a", "", "-1", "1.1", "1,1"])
  fun validateDepthField(input: String?) {
    addJBTextFieldWorkaround()
    val textField = JBTextField(input)
    val validationInfo = validateNumberField(textField)
    Assertions.assertNotNull(validationInfo)
    MatcherAssert.assertThat(
        validationInfo!!.message,
        CoreMatchers.`is`(VisualDebuggerSettingsComponent.NUMBER_GREATER_EQUALS_0))
  }

  companion object {
    private fun addJBTextFieldWorkaround() {
      // Workaround to avoid an error in the new JBTextField code since there is no real UI in the
      // test.
      System.setProperty("hidpi", "false")
    }
  }
}
