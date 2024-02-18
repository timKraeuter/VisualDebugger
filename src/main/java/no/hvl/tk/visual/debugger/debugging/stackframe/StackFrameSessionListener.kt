package no.hvl.tk.visual.debugger.debugging.stackframe

import com.intellij.debugger.engine.JavaStackFrame
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Key
import com.intellij.util.ui.UIUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel
import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.debugging.stackframe.exceptions.StackFrameAnalyzerException
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer
import no.hvl.tk.visual.debugger.debugging.visualization.WebSocketDebuggingVisualizer
import no.hvl.tk.visual.debugger.settings.DebuggingVisualizerOption
import no.hvl.tk.visual.debugger.settings.PluginSettingsState.Companion.settings
import no.hvl.tk.visual.debugger.ui.VisualDebuggerIcons

class StackFrameSessionListener(debugProcess: XDebugProcess) : XDebugSessionListener {
  private var userInterface: JPanel? = null

  private val debugSession: XDebugSession = debugProcess.session
  private var debuggingVisualizer: DebuggingInfoVisualizer? = null

  init {
    debugProcess.processHandler.addProcessListener(
        object : ProcessListener {
          override fun startNotified(event: ProcessEvent) {
            this@StackFrameSessionListener.initUIIfNeeded()
          }

          override fun processTerminated(event: ProcessEvent) {
            // not relevant
          }

          override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
            // not relevant
          }
        })
    SharedState.debugListener = this
  }

  override fun sessionStopped() {
    debuggingVisualizer!!.sessionStopped()
  }

  override fun sessionPaused() {
    this.startVisualDebugging()
  }

  private fun startVisualDebugging() {
    if (!SharedState.debuggingActive) {
      return
    }
    val stackFrame = stackFrameProxy

    val stackFrameAnalyzer =
        StackFrameAnalyzer(
            StackFrameProxyImplAdapter(stackFrame),
            settings.visualisationDepth,
            SharedState.manuallyExploredObjects,
            settings.isShowNullValues)

    if (debugSession.currentPosition != null) {
      val fileName = debugSession.currentPosition!!.file.nameWithoutExtension
      val line = debugSession.currentPosition!!.line + 1
      debuggingVisualizer!!.addMetadata(fileName, line, stackFrameAnalyzer)
    }

    debuggingVisualizer!!.doVisualization(stackFrameAnalyzer.analyze())
  }

  private val stackFrameProxy: StackFrameProxyImpl
    get() {
      val currentStackFrame =
          debugSession.currentStackFrame as JavaStackFrame?
              ?: throw StackFrameAnalyzerException("Current stack frame could not be found!")

      return currentStackFrame.stackFrameProxy
    }

  private fun initUIIfNeeded() {
    if (this.userInterface != null) {
      return
    }
    this.userInterface = JPanel()
    userInterface!!.layout = BorderLayout()
    this.getOrCreateDebuggingInfoVisualizer() // make sure visualizer is initialized
    if (!SharedState.debuggingActive) {
      this.resetUIAndAddActivateDebuggingButton()
    } else {
      debuggingVisualizer!!.debuggingActivated()
    }
    val uiContainer = SimpleToolWindowPanel(false, true)

    val actionManager = ActionManager.getInstance()
    val actionToolbar =
        actionManager.createActionToolbar(
            TOOLBAR_ACTION, (actionManager.getAction(TOOLBAR_ACTION) as DefaultActionGroup), false)
    actionToolbar.targetComponent = userInterface
    uiContainer.toolbar = actionToolbar.component
    uiContainer.setContent(userInterface!!)

    val ui = debugSession.ui
    val content =
        ui.createContent(
            CONTENT_ID, uiContainer, "Visual Debugger", VisualDebuggerIcons.VD_ICON, null)
    content.isCloseable = false
    UIUtil.invokeLaterIfNeeded { ui.addContent(content) }
    LOGGER.debug("UI initialized!")
  }

  fun resetUIAndAddActivateDebuggingButton() {
    userInterface!!.removeAll()
    SharedState.embeddedBrowserActive = false
    userInterface!!.layout = BorderLayout()

    val activateButton = JButton("Activate visual debugger")
    activateButton.addActionListener {
      SharedState.debuggingActive = true
      userInterface!!.remove(activateButton)
      debuggingVisualizer!!.debuggingActivated()
      userInterface!!.revalidate()
    }
    userInterface!!.add(activateButton, BorderLayout.NORTH)

    userInterface!!.revalidate()
    userInterface!!.repaint()
  }

  fun getOrCreateDebuggingInfoVisualizer(): DebuggingInfoVisualizer {
    if (this.debuggingVisualizer == null) {
      when (settings.visualizerOption) {
        DebuggingVisualizerOption.WEB_UI ->
            this.debuggingVisualizer = WebSocketDebuggingVisualizer(userInterface!!)
        DebuggingVisualizerOption.EMBEDDED ->
            this.debuggingVisualizer = PlantUmlDebuggingVisualizer(userInterface!!)
      }
    }
    return debuggingVisualizer!!
  }

  fun reprintDiagram() {
    debuggingVisualizer!!.reprintDiagram()
  }

  companion object {
    private val LOGGER = Logger.getInstance(StackFrameSessionListener::class.java)

    // UI constants
    private const val CONTENT_ID = "no.hvl.tk.VisualDebugger"
    private const val TOOLBAR_ACTION =
        "VisualDebugger.VisualizerToolbar" // has to match with plugin.xml
  }
}
