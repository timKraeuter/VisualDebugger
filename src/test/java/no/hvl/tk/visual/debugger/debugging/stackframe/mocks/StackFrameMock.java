package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.intellij.debugger.jdi.LocalVariableProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.sun.jdi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.hvl.tk.visual.debugger.debugging.stackframe.IStackFrame;
import org.mockito.Mockito;

public class StackFrameMock implements IStackFrame {

  private final ObjectReference thisObj;
  private final Map<LocalVariableProxyImpl, Value> localVars = new HashMap<>();

  public StackFrameMock(ObjectReference thisObj) {
    this.thisObj = thisObj;
  }

  public ObjectReference thisObject() {
    return thisObj;
  }

  public List<LocalVariableProxyImpl> visibleVariables() {
    return new ArrayList<>(localVars.keySet());
  }

  @Override
  public Value getValue(LocalVariableProxyImpl localVariable) {
    return this.localVars.get(localVariable);
  }

  public void setValue(LocalVariable variable, Value value) {
    LocalVariableProxyImpl mock = Mockito.mock(LocalVariableProxyImpl.class);
    Mockito.when(mock.name()).thenReturn(variable.name());
    Mockito.when(mock.typeName()).thenReturn(variable.typeName());
    this.localVars.put(mock, value);
  }

  @Override
  public ThreadReferenceProxyImpl threadProxy() {
    return Mockito.mock(ThreadReferenceProxyImpl.class);
  }
}
