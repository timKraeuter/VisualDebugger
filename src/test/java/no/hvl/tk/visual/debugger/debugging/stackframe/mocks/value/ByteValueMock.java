package no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value;

import com.sun.jdi.ByteValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import org.jetbrains.annotations.NotNull;

public class ByteValueMock implements ByteValue {
    private final byte value;

    public ByteValueMock(byte value) {
        this.value = value;
    }

    @Override
    public byte value() {
        return value;
    }

    @Override
    public byte byteValue() {
        return value;
    }

    // Below is irrelevant

    @Override
    public boolean booleanValue() {
        return false;
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

    @Override
    public int compareTo(@NotNull ByteValue o) {
        return 0;
    }
}
