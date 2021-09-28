package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;

public class LocalVariableMock implements LocalVariable {

    private String name;
    private String typeName;

    public LocalVariableMock(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String typeName() {
        return typeName;
    }

    @Override
    public Type type() throws ClassNotLoadedException {
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
    public boolean isVisible(StackFrame frame) {
        return false;
    }

    @Override
    public boolean isArgument() {
        return false;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }

    @Override
    public int compareTo(@NotNull LocalVariable o) {
        return 0;
    }
}
