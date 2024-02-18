package no.hvl.tk.visual.debugger

import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebuggerManagerListener
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListener

class DebugProcessListener : XDebuggerManagerListener {
  override fun processStarted(debugProcess: XDebugProcess) {
    val debugSession = debugProcess.session
    debugSession.addSessionListener(StackFrameSessionListener(debugProcess))
  }
}
