package no.hvl.tk.visual.debugger

import jakarta.websocket.Session
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListener
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.tyrus.server.Server

object SharedState {
    @JvmField
    val manuallyExploredObjects: Set<String> = HashSet()

    // UI / Debug API related
    @JvmStatic
    var uiServer: HttpServer? = null
        private set
    @JvmField
    var debugAPIServer: Server? = null

    /** All currently connected websocket client which will get updated.  */
    private val websocketClients: MutableSet<Session> = HashSet()

    /** Last diagram XML for newly connecting clients.  */
    @JvmField
    var lastDiagramXML: String = ""

    @JvmField
    var debugFileName: String? = null
    @JvmField
    var debugLine: Int? = null

    @JvmField
    var debuggingActive: Boolean = false

    @JvmField
    var embeddedBrowserActive: Boolean = false

    @JvmField
    var debugListener: StackFrameSessionListener? = null

    /** Last plant UML diagram input needed for the print function.  */
    @JvmField
    var lastPlantUMLDiagram: String = ""

    @JvmStatic
    fun getWebsocketClients(): Set<Session> {
        return websocketClients
    }

    @JvmStatic
    fun addWebsocketClient(clientSession: Session) {
        websocketClients.add(clientSession)
    }

    @JvmStatic
    fun removeWebsocketClient(clientSession: Session) {
        websocketClients.remove(clientSession)
    }

    @JvmStatic
    fun setUIServer(server: HttpServer?) {
        uiServer = server
    }
}
