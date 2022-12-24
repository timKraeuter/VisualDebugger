package no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value;

import com.sun.jdi.IntegerValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.TypeMock;
import org.jetbrains.annotations.NotNull;

public record IntegerValueMock(int value) implements IntegerValue {
    public static final String TYPE_NAME = "java.lang.Integer";

    @Override
    public Type type() {
        return new TypeMock(TYPE_NAME);
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
        return this.value;
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
    public VirtualMachine virtualMachine() {
        return null;
    }

    @Override
    public int compareTo(@NotNull final IntegerValue o) {
        return 0;
    }
}
