package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.jcef.JBCefBrowser;
import java.awt.BorderLayout;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.cef.SimpleDownloadHandler;
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

  private JBCefBrowser browser;
  private final JPanel debugUI;

  public WebSocketDebuggingVisualizer(final JPanel userInterface) {
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

    initUI();
  }

  private void initUI() {
    final var launchEmbeddedBrowserButton = new JButton("Launch embedded browser (experimental)");
    final var launchBrowserButton =
        new JButton(String.format("Launch browser (%s)", ServerConstants.UI_SERVER_URL));
    launchEmbeddedBrowserButton.addActionListener(
        e -> {
          this.debugUI.remove(launchEmbeddedBrowserButton);
          this.debugUI.remove(launchBrowserButton);
          launchEmbeddedBrowser();
        });
    launchBrowserButton.addActionListener(e -> BrowserUtil.browse(ServerConstants.UI_SERVER_URL));
    if (SharedState.isEmbeddedBrowserActive()) {
      launchEmbeddedBrowser();
    } else {
      this.debugUI.add(launchEmbeddedBrowserButton, BorderLayout.NORTH);
      this.debugUI.add(launchBrowserButton, BorderLayout.SOUTH);
    }
  }

  private void launchEmbeddedBrowser() {
    if (browser == null) {
      browser = new JBCefBrowser();
      browser.getJBCefClient().getCefClient().addDownloadHandler(new SimpleDownloadHandler());
      browser.setPageBackgroundColor("white");
    }
    browser.loadURL(ServerConstants.UI_SERVER_URL);
    debugUI.add(browser.getComponent(), 0);
    SharedState.setEmbeddedBrowserActive(true);
    this.debugUI.revalidate();
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
