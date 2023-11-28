package no.hvl.tk.visual.debugger.server.endpoint;

import static no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter.sendUIConfig;

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
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage;
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter;

@ServerEndpoint("/debug")
public class VisualDebuggingAPIEndpoint {
  private static final Logger LOGGER = Logger.getInstance(VisualDebuggingAPIEndpoint.class);

  public VisualDebuggingAPIEndpoint() {
    // Needs public constructor.
  }

  @OnOpen
  public static void onOpen(final Session session) {
    LOGGER.info(String.format("Websocket session with id \"%s\" opened.", session.getId()));
    SharedState.addWebsocketClient(session);

    sendUIConfig(session);

    // Send the last diagram xml to the newly connected client.
    final DebuggingWSMessage debugMessage =
        new DebuggingWSMessage(
            DebuggingMessageType.NEXT_DEBUG_STEP,
            SharedState.getLastDiagramXML(),
            SharedState.getDebugFileName(),
            SharedState.getDebugLine());
    VisualDebuggingAPIServerStarter.sendMessageToClient(session, debugMessage.serialize());
  }

  @OnClose
  public static void onClose(final Session session) {
    LOGGER.info(String.format("Websocket session with id \"%s\" closed.", session.getId()));
    SharedState.removeWebsocketClient(session);
  }

  @OnMessage
  public static String handleTextMessage(final String objectId) {
    LOGGER.debug(String.format("New websocket message with content \"%s\" received.", objectId));

    final DebuggingInfoVisualizer debuggingInfoVisualizer =
        SharedState.getDebugListener().getOrCreateDebuggingInfoVisualizer();
    try {
      final ObjectDiagram diagram = debuggingInfoVisualizer.getObjectWithChildren(objectId);
      return new DebuggingWSMessage(
              DebuggingMessageType.LOAD_CHILDREN, DiagramToXMLConverter.toXml(diagram))
          .serialize();
    } catch (NumberFormatException e) {
      return new DebuggingWSMessage(
              DebuggingMessageType.ERROR,
              String.format("Object id \"%s\" is not a number!", objectId))
          .serialize();
    }
  }
}
