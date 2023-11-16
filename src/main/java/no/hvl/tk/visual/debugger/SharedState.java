package no.hvl.tk.visual.debugger;

import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListener;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import java.util.HashSet;
import java.util.Set;

public class SharedState {


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
    private static String lastDiagramXML = "";
    private static String debugFileName;
    private static Integer debugLine;

    private static boolean debuggingActive = false;
    private static StackFrameSessionListener debugSessionListener;

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
        return lastDiagramXML;
    }

    public static void setLastDiagramXML(final String diagramXML) {
        SharedState.lastDiagramXML = diagramXML;
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
}
