package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.server.ServerConstants;
import no.hvl.tk.visual.debugger.server.UIServerStarter;
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage;
import no.hvl.tk.visual.debugger.util.ClassloaderUtil;
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

/** Sends visualization information through websocket. */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {
  private static final Logger LOGGER = Logger.getInstance(WebSocketDebuggingVisualizer.class);

  private final JPanel debugUI;

  public WebSocketDebuggingVisualizer(final JPanel userInterface) {
    this.debugUI = userInterface;
  }

  @Override
  public void finishVisualization() {
    if (SharedState.getDebugAPIServer() == null) {
      return;
    }
    final String diagramXML = DiagramToXMLConverter.toXml(this.getDiagramWithDepth());

    SharedState.setLastDiagramXML(diagramXML);
    String debugFileName = getFileNameIfExists();
    SharedState.setDebugFileName(debugFileName);
    int debugLine = getLineIfExists();
    SharedState.setDebugLine(debugLine);

    final String message =
        new DebuggingWSMessage(
                DebuggingMessageType.NEXT_DEBUG_STEP, diagramXML, debugFileName, debugLine)
            .serialize();
    SharedState.getWebsocketClients()
        .forEach(
            clientSession ->
                // If one client fails no more messages are sent. We should change this.
                VisualDebuggingAPIServerStarter.sendMessageToClient(clientSession, message));
    this.resetDiagram();
  }

  @Override
  public void debuggingActivated() {
    WebSocketDebuggingVisualizer.startDebugAPIServerIfNeeded();
    WebSocketDebuggingVisualizer.startUIServerIfNeeded();
    final var uiButton =
        new JButton(String.format("Launch user interface (%s)", ServerConstants.UI_SERVER_URL));
    uiButton.addActionListener(e -> WebSocketDebuggingVisualizer.launchUIInBrowser());
    this.debugUI.add(uiButton);
  }

  private static void launchUIInBrowser() {
    try {
      BrowserUtil.browse(new URI(ServerConstants.UI_SERVER_URL));
    } catch (final URISyntaxException ex) {
      LOGGER.error(ex);
    }
  }

  private static void startDebugAPIServerIfNeeded() {
    ClassloaderUtil.runWithContextClassloader(
        () -> {
          if (SharedState.getDebugAPIServer() == null) {
            final Server server = VisualDebuggingAPIServerStarter.runNewServer();
            SharedState.setDebugAPIServer(server);
          }
          return null; // needed because of generic method.
        });
  }

  private static void startUIServerIfNeeded() {
    ClassloaderUtil.runWithContextClassloader(
        () -> {
          if (SharedState.getUiServer() == null) {
            final HttpServer server = UIServerStarter.runNewServer();
            SharedState.setUIServer(server);
          }
          return null; // needed because of generic method.
        });
  }

  @Override
  public void debuggingDeactivated() {
    stopUIServerIfNeeded();
    stopDebugAPIServerIfNeeded();
  }

  private static void stopUIServerIfNeeded() {
    final HttpServer server = SharedState.getUiServer();
    if (server != null) {
      server.shutdownNow();
      LOGGER.info("UI server stopped.");
      SharedState.setUIServer(null);
    }
  }

  private static void stopDebugAPIServerIfNeeded() {
    final Server server = SharedState.getDebugAPIServer();
    if (server != null) {
      server.stop();
      LOGGER.info("Debug API server stopped.");
      SharedState.setDebugAPIServer(null);
    }
  }
}
