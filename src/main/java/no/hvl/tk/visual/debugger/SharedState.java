package no.hvl.tk.visual.debugger;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListener;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

public class SharedState {

  private static final Logger LOGGER = Logger.getInstance(SharedState.class);

  private static final Set<String> manuallyExploredObjects = ConcurrentHashMap.newKeySet();

  private SharedState() {}

  // UI / Debug API related
  private static HttpServer uiServer;
  private static Server debugAPIServer;

  /** All currently connected websocket client which will get updated. */
  private static final Set<Session> websocketClients = ConcurrentHashMap.newKeySet();

  /** Last diagram JSON for newly connecting clients. */
  private static volatile String lastDiagramJSON = "";

  private static volatile String debugFileName;
  private static volatile Integer debugLine;

  private static volatile boolean debuggingActive = false;

  private static volatile boolean embeddedBrowserActive = false;
  private static StackFrameSessionListener debugSessionListener;

  /** Last plant UML diagram input needed for the print function. */
  private static volatile String lastPlantUMLDiagram = "";

  public static String getLastPlantUMLDiagram() {
    return lastPlantUMLDiagram;
  }

  public static void setLastPlantUMLDiagram(final String diagram) {
    lastPlantUMLDiagram = diagram;
  }

  public static boolean isDebuggingActive() {
    return debuggingActive;
  }

  public static void setDebuggingActive(final boolean debuggingActive) {
    SharedState.debuggingActive = debuggingActive;
  }

  public static StackFrameSessionListener getDebugListener() {
    return debugSessionListener;
  }

  public static void setDebugListener(final StackFrameSessionListener debugSessionListener) {
    SharedState.debugSessionListener = debugSessionListener;
  }

  public static Server getDebugAPIServer() {
    return debugAPIServer;
  }

  public static void setDebugAPIServer(final Server debugAPIServer) {
    SharedState.debugAPIServer = debugAPIServer;
  }

  /**
   * Returns a snapshot of the currently connected websocket clients. The returned set is
   * unmodifiable; use {@link #addWebsocketClient} and {@link #removeWebsocketClient} to mutate.
   */
  public static Set<Session> getWebsocketClients() {
    return Set.copyOf(websocketClients);
  }

  public static void addWebsocketClient(final Session clientSession) {
    websocketClients.add(clientSession);
  }

  public static void removeWebsocketClient(final Session clientSession) {
    websocketClients.remove(clientSession);
  }

  /** Closes all websocket client sessions and clears the set. */
  public static void clearWebsocketClients() {
    for (final Session client : websocketClients) {
      try {
        if (client.isOpen()) {
          client.close();
        }
      } catch (final IOException e) {
        LOGGER.warn("Failed to close websocket client session.", e);
      }
    }
    websocketClients.clear();
  }

  public static String getLastDiagramJSON() {
    return lastDiagramJSON;
  }

  public static void setLastDiagramJSON(final String diagramJSON) {
    SharedState.lastDiagramJSON = diagramJSON;
  }

  public static void setUIServer(final HttpServer server) {
    SharedState.uiServer = server;
  }

  public static HttpServer getUiServer() {
    return uiServer;
  }

  public static String getDebugFileName() {
    return debugFileName;
  }

  public static Integer getDebugLine() {
    return debugLine;
  }

  public static void setDebugFileName(String lastDebugFileName) {
    SharedState.debugFileName = lastDebugFileName;
  }

  public static void setDebugLine(Integer lastDebugLine) {
    SharedState.debugLine = lastDebugLine;
  }

  public static Set<String> getManuallyExploredObjects() {
    return manuallyExploredObjects;
  }

  public static boolean isEmbeddedBrowserActive() {
    return embeddedBrowserActive;
  }

  public static void setEmbeddedBrowserActive(boolean embeddedBrowserActive) {
    SharedState.embeddedBrowserActive = embeddedBrowserActive;
  }
}
