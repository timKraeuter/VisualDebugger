package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefClient;
import com.intellij.ui.jcef.JBCefJSQuery;
import java.awt.BorderLayout;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.cef.BrowserDownloadHandler;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.server.ServerConstants;
import no.hvl.tk.visual.debugger.server.UIServerStarter;
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage;
import no.hvl.tk.visual.debugger.util.ClassloaderUtil;
import no.hvl.tk.visual.debugger.util.DiagramToJSONConverter;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

/** Sends visualization information through websocket. */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {

  private static final Logger LOGGER = Logger.getInstance(WebSocketDebuggingVisualizer.class);

  private JBCefBrowser browser;
  private JBCefClient browserClient;
  private JBCefJSQuery downloadQuery;
  private volatile String downloadInjectionScript;
  private final JPanel debugUI;

  public WebSocketDebuggingVisualizer(final JPanel userInterface) {
    this.debugUI = userInterface;
  }

  @Override
  public void doVisualizationFurther(ObjectDiagram diagram) {
    if (SharedState.getDebugAPIServer() == null) {
      return;
    }
    final String diagramJSON = DiagramToJSONConverter.toJSON(diagram);
    SharedState.setLastDiagramJSON(diagramJSON);

    final String message =
        new DebuggingWSMessage(
                DebuggingMessageType.NEXT_DEBUG_STEP,
                diagramJSON,
                SharedState.getDebugFileName(),
                SharedState.getDebugLine())
            .serialize();
    SharedState.getWebsocketClients()
        .forEach(
            clientSession ->
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
        new JButton(String.format("Launch browser (%s)", ServerConstants.getUiServerUrl()));
    launchEmbeddedBrowserButton.addActionListener(
        e -> {
          this.debugUI.remove(launchEmbeddedBrowserButton);
          this.debugUI.remove(launchBrowserButton);
          launchEmbeddedBrowser();
        });
    launchBrowserButton.addActionListener(
        e -> BrowserUtil.browse(ServerConstants.getUiServerUrl()));
    if (SharedState.isEmbeddedBrowserActive()) {
      launchEmbeddedBrowser();
    } else {
      this.debugUI.add(launchEmbeddedBrowserButton, BorderLayout.NORTH);
      this.debugUI.add(launchBrowserButton, BorderLayout.SOUTH);
    }
  }

  private void launchEmbeddedBrowser() {
    if (browser == null) {
      browserClient = JBCefApp.getInstance().createClient();
      // Register the load handler before the browser is created so the native callback is wired.
      // On every page load we (re-)inject the JavaScript that intercepts the download buttons.
      browserClient
          .getCefClient()
          .addLoadHandler(
              new CefLoadHandlerAdapter() {
                @Override
                public void onLoadEnd(
                    final CefBrowser cefBrowser, final CefFrame frame, final int httpStatusCode) {
                  final String script = downloadInjectionScript;
                  if (frame.isMain() && script != null) {
                    cefBrowser.executeJavaScript(script, cefBrowser.getURL(), 0);
                  }
                }
              });
      browser =
          JBCefBrowser.createBuilder()
              .setClient(browserClient)
              .setUrl(ServerConstants.getUiServerUrlEmbedded())
              .build();
      browser.setPageBackgroundColor("white");

      // Bridge diagram downloads from the browser to the IDE. JCEF does not reliably raise a
      // native download for the data: URLs used by the UI, so we intercept the click in JavaScript
      // and save the file through a regular IDE save dialog instead.
      downloadQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
      downloadQuery.addHandler(
          payload -> {
            BrowserDownloadHandler.saveDownload(payload, debugUI);
            return new JBCefJSQuery.Response("ok");
          });
      downloadInjectionScript =
          BrowserDownloadHandler.buildInjectionScript(downloadQuery.inject("vdPayload"));
    }
    browser.loadURL(ServerConstants.getUiServerUrlEmbedded());
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
    // Close and clear all websocket client sessions before stopping servers.
    SharedState.clearWebsocketClients();

    stopUIServerIfNeeded();
    stopDebugAPIServerIfNeeded();

    // Dispose the embedded browser to release native CEF resources.
    if (downloadQuery != null) {
      downloadQuery.dispose();
      downloadQuery = null;
    }
    downloadInjectionScript = null;
    if (browser != null) {
      browser.dispose();
      browser = null;
    }
    // The client is owned by us (passed explicitly to the builder), so dispose it too.
    if (browserClient != null) {
      browserClient.dispose();
      browserClient = null;
    }
    SharedState.setEmbeddedBrowserActive(false);
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
