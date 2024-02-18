package no.hvl.tk.visual.debugger.server

import com.intellij.openapi.diagnostic.Logger
import java.io.IOException
import org.glassfish.grizzly.http.server.CLStaticHttpHandler
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener

object UIServerStarter {
  private val LOGGER = Logger.getInstance(UIServerStarter::class.java)

  @JvmStatic
  fun runNewServer(): HttpServer? {
    val server = HttpServer()
    val networkListener =
        NetworkListener("UI", ServerConstants.HOST_NAME, ServerConstants.UI_SERVER_PORT)
    server.addListener(networkListener)

    server.serverConfiguration.addHttpHandler(
        CLStaticHttpHandler(
            UIServerStarter::class.java.classLoader, ServerConstants.STATIC_RESOURCE_PATH),
        "/")
    try {
      server.start()
      LOGGER.info("UI server started successfully.")
      return server
    } catch (e: IOException) {
      LOGGER.error(e)
      return null
    }
  }
}
