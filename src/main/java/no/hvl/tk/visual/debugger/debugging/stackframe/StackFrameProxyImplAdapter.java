package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.jdi.LocalVariableProxyImpl;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import java.util.List;

public class StackFrameProxyImplAdapter implements
    IStackFrame {

  private final StackFrameProxyImpl stackFrameProxy;

  public StackFrameProxyImplAdapter(StackFrameProxyImpl stackFrameProxy) {
    this.stackFrameProxy = stackFrameProxy;
  }


  @Override
  public ObjectReference thisObject() throws EvaluateException {
    return stackFrameProxy.thisObject();
  }

  @Override
  public List<LocalVariableProxyImpl> visibleVariables() throws EvaluateException {
    return stackFrameProxy.visibleVariables();
  }

  @Override
  public Value getValue(LocalVariableProxyImpl localVariable) throws EvaluateException {
    return stackFrameProxy.getValue(localVariable);
  }

  @Override
  public ThreadReferenceProxyImpl threadProxy() {
    return stackFrameProxy.threadProxy();
  }
}
