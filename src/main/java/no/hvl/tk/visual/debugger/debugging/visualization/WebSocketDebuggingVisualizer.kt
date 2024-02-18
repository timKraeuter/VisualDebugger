package no.hvl.tk.visual.debugger.debugging.visualization

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.jcef.JBCefBrowser
import jakarta.websocket.Session
import java.awt.BorderLayout
import java.util.function.Consumer
import javax.swing.JButton
import javax.swing.JPanel
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.SharedState.getWebsocketClients
import no.hvl.tk.visual.debugger.SharedState.setUIServer
import no.hvl.tk.visual.debugger.SharedState.uiServer
import no.hvl.tk.visual.debugger.debugging.visualization.cef.SimpleDownloadHandler
import no.hvl.tk.visual.debugger.domain.ObjectDiagram
import no.hvl.tk.visual.debugger.server.ServerConstants
import no.hvl.tk.visual.debugger.server.UIServerStarter
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter
import no.hvl.tk.visual.debugger.server.VisualDebuggingAPIServerStarter.sendMessageToClient
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage
import no.hvl.tk.visual.debugger.util.ClassloaderUtil.runWithContextClassloader
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter.toXml

/** Sends visualization information through websocket. */
class WebSocketDebuggingVisualizer(private val debugUI: JPanel) : DebuggingInfoVisualizerBase() {
  private var browser: JBCefBrowser? = null

  public override fun doVisualizationFurther(diagram: ObjectDiagram) {
    if (SharedState.debugAPIServer == null) {
      return
    }
    val diagramXML = toXml(diagram)
    SharedState.lastDiagramXML = diagramXML

    val message =
        DebuggingWSMessage(
                DebuggingMessageType.NEXT_DEBUG_STEP,
                diagramXML,
                SharedState.debugFileName,
                SharedState.debugLine)
            .serialize()
    getWebsocketClients()
        .forEach(
            Consumer { clientSession: Session?
              -> // If one client fails no more messages are sent. We should change this.
              sendMessageToClient(clientSession!!, message)
            })
  }

  override fun debuggingActivated() {
    startDebugAPIServerIfNeeded()
    startUIServerIfNeeded()

    initUI()
  }

  private fun initUI() {
    val launchEmbeddedBrowserButton = JButton("Launch embedded browser (experimental)")
    val launchBrowserButton =
        JButton(String.format("Launch browser (%s)", ServerConstants.UI_SERVER_URL))
    launchEmbeddedBrowserButton.addActionListener {
      debugUI.remove(launchEmbeddedBrowserButton)
      debugUI.remove(launchBrowserButton)
      launchEmbeddedBrowser()
    }
    launchBrowserButton.addActionListener { BrowserUtil.browse(ServerConstants.UI_SERVER_URL) }
    if (SharedState.embeddedBrowserActive) {
      launchEmbeddedBrowser()
    } else {
      debugUI.add(launchEmbeddedBrowserButton, BorderLayout.NORTH)
      debugUI.add(launchBrowserButton, BorderLayout.SOUTH)
    }
  }

  private fun launchEmbeddedBrowser() {
    if (browser == null) {
      browser = JBCefBrowser()
      browser!!.jbCefClient.cefClient.addDownloadHandler(SimpleDownloadHandler())
      browser!!.setPageBackgroundColor("white")
    }
    browser!!.loadURL(ServerConstants.UI_SERVER_URL_EMBEDDED)
    debugUI.add(browser!!.component, 0)
    SharedState.embeddedBrowserActive = true
    debugUI.revalidate()
  }

  override fun debuggingDeactivated() {
    stopUIServerIfNeeded()
    stopDebugAPIServerIfNeeded()
  }

  companion object {
    private val LOGGER = Logger.getInstance(WebSocketDebuggingVisualizer::class.java)

    private fun startDebugAPIServerIfNeeded() {
      runWithContextClassloader<Any?> {
        if (SharedState.debugAPIServer == null) {
          val server = VisualDebuggingAPIServerStarter.runNewServer()
          SharedState.debugAPIServer = server
        }
        null // needed because of generic method.
      }
    }

    private fun startUIServerIfNeeded() {
      runWithContextClassloader<Any?> {
        if (uiServer == null) {
          val server = UIServerStarter.runNewServer()
          setUIServer(server)
        }
        null // needed because of generic method.
      }
    }

    private fun stopUIServerIfNeeded() {
      val server = uiServer
      if (server != null) {
        server.shutdownNow()
        LOGGER.info("UI server stopped.")
        setUIServer(null)
      }
    }

    private fun stopDebugAPIServerIfNeeded() {
      val server = SharedState.debugAPIServer
      if (server != null) {
        server.stop()
        LOGGER.info("Debug API server stopped.")
        SharedState.debugAPIServer = null
      }
    }
  }
}
