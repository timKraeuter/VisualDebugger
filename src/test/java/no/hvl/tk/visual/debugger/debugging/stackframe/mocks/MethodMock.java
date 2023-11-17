package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record MethodMock(String name) implements Method {

  // Below is irrelevant

  @Override
  public String returnTypeName() {
    return null;
  }

  @Override
  public Type returnType() {
    return null;
  }

  @Override
  public List<String> argumentTypeNames() {
    return null;
  }

  @Override
  public List<Type> argumentTypes() {
    return null;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  @Override
  public boolean isSynchronized() {
    return false;
  }

  @Override
  public boolean isNative() {
    return false;
  }

  @Override
  public boolean isVarArgs() {
    return false;
  }

  @Override
  public boolean isBridge() {
    return false;
  }

  @Override
  public boolean isConstructor() {
    return false;
  }

  @Override
  public boolean isStaticInitializer() {
    return false;
  }

  @Override
  public boolean isObsolete() {
    return false;
  }

  @Override
  public List<Location> allLineLocations() {
    return null;
  }

  @Override
  public List<Location> allLineLocations(final String s, final String s1) {
    return null;
  }

  @Override
  public List<Location> locationsOfLine(final int i) {
    return null;
  }

  @Override
  public List<Location> locationsOfLine(final String s, final String s1, final int i) {
    return null;
  }

  @Override
  public Location locationOfCodeIndex(final long l) {
    return null;
  }

  @Override
  public List<LocalVariable> variables() {
    return null;
  }

  @Override
  public List<LocalVariable> variablesByName(final String s) {
    return null;
  }

  @Override
  public List<LocalVariable> arguments() {
    return null;
  }

  @Override
  public byte[] bytecodes() {
    return new byte[0];
  }

  @Override
  public Location location() {
    return null;
  }

  @Override
  public String signature() {
    return null;
  }

  @Override
  public String genericSignature() {
    return null;
  }

  @Override
  public ReferenceType declaringType() {
    return null;
  }

  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isFinal() {
    return false;
  }

  @Override
  public boolean isSynthetic() {
    return false;
  }

  @Override
  public int modifiers() {
    return 0;
  }

  @Override
  public boolean isPrivate() {
    return false;
  }

  @Override
  public boolean isPackagePrivate() {
    return false;
  }

  @Override
  public boolean isProtected() {
    return false;
  }

  @Override
  public boolean isPublic() {
    return false;
  }

  @Override
  public VirtualMachine virtualMachine() {
    return null;
  }

  @Override
  public int compareTo(@NotNull final Method method) {
    return 0;
  }
}
