package no.hvl.tk.visual.debugger;

import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.debugging.DebugSessionListener;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import java.util.HashSet;
import java.util.Set;

public class SharedState {

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private SharedState() {
    }

    // UI / Debug API related
    private static HttpServer uiServer;
    private static Server debugAPIServer;
    /**
     * All currently connected websocket client which will get updated.
     */
    private static final Set<Session> websocketClients = new HashSet<>();
    /**
     * Last diagram XML for newly connecting clients.
     */
    private static String diagramXML = "";

    private static boolean debuggingActive = false;
    private static DebugSessionListener debugSessionListener;

    /**
     * Last plant UML diagram input needed for the print function.
     */
    private static String lastPlantUMLDiagram = "";

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

    public static DebugSessionListener getDebugListener() {
        return debugSessionListener;
    }

    public static void setDebugListener(final DebugSessionListener debugSessionListener) {
        SharedState.debugSessionListener = debugSessionListener;
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
