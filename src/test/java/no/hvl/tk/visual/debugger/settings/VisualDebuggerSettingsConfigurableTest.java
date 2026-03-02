package no.hvl.tk.visual.debugger.settings;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisualDebuggerSettingsConfigurableTest {

  private VisualDebuggerSettingsConfigurable configurable;
  private VisualDebuggerSettingsComponent component;

  @BeforeEach
  void setUp() throws Exception {
    configurable = new VisualDebuggerSettingsConfigurable();
    component = mock(VisualDebuggerSettingsComponent.class);

    // Inject the mocked component into the configurable via reflection.
    Field field = VisualDebuggerSettingsConfigurable.class.getDeclaredField("settingsComponent");
    field.setAccessible(true);
    field.set(configurable, component);

    // Set up sensible defaults for fields that isModified / apply / reset use.
    // PluginSettingsState.getInstance() always returns a fresh default instance in test context.
    when(component.getVisualizationDepthText()).thenReturn("3");
    when(component.getSavedDebugStepsText()).thenReturn("3");
    when(component.getDebuggingVisualizerOptionChoice())
        .thenReturn(DebuggingVisualizerOption.WEB_UI);
    when(component.getColoredDiffValue()).thenReturn(true);
    when(component.getShowNullValues()).thenReturn(false);
    when(component.getUiServerPortText()).thenReturn("8070");
    when(component.getApiServerPortText()).thenReturn("8071");
  }

  // --- isModified / isPortModified ---

  @Test
  void isNotModifiedWhenPortsMatchDefaults() {
    assertFalse(configurable.isModified());
  }

  @Test
  void isModifiedWhenUiPortDiffers() {
    when(component.getUiServerPortText()).thenReturn("9999");
    assertTrue(configurable.isModified());
  }

  @Test
  void isModifiedWhenApiPortDiffers() {
    when(component.getApiServerPortText()).thenReturn("9999");
    assertTrue(configurable.isModified());
  }

  // --- apply ---

  @Test
  void applyParsesAndWritesPorts() {
    when(component.getUiServerPortText()).thenReturn("9090");
    when(component.getApiServerPortText()).thenReturn("9091");

    // apply() exercises Integer.parseInt on the port text and writes to a PluginSettingsState.
    // In test context getInstance() returns ephemeral instances, so we verify the code path runs
    // without error and the component getters were invoked.
    assertDoesNotThrow(() -> configurable.apply());
    verify(component).getUiServerPortText();
    verify(component).getApiServerPortText();
  }

  // --- reset ---

  @Test
  void resetWritesDefaultPortsToComponent() {
    // reset() reads from PluginSettingsState.getInstance() (returns defaults: 8070, 8071)
    // and writes them into the component.
    configurable.reset();

    verify(component).setUiServerPortText("8070");
    verify(component).setApiServerPortText("8071");
  }
}
