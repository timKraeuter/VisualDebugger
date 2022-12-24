package no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public record BooleanValueMock(boolean value) implements BooleanValue {

    @Override
    public boolean booleanValue() {
        return value;
    }

    // Below is irrelevant

    @Override
    public byte byteValue() {
        return 0;
    }

    @Override
    public char charValue() {
        return 0;
    }

    @Override
    public short shortValue() {
        return 0;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }
}
