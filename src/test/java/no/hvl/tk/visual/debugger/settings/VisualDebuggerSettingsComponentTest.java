package no.hvl.tk.visual.debugger.settings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class VisualDebuggerSettingsComponentTest {

  @ParameterizedTest
  @ValueSource(strings = {"a", "", "-1", "1.1", "1,1"})
  void validateDepthFieldShouldReturnErrorForInvalidInputs(String input) {
    JBTextField textField = new JBTextField(input);
    ValidationInfo validationInfo = VisualDebuggerSettingsComponent.validateNumberField(textField);
    assertNotNull(validationInfo);
    assertThat(validationInfo.message, is(VisualDebuggerSettingsComponent.NUMBER_GREATER_EQUALS_0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "10", "100", "999"})
  void validateDepthFieldShouldReturnNullForValidInputs(String input) {
    JBTextField textField = new JBTextField(input);
    ValidationInfo validationInfo = VisualDebuggerSettingsComponent.validateNumberField(textField);
    assertNull(validationInfo);
  }

  // --- Port field validation ---

  @ParameterizedTest
  @ValueSource(
      strings = {
        // Non-numeric
        "a",
        "",
        "-1",
        "1.1",
        "1,1",
        "abc",
        "12.34",
        // Privileged ports (below 1024)
        "0",
        "1",
        "1023",
        "80",
        "443",
        // Above max (above 65535)
        "65536",
        "70000",
        "99999"
      })
  void validatePortFieldRejectsInvalidPorts(String input) {
    JBTextField textField = new JBTextField(input);
    ValidationInfo validationInfo = VisualDebuggerSettingsComponent.validatePortField(textField);
    assertNotNull(validationInfo);
    assertThat(validationInfo.message, is(VisualDebuggerSettingsComponent.PORT_VALIDATION_MESSAGE));
  }

  @Test
  void validatePortFieldAcceptsLowerBound() {
    JBTextField textField = new JBTextField("1024");
    assertNull(VisualDebuggerSettingsComponent.validatePortField(textField));
  }

  @Test
  void validatePortFieldAcceptsUpperBound() {
    JBTextField textField = new JBTextField("65535");
    assertNull(VisualDebuggerSettingsComponent.validatePortField(textField));
  }

  @ParameterizedTest
  @ValueSource(strings = {"1024", "8070", "8071", "8080", "49152", "65535"})
  void validatePortFieldAcceptsValidPorts(String input) {
    JBTextField textField = new JBTextField(input);
    assertNull(VisualDebuggerSettingsComponent.validatePortField(textField));
  }
}
