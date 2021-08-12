package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.util.Pair;
import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter;
import no.hvl.tk.visual.debugger.webAPI.endpoint.message.TypedWebsocketMessage;
import no.hvl.tk.visual.debugger.webAPI.endpoint.message.WebsocketMessageType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends visualization information through websocket.
 */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {

    private final Map<String, Pair<ODObject, JavaValue>> objectIDToJValue = new HashMap<>();

    @Override
    protected void handleObjectAndJavaValue(final ODObject object, final JavaValue jValue) {
        this.objectIDToJValue.put(object.getId(), Pair.create(object, jValue));
    }

    @Override
    public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(final String objectId) {
        return this.objectIDToJValue.get(objectId);
    }

    @Override
    public void finishVisualization() {
        if (SharedState.getServer() == null) {
            return;
        }
        final String message = new TypedWebsocketMessage(
                WebsocketMessageType.NEXT_DEBUG_STEP,
                DiagramToXMLConverter.toXml(this.diagram)).serialize();
        SharedState.getWebsocketClients().forEach(clientSession -> {
            // If one client fails no more messages are sent. We should change this.
            WebSocketDebuggingVisualizer.sendMessageToClient(clientSession, message);
        });
        // Reset diagram
        this.diagram = new ObjectDiagram();
    }

    private static void sendMessageToClient(final Session client, final String message) {
        if (client != null) {
            try {
                client.getBasicRemote().sendText(message);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
