package no.hvl.tk.visual.debugger.server.endpoint;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter;
import no.hvl.tk.visual.debugger.server.endpoint.message.TypedWebsocketMessage;
import no.hvl.tk.visual.debugger.server.endpoint.message.WebsocketMessageType;
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter;

@ServerEndpoint("/debug")
public class VisualDebuggingAPIEndpoint {
    private static final Logger LOGGER = Logger.getInstance(VisualDebuggingAPIEndpoint.class);
    // One gets one instance of this class per session/client.


    public VisualDebuggingAPIEndpoint() {
        // Needs public constructor.
    }

    @OnOpen
    public static void onOpen(final Session session) {
        LOGGER.info(String.format("Websocket session with id \"%s\" opened.", session.getId()));
        SharedState.addWebsocketClient(session);

        // Send the last diagram xml to the newly connected client.
        final String message = new TypedWebsocketMessage(
                WebsocketMessageType.NEXT_DEBUG_STEP,
                SharedState.getLastDiagramXML()).serialize();
        VisualDebuggingAPIServerStarter.sendMessageToClient(session, message);
    }

    @OnClose
    public static void onClose(final Session session) {
        LOGGER.info(String.format("Websocket session with id \"%s\" closed.", session.getId()));
        SharedState.removeWebsocketClient(session);
    }

    @OnMessage
    public static String handleTextMessage(final String objectId) {
        LOGGER.debug(String.format("New websocket message with content \"%s\" received.", objectId));

        final DebuggingInfoVisualizer debuggingInfoVisualizer = SharedState.getDebugListener()
                                                                           .getOrCreateDebuggingInfoVisualizer();
        try {
            final ObjectDiagram diagram = debuggingInfoVisualizer.getObjectWithChildrenFromPreviousDiagram(objectId);
            return new TypedWebsocketMessage(
                    WebsocketMessageType.LOAD_CHILDREN,
                    DiagramToXMLConverter.toXml(diagram)).serialize();
        } catch (NumberFormatException e) {
            return new TypedWebsocketMessage(
                    WebsocketMessageType.ERROR,
                    String.format("Object id \"%s\" is not a number!", objectId)).serialize();
        }
    }
}