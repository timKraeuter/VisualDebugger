package no.hvl.tk.visual.debugger.server

import com.intellij.openapi.diagnostic.Logger
import jakarta.websocket.Session
import java.io.IOException
import no.hvl.tk.visual.debugger.server.endpoint.VisualDebuggingAPIEndpoint
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage
import no.hvl.tk.visual.debugger.settings.PluginSettingsState.Companion.settings
import org.glassfish.tyrus.server.Server

/**
 * This class can start a websocket server which runs an API that provides the client with live
 * debug data. See [VisualDebuggingAPIEndpoint] for the Endpoint.
 */
object VisualDebuggingAPIServerStarter {
  private val LOGGER = Logger.getInstance(VisualDebuggingAPIServerStarter::class.java)

  @JvmStatic
  fun runNewServer(): Server? {
    val server =
        Server(
            ServerConstants.HOST_NAME,
            ServerConstants.VISUAL_DEBUGGING_API_SERVER_PORT,
            "",
            HashMap(),
            VisualDebuggingAPIEndpoint::class.java)
    try {
      server.start()
      LOGGER.info("Debug API server started successfully.")
      return server
    } catch (e: Exception) {
      LOGGER.error(e)
      return null
    }
  }

  /**
   * Sends the given message to the given client, if the client is not null.
   *
   * @param client client.
   * @param message message for the client.
   */
  @JvmStatic
  fun sendMessageToClient(client: Session, message: String) {
      try {
        client.basicRemote.sendText(message)
      } catch (e: IOException) {
        LOGGER.error(e)
      }
  }

  /**
   * Sends the configuration to the client.
   *
   * @param client client
   */
  @JvmStatic
  fun sendUIConfig(client: Session) {
    val uiConfig = settings.uIConfig
    val configMessage = DebuggingWSMessage(DebuggingMessageType.CONFIG, uiConfig.serialize())

    sendMessageToClient(client, configMessage.serialize())
  }
}
