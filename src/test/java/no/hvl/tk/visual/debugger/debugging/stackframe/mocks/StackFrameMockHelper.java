package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.Value;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.*;

import java.util.List;

public class StackFrameMockHelper {
    public static void addLocalStringVariable(final StackFrameMock sf, final String variableName, final String value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.String"),
                new StringReferenceMock(value));
    }

    public static void addLocalCharVariable(final StackFrameMock sf, final String variableName, final char value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Char"),
                new CharValueMock(value));
    }

    public static void addLocalBooleanVariable(final StackFrameMock sf, final String variableName, final boolean value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Boolean"),
                new BooleanValueMock(value));

    }

    public static void addLocalByteVariable(final StackFrameMock sf, final String variableName, final byte value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Byte"),
                new ByteValueMock(value));

    }

    public static void addLocalShortVariable(final StackFrameMock sf, final String variableName, final short value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Short"),
                new ShortValueMock(value));

    }

    public static void addLocalIntegerVariable(final StackFrameMock sf, final String variableName, final int value) {
        sf.setValue(
                new LocalVariableMock(variableName, IntegerValueMock.TYPE_NAME),
                new IntegerValueMock(value));

    }

    public static void addLocalLongVariable(final StackFrameMock sf, final String variableName, final long value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Long"),
                new LongValueMock(value));

    }

    public static void addLocalFloatVariable(final StackFrameMock sf, final String variableName, final float value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Float"),
                new FloatValueMock(value));

    }

    public static void addLocalDoubleVariable(final StackFrameMock sf, final String variableName, final double value) {
        sf.setValue(
                new LocalVariableMock(variableName, "java.lang.Double"),
                new DoubleValueMock(value));

    }

    public static ObjectReferenceMock createObject(final StackFrameMock sf, final String typeName, final String variableName) {
        final ObjectReferenceMock objRefMock = new ObjectReferenceMock(typeName);
        sf.setValue(
                new LocalVariableMock(variableName, typeName),
                objRefMock
        );
        return objRefMock;
    }

    public static void addAttributeToObject(
            final ObjectReferenceMock objRefMock,
            final String fieldName,
            final String fieldType,
            final Value fieldValue) {
        objRefMock.setValue(new FieldMock(fieldName, fieldType), fieldValue);
    }

    public static ObjectReferenceMock createChildObject(
            final ObjectReferenceMock father,
            final String fieldName,
            final String childType) {
        final ObjectReferenceMock child = new ObjectReferenceMock(childType);
        father.setValue(new FieldMock(fieldName, childType), child);
        return child;
    }

    public static ArrayReferenceMock createArray(
            final StackFrameMock sf,
            final String variableName,
            final List<Value> content) {
        final ArrayReferenceMock arrayMock = new ArrayReferenceMock(content);
        sf.setValue(
                new LocalVariableMock(variableName, "Array"),
                arrayMock
        );
        return arrayMock;
    }

    public static void addChildObject(final ObjectReferenceMock obj, final String fieldName, final Value value) {
        obj.setValue(new FieldMock(fieldName, value.type().name()), value);
    }
}
