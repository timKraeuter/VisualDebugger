package no.hvl.tk.visual.debugger.settings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VisualDebuggerSettingsComponentTest {

  @ParameterizedTest
  @ValueSource(strings = {"a", "", "-1", "-2"})
  void validateDepthField(String input) {
    addJBTextFieldWorkaround();
    JBTextField textField = new JBTextField(input);
    ValidationInfo validationInfo = VisualDebuggerSettingsComponent.validateNumberField(textField);
    assertNotNull(validationInfo);
    assertThat(validationInfo.message, is(VisualDebuggerSettingsComponent.NUMBER_GREATER_EQUALS_0));
  }

  private static void addJBTextFieldWorkaround() {
    // Workaround to avoid an error in the new JBTextField code since there is no real UI in the
    // test.
    System.setProperty("hidpi", "false");
  }
}
