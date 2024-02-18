package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.jdi.LocalVariableProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import java.util.List;

public interface IStackFrame {

  ObjectReference thisObject() throws EvaluateException;

  List<LocalVariableProxyImpl> visibleVariables() throws EvaluateException;

  Value getValue(LocalVariableProxyImpl localVariable) throws EvaluateException;

  ThreadReferenceProxyImpl threadProxy();
}
