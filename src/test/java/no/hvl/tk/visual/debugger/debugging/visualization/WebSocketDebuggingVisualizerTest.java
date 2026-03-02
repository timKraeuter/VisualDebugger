package no.hvl.tk.visual.debugger.debugging.visualization;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.websocket.Session;
import java.io.IOException;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebSocketDebuggingVisualizerTest {

  private WebSocketDebuggingVisualizer visualizer;

  @BeforeEach
  void setUp() {
    visualizer = new WebSocketDebuggingVisualizer(new JPanel());
  }

  @AfterEach
  void tearDown() {
    // Clean up shared state.
    SharedState.clearWebsocketClients();
    SharedState.setUIServer(null);
    SharedState.setDebugAPIServer(null);
    SharedState.setEmbeddedBrowserActive(false);
  }

  @Test
  void debuggingDeactivatedClearsWebsocketClients() throws IOException {
    Session session = mock(Session.class);
    when(session.isOpen()).thenReturn(true);
    SharedState.addWebsocketClient(session);

    visualizer.debuggingDeactivated();

    verify(session).close();
    assertThat(SharedState.getWebsocketClients().size(), is(0));
  }

  @Test
  void debuggingDeactivatedStopsUIServer() {
    HttpServer uiServer = mock(HttpServer.class);
    SharedState.setUIServer(uiServer);

    visualizer.debuggingDeactivated();

    verify(uiServer).shutdownNow();
    assertNull(SharedState.getUiServer());
  }

  @Test
  void debuggingDeactivatedStopsDebugAPIServer() {
    Server apiServer = mock(Server.class);
    SharedState.setDebugAPIServer(apiServer);

    visualizer.debuggingDeactivated();

    verify(apiServer).stop();
    assertNull(SharedState.getDebugAPIServer());
  }

  @Test
  void debuggingDeactivatedResetsEmbeddedBrowserFlag() {
    SharedState.setEmbeddedBrowserActive(true);

    visualizer.debuggingDeactivated();

    assertFalse(SharedState.isEmbeddedBrowserActive());
  }

  @Test
  void debuggingDeactivatedHandlesNullServersGracefully() {
    // No servers set — should not throw.
    SharedState.setUIServer(null);
    SharedState.setDebugAPIServer(null);

    assertDoesNotThrow(() -> visualizer.debuggingDeactivated());
  }
}
