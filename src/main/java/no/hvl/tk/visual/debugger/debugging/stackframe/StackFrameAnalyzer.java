package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.openapi.util.Pair;
import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.*;
import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.invokeSimple;

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
    private final TreeMap<Long, Pair<ObjectReference, ODObject>> rootObjects = new TreeMap<>();

    public StackFrameAnalyzer(StackFrame stackFrame, ThreadReference thread, DebuggingInfoVisualizer debuggingVisualizer) {
        this.stackFrame = stackFrame;
        this.thread = thread;
        this.debuggingVisualizer = debuggingVisualizer;
        this.seenObjectIds = new HashSet<>();
    }

    public void analyze() {

        visualizeThisObject(stackFrame);
        visualizeVariables(stackFrame);

        convertObjects();

        this.seenObjectIds.clear();
    }


    private void convertObjects() {
        rootObjects.forEach((objID, objectReferenceODObjectPair) -> {
            final ObjectReference obRef = objectReferenceODObjectPair.getFirst();
            final ODObject odObject = objectReferenceODObjectPair.getSecond();
            // No parents at root
            this.exploreObjectReference(obRef, odObject, null, "");
        });
    }

    private void visualizeThisObject(StackFrame stackFrame) {
        ObjectReference thisObjectReference = stackFrame.thisObject();
        assert thisObjectReference != null;

        final ODObject thisObject = new ODObject(
                thisObjectReference.uniqueID(),
                thisObjectReference.referenceType().name(),
                "this");

        rootObjects.put(thisObjectReference.uniqueID(),
                Pair.create(thisObjectReference,
                        thisObject));
    }

    private void visualizeVariables(StackFrame stackFrame) {
        try {
            // All visible variables in the stack frame.
            final List<LocalVariable> methodVariables = stackFrame.visibleVariables();
            methodVariables.forEach(localVariable -> this.convertVariable(
                    localVariable,
                    stackFrame));
        } catch (AbsentInformationException e) {
            // OK
        }
    }


    private void exploreObjectReference(
            ObjectReference objectReference,
            ODObject odObject,
            ODObject parentIfExists,
            String linkTypeIfExists) {
        final String objectType = objectReference.referenceType().name();
        if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
            final Value value = objectReference.getValue(objectReference.referenceType().fieldByName(VALUE));
            this.convertValue(value, odObject.getVariableName(), objectType, parentIfExists, linkTypeIfExists, true);
            return;
        }
        if (objectReference instanceof ArrayReference) {
            convertArray(
                    odObject.getVariableName(),
                    (ArrayReference) objectReference,
                    objectType,
                    parentIfExists,
                    linkTypeIfExists);
            return;
        }
        if ((doesImplementInterface(objectReference, "java.util.List")
                || doesImplementInterface(objectReference, "java.util.Set"))
                && isInternalPackage(objectType)) {
            convertListOrSet(odObject.getVariableName(), objectReference, objectType, parentIfExists, linkTypeIfExists);
            return;
        }

        if (doesImplementInterface(objectReference, "java.util.Map") && isInternalPackage(objectType)) {
            convertMap(odObject.getVariableName(), objectReference, objectType, parentIfExists, linkTypeIfExists);
            return;
        }

        if (parentIfExists != null) {
            debuggingVisualizer.addLinkToObject(parentIfExists, odObject, linkTypeIfExists);
        }

        if (this.seenObjectIds.contains(objectReference.uniqueID())) {
            return;
        }
        this.debuggingVisualizer.addObject(odObject);
        this.seenObjectIds.add(objectReference.uniqueID());

        // Filter static fields? Or non visible fields?
        for (Map.Entry<Field, Value> fieldValueEntry : objectReference.getValues(getNonStaticFields(objectReference)).entrySet()) {
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
    private List<Field> getNonStaticFields(ObjectReference objectReference) {
        return objectReference.referenceType().allFields().stream()
                              .filter(field -> !field.isStatic())
                              .collect(Collectors.toList());
    }

    private void convertArray(
            String name,
            ArrayReference arrayRef,
            String objectType,
            ODObject parentIfExists,
            String linkTypeIfExists) {
        final ODObject parent = createParentIfNeededForCollection(arrayRef, parentIfExists, name, objectType);
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
            ObjectReference obRef,
            ODObject parentIfExists,
            String obName,
            String objectType) {
        final ODObject parent;
        if (parentIfExists != null) {
            parent = parentIfExists;
        } else {
            parent = new ODObject(obRef.uniqueID(), objectType, obName);
            this.debuggingVisualizer.addObject(parent);
        }
        return parent;
    }

    private void convertListOrSet(
            String name,
            ObjectReference collectionRef,
            String objectType,
            ODObject parentIfExists,
            String linkTypeIfExists) {
        final ODObject parent = createParentIfNeededForCollection(collectionRef, parentIfExists, name, objectType);
        Iterator<Value> iterator = getIterator(thread, collectionRef);
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
            String name,
            ObjectReference mapRef,
            String objectType,
            ODObject parentIfExists,
            String linkTypeIfExists) {
        final ODObject parent = createParentIfNeededForCollection(mapRef, parentIfExists, name, objectType);
        ObjectReference entrySet = (ObjectReference) invokeSimple(thread, mapRef, "entrySet");
        Iterator<Value> iterator = getIterator(thread, entrySet);
        int i = 0;
        while (iterator.hasNext()) {
            ObjectReference entry = (ObjectReference) iterator.next();
            final Value keyValue = invokeSimple(thread, entry, "getKey");
            final Value valueValue = invokeSimple(thread, entry, "getValue");

            final ODObject entryObject = new ODObject(entry.uniqueID(), entry.referenceType().name(), String.valueOf(i));

            this.debuggingVisualizer.addObject(entryObject);
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
            LocalVariable localVariable,
            StackFrame stackFrame) {
        final Value variableValue = stackFrame.getValue(localVariable);
        final String variableName = localVariable.name();
        final String variableType = localVariable.typeName();
        this.convertValue(variableValue, variableName, variableType, null, null, false);
    }

    private void convertValue(
            Value variableValue,
            String variableName,
            String variableType,
            ODObject parentIfExists,
            String linkTypeIfExists,
            boolean exploreObjects) {
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
        ObjectReference obj = (ObjectReference) variableValue;
        if (obj == null) {
            this.addVariableToDiagram(variableName, variableType, "null", parentIfExists);
            return;
        }

        final ODObject odObject = new ODObject(obj.uniqueID(), variableType, variableName);
        if (exploreObjects) {
            this.exploreObjectReference(obj, odObject, parentIfExists, linkTypeIfExists);
        } else {
            this.rootObjects.put(obj.uniqueID(), Pair.create(obj, odObject));
        }
    }

    private void addVariableToDiagram(String variableName, String variableType, String value, ODObject parentIfExists) {
        if (parentIfExists != null) {
            this.debuggingVisualizer.addAttributeToObject(parentIfExists, variableName, value, variableType);
        } else {
            this.debuggingVisualizer.addPrimitiveRootValue(variableName, variableType, value);
        }
    }
}
