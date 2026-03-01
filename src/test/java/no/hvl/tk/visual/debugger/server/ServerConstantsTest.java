package no.hvl.tk.visual.debugger.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ServerConstantsTest {

  @Test
  void getUiServerPortReturnsDefault() {
    // Without an IntelliJ application context, getInstance() returns a fresh PluginSettingsState
    // with default port values.
    assertThat(ServerConstants.getUiServerPort(), is(ServerConstants.DEFAULT_UI_SERVER_PORT));
  }

  @Test
  void getApiServerPortReturnsDefault() {
    assertThat(ServerConstants.getApiServerPort(), is(ServerConstants.DEFAULT_API_SERVER_PORT));
  }

  @Test
  void getUiServerUrlContainsHostAndPorts() {
    String url = ServerConstants.getUiServerUrl();
    assertThat(
        url,
        is(
            String.format(
                "http://%s:%s?serverPort=%s",
                ServerConstants.HOST_NAME,
                ServerConstants.DEFAULT_UI_SERVER_PORT,
                ServerConstants.DEFAULT_API_SERVER_PORT)));
  }

  @Test
  void getUiServerUrlEmbeddedContainsEmbeddedFlag() {
    String url = ServerConstants.getUiServerUrlEmbedded();
    assertThat(
        url,
        is(
            String.format(
                "http://%s:%s?embedded=true&serverPort=%s",
                ServerConstants.HOST_NAME,
                ServerConstants.DEFAULT_UI_SERVER_PORT,
                ServerConstants.DEFAULT_API_SERVER_PORT)));
  }

  @Test
  void getUiServerUrlEmbeddedContainsServerPortParam() {
    String url = ServerConstants.getUiServerUrlEmbedded();
    assertTrue(
        url.contains("serverPort=" + ServerConstants.DEFAULT_API_SERVER_PORT),
        "Embedded URL must include serverPort query parameter");
  }

  @Test
  void defaultPortsAreInValidRange() {
    assertTrue(ServerConstants.DEFAULT_UI_SERVER_PORT >= 1024);
    assertTrue(ServerConstants.DEFAULT_UI_SERVER_PORT <= 65535);
    assertTrue(ServerConstants.DEFAULT_API_SERVER_PORT >= 1024);
    assertTrue(ServerConstants.DEFAULT_API_SERVER_PORT <= 65535);
  }
}
