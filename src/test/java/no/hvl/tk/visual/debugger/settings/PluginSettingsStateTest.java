package no.hvl.tk.visual.debugger.settings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class PluginSettingsStateTest {

  @Test
  void defaultUiServerPort() {
    PluginSettingsState state = new PluginSettingsState();
    assertThat(state.getUiServerPort(), is(8070));
  }

  @Test
  void defaultApiServerPort() {
    PluginSettingsState state = new PluginSettingsState();
    assertThat(state.getApiServerPort(), is(8071));
  }

  @Test
  void setAndGetUiServerPort() {
    PluginSettingsState state = new PluginSettingsState();
    state.setUiServerPort(9090);
    assertThat(state.getUiServerPort(), is(9090));
  }

  @Test
  void setAndGetApiServerPort() {
    PluginSettingsState state = new PluginSettingsState();
    state.setApiServerPort(9091);
    assertThat(state.getApiServerPort(), is(9091));
  }

  @Test
  void getInstanceWithoutApplicationReturnsDefaults() {
    // When ApplicationManager.getApplication() is null (unit test context),
    // getInstance() returns a fresh instance with default values.
    PluginSettingsState instance = PluginSettingsState.getInstance();
    assertThat(instance.getUiServerPort(), is(8070));
    assertThat(instance.getApiServerPort(), is(8071));
  }
}
