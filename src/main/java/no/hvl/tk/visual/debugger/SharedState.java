package no.hvl.tk.visual.debugger;

import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.debugging.DebugListener;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import java.util.HashSet;
import java.util.Set;

public class SharedState {


    private SharedState() {
    }

    // UI
    private static HttpServer uiServer;
    // Websocket related
    private static Server debugAPIServer;
    private static final Set<Session> websocketClients = new HashSet<>();

    private static boolean debuggingActive = false;

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private static DebugListener debugListener;
    private static String lastPlantUMLDiagram = "";
    private static String diagramXML = "";

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

    public static DebugListener getDebugListener() {
        return debugListener;
    }

    public static void setDebugListener(final DebugListener debugListener) {
        SharedState.debugListener = debugListener;
    }

    public static Server getDebugAPIServer() {
        return debugAPIServer;
    }

    public static void setDebugAPIServer(final Server debugAPIServer) {
        SharedState.debugAPIServer = debugAPIServer;
    }

    public static Set<Session> getWebsocketClients() {
        return websocketClients;
    }

    public static void addWebsocketClient(final Session clientSession) {
        websocketClients.add(clientSession);
    }

    public static void removeWebsocketClient(final Session clientSession) {
        websocketClients.remove(clientSession);
    }

    public static String getLastDiagramXML() {
        return diagramXML;
    }

    public static void setLastDiagramXML(final String diagramXML) {
        SharedState.diagramXML = diagramXML;
    }

    public static void setUIServer(final HttpServer server) {
        SharedState.uiServer = server;
    }

    public static HttpServer getUiServer() {
        return uiServer;
    }
}
