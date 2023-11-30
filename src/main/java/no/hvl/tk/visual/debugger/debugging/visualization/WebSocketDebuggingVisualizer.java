package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.jcef.JCefDownloadHandler;
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

/**
 * Sends visualization information through websocket.
 */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {

  private static final Logger LOGGER = Logger.getInstance(WebSocketDebuggingVisualizer.class);

  private final Project project;
  private JBCefBrowser browser;
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

    final var uiButton =
        new JButton(String.format("Launch browser (%s)", ServerConstants.UI_SERVER_URL));
    uiButton.addActionListener(e -> BrowserUtil.browse(ServerConstants.UI_SERVER_URL));
    this.debugUI.add(uiButton);

    final var openEmbeddedBrowserButton = new JButton("Launch embedded browser (experimental)");
    final var closeEmbeddedBrowserButton = new JButton("Close embedded browser");
    openEmbeddedBrowserButton.addActionListener(
        e -> {
          this.debugUI.remove(openEmbeddedBrowserButton);
          this.debugUI.add(closeEmbeddedBrowserButton);
          launchEmbeddedBrowser();
          this.debugUI.revalidate();
        });
    this.debugUI.add(openEmbeddedBrowserButton);

    closeEmbeddedBrowserButton.addActionListener(
        e -> {
          this.debugUI.remove(browser.getComponent());
          this.debugUI.remove(closeEmbeddedBrowserButton);
          this.debugUI.add(openEmbeddedBrowserButton);
          this.debugUI.revalidate();
        });
  }

  private void launchEmbeddedBrowser() {
    if (browser == null) {
      browser = new JBCefBrowser();
      browser
          .getJBCefClient()
          .addDownloadHandler(new JCefDownloadHandler(project), browser.getCefBrowser());
      browser.setPageBackgroundColor("white");
    }
    browser.loadURL(ServerConstants.UI_SERVER_URL);
    debugUI.add(browser.getComponent(), 0);
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
