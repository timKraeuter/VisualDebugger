package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.util.Pair;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter;
import no.hvl.tk.visual.debugger.webAPI.DebugAPIServerStarter;
import no.hvl.tk.visual.debugger.webAPI.endpoint.message.TypedWebsocketMessage;
import no.hvl.tk.visual.debugger.webAPI.endpoint.message.WebsocketMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends visualization information through websocket.
 */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {

    private Map<String, Pair<ODObject, JavaValue>> objectIDToJValue = new HashMap<>();

    @Override
    public DebuggingInfoVisualizer addDebugNodeForObject(final ODObject object, final JavaValue jValue) {
        this.objectIDToJValue.put(object.getId(), Pair.create(object, jValue));
        return null;
    }

    @Override
    public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(final String objectId) {
        return this.objectIDToJValue.get(objectId);
    }

    @Override
    public void finishVisualization() {
        if (SharedState.getDebugAPIServer() == null) {
            return;
        }
        final String diagramXML = DiagramToXMLConverter.toXml(this.diagram);
        SharedState.setLastDiagramXML(diagramXML);
        final String message = new TypedWebsocketMessage(
                WebsocketMessageType.NEXT_DEBUG_STEP,
                diagramXML).serialize();
        SharedState.getWebsocketClients().forEach(clientSession -> {
            // If one client fails no more messages are sent. We should change this.
            DebugAPIServerStarter.sendMessageToClient(clientSession, message);
        });
        // Reset diagram
        this.diagram = new ObjectDiagram();
    }

    @Override
    protected void preAddObject() {
        if (this.diagram.isEmpty()) {
            // reset debug node map.
            this.objectIDToJValue = new HashMap<>();
        }
    }
}
