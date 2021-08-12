package no.hvl.tk.visual.debugger;

import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.debugging.DebugListener;
import org.glassfish.tyrus.server.Server;

import java.util.HashSet;
import java.util.Set;

public class SharedState {


    private SharedState() {
    }

    // Websocket related
    private static Server server;
    private static final Set<Session> websocketClients = new HashSet<>();

    private static boolean debuggingActive = false;

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private static DebugListener debugListener;
    private static int visualizationDepth = 5;
    private static String lastPlantUMLDiagram = "";
    private static String diagramXML = "";

    public static int getVisualizationDepth() {
        return visualizationDepth;
    }

    public static void setVisualizationDepth(final int visualizationDepth) {
        SharedState.visualizationDepth = visualizationDepth;
    }

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

    public static Server getServer() {
        return server;
    }

    public static void setServer(final Server server) {
        SharedState.server = server;
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

}
