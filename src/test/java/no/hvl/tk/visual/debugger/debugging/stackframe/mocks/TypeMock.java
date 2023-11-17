package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class TypeMock implements Type {

  private final String typeName;

  public TypeMock(final String typeName) {
    this.typeName = typeName;
  }

  @Override
  public String name() {
    return this.typeName;
  }

  @Override
  public String signature() {
    return null;
  }

  @Override
  public VirtualMachine virtualMachine() {
    return null;
  }
}
