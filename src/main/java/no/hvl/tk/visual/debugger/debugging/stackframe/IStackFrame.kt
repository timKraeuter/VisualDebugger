package no.hvl.tk.visual.debugger.debugging.stackframe

import com.intellij.debugger.engine.evaluation.EvaluateException
import com.intellij.debugger.jdi.LocalVariableProxyImpl
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl
import com.sun.jdi.ObjectReference
import com.sun.jdi.Value

interface IStackFrame {
  @Throws(EvaluateException::class) fun thisObject(): ObjectReference

  @Throws(EvaluateException::class) fun visibleVariables(): List<LocalVariableProxyImpl>

  @Throws(EvaluateException::class) fun getValue(localVariable: LocalVariableProxyImpl): Value

  fun threadProxy(): ThreadReferenceProxyImpl?
}
