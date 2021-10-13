package no.hvl.tk.visual.debugger.debugging.stackframe.mocks;

import com.sun.jdi.Value;
import no.hvl.tk.visual.debugger.debugging.stackframe.mocks.value.*;

import java.util.List;
import java.util.Set;

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

    public static ObjectReferenceMock<Value> createObject(final StackFrameMock sf, final String typeName, final String variableName) {
        final ObjectReferenceMock<Value> objRefMock = ObjectReferenceMock.create(typeName);
        sf.setValue(
                new LocalVariableMock(variableName, typeName),
                objRefMock
        );
        return objRefMock;
    }

    public static <E extends Value> void addAttributeToObject(
            final ObjectReferenceMock<E> objRefMock,
            final String fieldName,
            final String fieldType,
            final Value fieldValue) {
        objRefMock.setValue(new FieldMock(fieldName, fieldType), fieldValue);
    }

    public static <E extends Value> ObjectReferenceMock<Value> createChildObject(
            final ObjectReferenceMock<E> father,
            final String fieldName,
            final String childType) {
        final ObjectReferenceMock<Value> child = ObjectReferenceMock.create(childType);
        father.setValue(new FieldMock(fieldName, childType), child);
        return child;
    }

    public static void createArray(
            final StackFrameMock sf,
            final String variableName,
            final List<Value> content) {
        final ArrayReferenceMock arrayMock = new ArrayReferenceMock(content);
        sf.setValue(
                new LocalVariableMock(variableName, "Array"),
                arrayMock
        );
    }

    public static <E extends Value> void addChildObject(
            final ObjectReferenceMock<E> obj,
            final String fieldName,
            final Value value) {
        obj.setValue(new FieldMock(fieldName, value.type().name()), value);
    }

    public static <E extends Value> void createList(
            final StackFrameMock sf,
            final String variableName,
            final List<E> content) {
        final String typeName = "java.util.List";
        final ObjectReferenceMock<E> setObjectReferenceMock = ObjectReferenceMock.createCollectionObjectRefMock(
                typeName,
                content);
        setObjectReferenceMock.referenceType().addInterface(new InterfaceTypeMock(typeName));
        sf.setValue(
                new LocalVariableMock(variableName, "java.util.ArrayList"),
                setObjectReferenceMock
        );
    }

    public static <E extends Value> void createSet(
            final StackFrameMock sf,
            final String variableName,
            final Set<E> content) {
        final String typeName = "java.util.Set";
        final ObjectReferenceMock<E> setObjectReferenceMock = ObjectReferenceMock.createCollectionObjectRefMock(typeName, content);
        setObjectReferenceMock.referenceType().addInterface(new InterfaceTypeMock(typeName));
        sf.setValue(
                new LocalVariableMock(variableName, "java.util.HashSet"),
                setObjectReferenceMock
        );
    }
}
