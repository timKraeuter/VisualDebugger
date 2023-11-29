package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.web.DownloadHandler;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
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

  private final Project project;
  private final JPanel debugUI;

  public WebSocketDebuggingVisualizer(Project project, final JPanel userInterface) {
    this.project = project;
    this.debugUI = userInterface;
  }

  @Override
  public void doVisualizationFurther(ObjectDiagram diagram) {
    if (SharedState.getDebugAPIServer() == null) {
      return;
    }
    final String diagramXML = DiagramToXMLConverter.toXml(diagram);
    SharedState.setLastDiagramXML(diagramXML);

    final String message =
        new DebuggingWSMessage(
                DebuggingMessageType.NEXT_DEBUG_STEP,
                diagramXML,
                SharedState.getDebugFileName(),
                SharedState.getDebugLine())
            .serialize();
    SharedState.getWebsocketClients()
        .forEach(
            clientSession ->
                // If one client fails no more messages are sent. We should change this.
                VisualDebuggingAPIServerStarter.sendMessageToClient(clientSession, message));
  }

  @Override
  public void debuggingActivated() {
    WebSocketDebuggingVisualizer.startDebugAPIServerIfNeeded();
    WebSocketDebuggingVisualizer.startUIServerIfNeeded();

    JBCefBrowser browser = new JBCefBrowser(ServerConstants.UI_SERVER_URL);
    browser
        .getJBCefClient()
        .addDownloadHandler(new DownloadHandler(project), browser.getCefBrowser());
    browser.setPageBackgroundColor("white");
    debugUI.add(browser.getComponent());
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
