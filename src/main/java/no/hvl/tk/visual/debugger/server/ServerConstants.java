package no.hvl.tk.visual.debugger.server;

import no.hvl.tk.visual.debugger.settings.PluginSettingsState;

public class ServerConstants {
  public static final String HOST_NAME = "localhost";

  public static final String STATIC_RESOURCE_PATH = "/ui/";

  /** Default ports used as fallbacks when settings are not available. */
  static final int DEFAULT_UI_SERVER_PORT = 8070;

  static final int DEFAULT_API_SERVER_PORT = 8071;

  /** Returns the configured UI server port, falling back to the default. */
  public static int getUiServerPort() {
    try {
      return PluginSettingsState.getInstance().getUiServerPort();
    } catch (final Exception e) {
      return DEFAULT_UI_SERVER_PORT;
    }
  }

  /** Returns the configured WebSocket API server port, falling back to the default. */
  public static int getApiServerPort() {
    try {
      return PluginSettingsState.getInstance().getApiServerPort();
    } catch (final Exception e) {
      return DEFAULT_API_SERVER_PORT;
    }
  }

  /** Returns the UI server URL with the WebSocket server port as a query parameter. */
  public static String getUiServerUrl() {
    return String.format(
        "http://%s:%s?serverPort=%s", HOST_NAME, getUiServerPort(), getApiServerPort());
  }

  /**
   * Returns the UI server URL for the embedded browser with the WebSocket server port as a query
   * parameter.
   */
  public static String getUiServerUrlEmbedded() {
    return String.format(
        "http://%s:%s?embedded=true&serverPort=%s",
        HOST_NAME, getUiServerPort(), getApiServerPort());
  }

  private ServerConstants() {
    // Only constants in this class
  }
}
