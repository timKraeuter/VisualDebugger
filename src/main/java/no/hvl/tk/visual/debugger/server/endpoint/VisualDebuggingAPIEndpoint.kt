package no.hvl.tk.visual.debugger.server.endpoint

import com.intellij.openapi.diagnostic.Logger
import jakarta.websocket.OnClose
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.ServerEndpoint
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.SharedState.addWebsocketClient
import no.hvl.tk.visual.debugger.SharedState.removeWebsocketClient
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter.sendMessageToClient
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter.sendUIConfig
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter.toXml

@ServerEndpoint("/debug")
object VisualDebuggingAPIEndpoint {
  private val LOGGER = Logger.getInstance(VisualDebuggingAPIEndpoint::class.java)

  @OnOpen
  fun onOpen(session: Session) {
    LOGGER.info(String.format("Websocket session with id \"%s\" opened.", session.id))
    addWebsocketClient(session)

    sendUIConfig(session)

    // Send the last diagram xml to the newly connected client.
    val debugMessage =
        DebuggingWSMessage(
            DebuggingMessageType.NEXT_DEBUG_STEP,
            SharedState.lastDiagramXML,
            SharedState.debugFileName,
            SharedState.debugLine)
    sendMessageToClient(session, debugMessage.serialize())
  }

  @OnClose
  fun onClose(session: Session) {
    LOGGER.info(String.format("Websocket session with id \"%s\" closed.", session.id))
    removeWebsocketClient(session)
  }

  @OnMessage
  fun handleTextMessage(objectId: String?): String {
    LOGGER.debug(String.format("New websocket message with content \"%s\" received.", objectId))

    val debuggingInfoVisualizer = SharedState.debugListener!!.getOrCreateDebuggingInfoVisualizer()
    try {
      val diagram = debuggingInfoVisualizer.getObjectWithChildren(objectId)
      return DebuggingWSMessage(DebuggingMessageType.LOAD_CHILDREN, toXml(diagram)).serialize()
    } catch (e: NumberFormatException) {
      return DebuggingWSMessage(
              DebuggingMessageType.ERROR,
              String.format("Object id \"%s\" is not a number!", objectId))
          .serialize()
    }
  }
}
