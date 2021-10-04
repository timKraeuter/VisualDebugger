package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.*;

public class StackFrameAnalyzer {
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private final StackFrame stackFrame;
    private final ThreadReference thread;
    private final DebuggingInfoVisualizer debuggingVisualizer;

    private final Set<Long> seenObjectIds;
    /*
    Converting actual heap objects requires running code on the suspended VM thread.
    However, once we start running code on the thread, we can no longer read frame locals.
    Therefore, we have to convert all heap objects at the very end.
    */
    private final Map<ODObject, ObjectReference> rootObjects = new TreeMap<>();

    public StackFrameAnalyzer(final StackFrame stackFrame, final ThreadReference thread, final DebuggingInfoVisualizer debuggingVisualizer) {
        this.stackFrame = stackFrame;
        this.thread = thread;
        this.debuggingVisualizer = debuggingVisualizer;
        this.seenObjectIds = new HashSet<>();
    }

    public void analyze() {

        this.visualizeThisObject(this.stackFrame);
        this.visualizeVariables(this.stackFrame);

        this.convertObjects();

        this.seenObjectIds.clear();
    }


    private void convertObjects() {
        this.rootObjects.forEach((odObject, obRef) ->
                // No parents at root
                this.exploreObjectReference(obRef, odObject, null, "")
        );
    }

    private void visualizeThisObject(final StackFrame stackFrame) {
        final ObjectReference thisObjectReference = stackFrame.thisObject();
        assert thisObjectReference != null;

        final ODObject thisObject = new ODObject(
                thisObjectReference.uniqueID(),
                thisObjectReference.referenceType().name(),
                "this");

        this.rootObjects.put(thisObject, thisObjectReference);
    }

    private void visualizeVariables(final StackFrame stackFrame) {
        try {
            // All visible variables in the stack frame.
            final List<LocalVariable> methodVariables = stackFrame.visibleVariables();
            methodVariables.forEach(localVariable -> this.convertVariable(
                    localVariable,
                    stackFrame));
        } catch (final AbsentInformationException e) {
            // OK
        }
    }


    private void exploreObjectReference(
            final ObjectReference objectReference,
            final ODObject odObject,
            final ODObject parentIfExists,
            final String linkTypeIfExists) {
        final String objectType = objectReference.referenceType().name();
        if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
            final Value value = objectReference.getValue(objectReference.referenceType().fieldByName(VALUE));
            this.convertValue(value, odObject.getVariableName(), objectType, parentIfExists, linkTypeIfExists, true);
            return;
        }
        if (objectReference instanceof ArrayReference) {
            this.convertArray(
                    odObject.getVariableName(),
                    (ArrayReference) objectReference,
                    objectType,
                    parentIfExists,
                    linkTypeIfExists);
            return;
        }
        if ((implementsInterface(objectReference, "java.util.List")
                || implementsInterface(objectReference, "java.util.Set"))
                && isInternalPackage(objectType)) {
            this.convertListOrSet(odObject.getVariableName(), objectReference, objectType, parentIfExists, linkTypeIfExists);
            return;
        }

        if (implementsInterface(objectReference, "java.util.Map") && isInternalPackage(objectType)) {
            this.convertMap(odObject.getVariableName(), objectReference, objectType, parentIfExists, linkTypeIfExists);
            return;
        }

        if (parentIfExists != null) {
            this.debuggingVisualizer.addLinkToObject(parentIfExists, odObject, linkTypeIfExists);
        }

        if (this.seenObjectIds.contains(objectReference.uniqueID())) {
            return;
        }
        this.debuggingVisualizer.addObject(odObject, parentIfExists == null);
        this.seenObjectIds.add(objectReference.uniqueID());

        for (final Map.Entry<Field, Value> fieldValueEntry : objectReference.getValues(this.getNonStaticFields(objectReference)).entrySet()) {
            final String fieldName = fieldValueEntry.getKey().name();
            this.convertValue(
                    fieldValueEntry.getValue(),
                    fieldName,
                    fieldValueEntry.getKey().typeName(),
                    odObject,
                    fieldName,
                    true);
        }
    }

    @NotNull
    private List<Field> getNonStaticFields(final ObjectReference objectReference) {
        return objectReference.referenceType().allFields().stream()
                              .filter(field -> !field.isStatic())
                              .collect(Collectors.toList());
    }

    private void convertArray(
            final String name,
            final ArrayReference arrayRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists) {
        final ODObject parent = this.createParentIfNeededForCollection(arrayRef, parentIfExists, name, objectType);
        for (int i = 0; i < arrayRef.length(); i++) {
            final Value value = arrayRef.getValue(i);
            final String variableName = String.valueOf(i);
            this.convertValue(
                    value,
                    variableName,
                    value.type().name(),
                    parent,
                    parent.equals(parentIfExists) ? linkTypeIfExists : variableName, true); // link type is just the index in case of root collections.
        }
    }

    @NotNull
    private ODObject createParentIfNeededForCollection(
            final ObjectReference obRef,
            final ODObject parentIfExists,
            final String obName,
            final String objectType) {
        final ODObject parent;
        if (parentIfExists != null) {
            parent = parentIfExists;
        } else {
            parent = new ODObject(obRef.uniqueID(), objectType, obName);
            this.debuggingVisualizer.addObject(parent, true);
        }
        return parent;
    }

    private void convertListOrSet(
            final String name,
            final ObjectReference collectionRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists) {
        final ODObject parent = this.createParentIfNeededForCollection(collectionRef, parentIfExists, name, objectType);
        final Iterator<Value> iterator = getIterator(this.thread, collectionRef);
        int i = 0;
        while (iterator.hasNext()) {
            final Value value = iterator.next();
            final String obName = String.valueOf(i);
            this.convertValue(
                    value,
                    obName,
                    value.type().name(),
                    parent,
                    parent.equals(parentIfExists) ? linkTypeIfExists : obName, true); // link type is just the index in case of root collections.
            i++;
        }
    }

    private void convertMap(
            final String name,
            final ObjectReference mapRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists) {
        final ODObject parent = this.createParentIfNeededForCollection(mapRef, parentIfExists, name, objectType);
        final ObjectReference entrySet = (ObjectReference) invokeSimple(this.thread, mapRef, "entrySet");
        final Iterator<Value> iterator = getIterator(this.thread, entrySet);
        int i = 0;
        while (iterator.hasNext()) {
            final ObjectReference entry = (ObjectReference) iterator.next();
            final Value keyValue = invokeSimple(this.thread, entry, "getKey");
            final Value valueValue = invokeSimple(this.thread, entry, "getValue");

            final ODObject entryObject = new ODObject(entry.uniqueID(), entry.referenceType().name(), String.valueOf(i));

            this.debuggingVisualizer.addObject(entryObject, false);
            this.debuggingVisualizer.addLinkToObject(
                    parent,
                    entryObject,
                    i + (parentIfExists != null ? linkTypeIfExists : ""));

            if (keyValue != null) {
                this.convertValue(
                        keyValue,
                        KEY,
                        keyValue.type() == null ? "" : keyValue.type().name(),
                        entryObject,
                        KEY,
                        true);
            }
            if (valueValue != null) {
                this.convertValue(
                        valueValue,
                        VALUE,
                        valueValue.type() == null ? "" : valueValue.type().name(),
                        entryObject,
                        VALUE,
                        true);
            }
            i++;
        }
    }

    private void convertVariable(
            final LocalVariable localVariable,
            final StackFrame stackFrame) {
        final Value variableValue = stackFrame.getValue(localVariable);
        final String variableName = localVariable.name();
        final String variableType = localVariable.typeName();
        this.convertValue(variableValue, variableName, variableType, null, null, false);
    }

    private void convertValue(
            final Value variableValue,
            final String variableName,
            final String variableType,
            final ODObject parentIfExists,
            final String linkTypeIfExists,
            final boolean exploreObjects) {
        if (variableValue instanceof BooleanValue) {
            final String value = String.valueOf(((BooleanValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof ByteValue) {
            final String value = String.valueOf(((ByteValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof ShortValue) {
            final String value = String.valueOf(((ShortValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof IntegerValue) {
            final String value = Integer.toString(((IntegerValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof LongValue) {
            final String value = Long.toString(((LongValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof FloatValue) {
            final String value = Float.toString(((FloatValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof DoubleValue) {
            final String value = Double.toString(((DoubleValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
            return;
        }
        if (variableValue instanceof CharValue) {
            final String value = Character.toString(((CharValue) variableValue).value());
            this.addVariableToDiagram(variableName, variableType, String.format("'%s'", value), parentIfExists);
            return;
        }
        if (variableValue instanceof StringReference) {
            final String value = ((StringReference) variableValue).value();
            this.addVariableToDiagram(variableName, variableType, String.format("\"%s\"", value), parentIfExists);
            return;
        }
        final ObjectReference obj = (ObjectReference) variableValue;
        if (obj == null) {
            this.addVariableToDiagram(variableName, variableType, "null", parentIfExists);
            return;
        }

        final ODObject odObject = new ODObject(obj.uniqueID(), variableType, variableName);
        if (exploreObjects) {
            this.exploreObjectReference(obj, odObject, parentIfExists, linkTypeIfExists);
        } else {
            this.rootObjects.put(odObject, obj);
        }
    }

    private void addVariableToDiagram(final String variableName, final String variableType, final String value, final ODObject parentIfExists) {
        if (parentIfExists != null) {
            this.debuggingVisualizer.addAttributeToObject(parentIfExists, variableName, value, variableType);
        } else {
            this.debuggingVisualizer.addPrimitiveRootValue(variableName, variableType, value);
        }
    }
}
