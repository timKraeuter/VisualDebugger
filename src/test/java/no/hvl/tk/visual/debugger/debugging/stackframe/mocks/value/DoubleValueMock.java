package no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value;

import com.sun.jdi.DoubleValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import org.jetbrains.annotations.NotNull;

public record DoubleValueMock(double value) implements DoubleValue {

    // Below is irrelevant

    @Override
    public boolean booleanValue() {
        return false;
    }

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
        return value;
    }

    @Override
    public Type type() {
        return null;
    }

    @Override
    public VirtualMachine virtualMachine() {
        return null;
    }

    @Override
    public int compareTo(@NotNull DoubleValue o) {
        return 0;
    }
}
