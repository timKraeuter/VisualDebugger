package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.*;

public class StackFrameMockHelper {
    public static void addLocalStringVariable(StackFrameMock sf, String variableName, String value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.String"),
                new StringReferenceMock(value));
    }
    public static void addLocalCharVariable(StackFrameMock sf, String variableName, char value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Char"),
                new CharValueMock(value));
    }

    public static void addLocalBooleanVariable(StackFrameMock sf, String variableName, boolean value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Boolean"),
                new BooleanValueMock(value));

    }

    public static void addLocalByteVariable(StackFrameMock sf, String variableName, byte value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Byte"),
                new ByteValueMock(value));

    }

    public static void addLocalShortVariable(StackFrameMock sf, String variableName, short value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Short"),
                new ShortValueMock(value));

    }

    public static void addLocalIntegerVariable(StackFrameMock sf, String variableName, int value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Integer"),
                new IntegerValueMock(value));

    }

    public static void addLocalLongVariable(StackFrameMock sf, String variableName, long value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Long"),
                new LongValueMock(value));

    }

    public static void addLocalFloatVariable(StackFrameMock sf, String variableName, float value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Float"),
                new FloatValueMock(value));

    }

    public static void addLocalDoubleVariable(StackFrameMock sf, String variableName, double value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Double"),
                new DoubleValueMock(value));

    }
}
