package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.Session;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.util.ClassloaderUtil;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Sends visualization information through websocket.
 */
public class WebSocketDebuggingVisualizer extends DebuggingInfoVisualizerBase {
    private static final Logger LOGGER = Logger.getInstance(WebSocketDebuggingVisualizer.class);

    private JAXBContext jaxbContext;
    private Marshaller jaxbMarshaller;

    @Override
    public void finishVisualization() {
        if (SharedState.getServer() == null) {
            return;
        }
        SharedState.getWebsocketClients().forEach(clientSession -> {
            // If one client fails no more messages are sent. We should change this.
            WebSocketDebuggingVisualizer.sendMessageToClient(clientSession, this.transformDiagramToXML());
        });
    }

    private String transformDiagramToXML() {
        return ClassloaderUtil.runWithContextClassloader(() -> {
            this.createJAXBObjectsIfNeeded();
            return this.marshallDiagram(this.diagram);
        });
    }

    private String marshallDiagram(final ObjectDiagram mockDiagram) {
        final StringWriter sw = new StringWriter();
        try {
            this.jaxbMarshaller.marshal(mockDiagram, sw);
        } catch (final JAXBException e) {
            LOGGER.error(e);
        }
        return sw.toString();
    }

    private void createJAXBObjectsIfNeeded() {
        if (this.jaxbContext == null) {
            try {
                this.jaxbContext = JAXBContext.newInstance(ObjectDiagram.class);
            } catch (final JAXBException e) {
                LOGGER.error(e);
            }
        }
        if (this.jaxbMarshaller == null) {
            try {
                this.jaxbMarshaller = this.jaxbContext.createMarshaller();
                this.jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            } catch (final JAXBException e) {
                LOGGER.error(e);
            }
        }
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
