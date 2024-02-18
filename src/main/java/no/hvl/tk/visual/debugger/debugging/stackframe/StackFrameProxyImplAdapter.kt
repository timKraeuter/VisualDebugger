package no.hvl.tk.visual.debugger.debugging.stackframe

import com.intellij.debugger.engine.evaluation.EvaluateException
import com.intellij.debugger.jdi.LocalVariableProxyImpl
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl
import com.sun.jdi.ObjectReference
import com.sun.jdi.Value

class StackFrameProxyImplAdapter(private val stackFrameProxy: StackFrameProxyImpl) : IStackFrame {
  @Throws(EvaluateException::class)
  override fun thisObject(): ObjectReference {
    return stackFrameProxy.thisObject()!!
  }

  @Throws(EvaluateException::class)
  override fun visibleVariables(): List<LocalVariableProxyImpl> {
    return stackFrameProxy.visibleVariables()
  }

  @Throws(EvaluateException::class)
  override fun getValue(localVariable: LocalVariableProxyImpl): Value {
    return stackFrameProxy.getValue(localVariable)
  }

  override fun threadProxy(): ThreadReferenceProxyImpl {
    return stackFrameProxy.threadProxy()
  }
}
