package no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value;

import com.sun.jdi.LongValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import org.jetbrains.annotations.NotNull;

public class LongValueMock implements LongValue {
    private final long value;

    public LongValueMock(long value) {
        this.value = value;
    }

    @Override
    public long value() {
        return value;
    }

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
        return value;
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

    @Override
    public int compareTo(@NotNull LongValue o) {
        return 0;
    }
}
