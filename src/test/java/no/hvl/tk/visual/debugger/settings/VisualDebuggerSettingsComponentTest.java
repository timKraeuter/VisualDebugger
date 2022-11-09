package no.hvl.tk.visual.debugger.settings;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VisualDebuggerSettingsComponentTest {

    @ParameterizedTest
    @ValueSource(strings = {"a", "", "0", "-1"})
    void validateDepthField(String input) {
        JBTextField textField = new JBTextField(input);
        ValidationInfo validationInfo = VisualDebuggerSettingsComponent.validateDepthField(textField);
        assertNotNull(validationInfo);
        assertThat(validationInfo.message, is(VisualDebuggerSettingsComponent.NUMBER_GREATER_THAN_0));
    }
}