package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.openapi.diagnostic.Logger;
import com.sun.jdi.*;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.*;

public class StackFrameAnalyzer {
    private static final Logger LOGGER = Logger.getInstance(StackFrameAnalyzer.class);
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private final StackFrame stackFrame;
    private final ThreadReference thread;
    private final DebuggingInfoVisualizer debuggingVisualizer;
    private final int loadingDepth;

    private final Set<Long> seenObjectIds;
    /*
    Converting actual heap objects requires running code on the suspended VM thread.
    However, once we start running code on the thread, we can no longer read frame locals.
    Therefore, we have to convert all heap objects at the very end.
    */
    private final Map<ODObject, ObjectReference> rootObjects = new TreeMap<>();

    public StackFrameAnalyzer(
            final StackFrame stackFrame,
            final ThreadReference thread,
            final DebuggingInfoVisualizer debuggingVisualizer,
            final int loadingDepth) {
        this.stackFrame = stackFrame;
        this.thread = thread;
        this.debuggingVisualizer = debuggingVisualizer;
        this.loadingDepth = loadingDepth;
        this.seenObjectIds = new HashSet<>();
    }

    /**
     * Use a default loading depth of 10.
     */
    public StackFrameAnalyzer(
            final StackFrame stackFrame,
            final ThreadReference thread,
            final DebuggingInfoVisualizer debuggingVisualizer) {
        this(stackFrame, thread, debuggingVisualizer, 10);
    }

    public void analyze() {

        this.gatherThisObject(this.stackFrame);
        this.gatherVariablesInScope(this.stackFrame);

        this.convertObjects();

        this.seenObjectIds.clear();
    }


    private void convertObjects() {
        this.rootObjects.forEach((odObject, obRef) ->
                                         // No parents at root
                                         this.exploreObject(obRef, odObject, null, "", loadingDepth)
        );
    }

    private void gatherThisObject(final StackFrame stackFrame) {
        final ObjectReference thisObjectReference = stackFrame.thisObject();
        if (thisObjectReference == null) {
            LOGGER.warn("this object was null!");
            return;
        }

        final ODObject thisObject = new ODObject(
                thisObjectReference.uniqueID(),
                thisObjectReference.referenceType().name(),
                "this");

        this.rootObjects.put(thisObject, thisObjectReference);
    }

    private void gatherVariablesInScope(final StackFrame stackFrame) {
        try {
            // All visible variables in the stack frame.
            final List<LocalVariable> methodVariables = stackFrame.visibleVariables();
            methodVariables.forEach(localVariable -> this.gatherVariable(
                    localVariable,
                    stackFrame));
        }
        catch (final AbsentInformationException e) {
            // OK
        }
    }


    private void exploreObject(
            final ObjectReference objectReference,
            final ODObject odObject,
            final ODObject parentIfExists,
            final String linkTypeIfExists,
            int remainingDepthToBeExplored) {
        if (remainingDepthToBeExplored < 0) {
            return;
        }

        final String objectType = objectReference.referenceType().name();
        if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
            final Value value = objectReference.getValue(objectReference.referenceType().fieldByName(VALUE));
            this.convertValue(value,
                              odObject.getVariableName(),
                              objectType,
                              parentIfExists,
                              linkTypeIfExists,
                              true,
                              remainingDepthToBeExplored);
            return;
        }
        if (objectReference instanceof ArrayReference && !this.seenObjectIds.contains(objectReference.uniqueID())) {
            this.seenObjectIds.add(objectReference.uniqueID());
            this.convertArray(
                    odObject.getVariableName(),
                    (ArrayReference) objectReference,
                    objectType,
                    parentIfExists,
                    linkTypeIfExists,
                    remainingDepthToBeExplored);
            return;
        }
        if ((implementsInterface(objectReference, "java.util.List")
                || implementsInterface(objectReference, "java.util.Set"))
                && isInternalPackage(objectType)
                && !this.seenObjectIds.contains(objectReference.uniqueID())) {
            this.seenObjectIds.add(objectReference.uniqueID());
            this.convertListOrSet(odObject.getVariableName(),
                                  objectReference,
                                  objectType,
                                  parentIfExists,
                                  linkTypeIfExists,
                                  remainingDepthToBeExplored);
            return;
        }

        if (implementsInterface(objectReference, "java.util.Map")
                && isInternalPackage(objectType)
                && !this.seenObjectIds.contains(objectReference.uniqueID())) {
            this.seenObjectIds.add(objectReference.uniqueID());
            this.convertMap(odObject.getVariableName(),
                            objectReference,
                            objectType,
                            parentIfExists,
                            linkTypeIfExists,
                            remainingDepthToBeExplored);
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

        // Load fields
        for (final Map.Entry<Field, Value> fieldValueEntry : objectReference.getValues(this.getNonStaticFields(
                objectReference)).entrySet()) {
            final String fieldName = fieldValueEntry.getKey().name();
            this.convertValue(
                    fieldValueEntry.getValue(),
                    fieldName,
                    fieldValueEntry.getKey().typeName(),
                    odObject,
                    fieldName,
                    true,
                    remainingDepthToBeExplored - 1);
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
            final String linkTypeIfExists,
            int remainingDepthToBeExplored) {
        final ODObject newParent = this.findCollectionParent(
                name,
                arrayRef,
                objectType,
                parentIfExists,
                linkTypeIfExists,
                this.isPrimitiveOrEmptyArray(arrayRef));

        for (int i = 0; i < arrayRef.length(); i++) {
            final Value value = arrayRef.getValue(i);
            final String variableName = String.valueOf(i);
            this.convertValue(
                    value,
                    variableName,
                    value == null ? "" : value.type().name(),
                    newParent,
                    newParent.equals(parentIfExists) ? linkTypeIfExists : variableName,
                    true,
                    remainingDepthToBeExplored);
        }

    }

    private boolean isPrimitiveOrEmptyArray(final ArrayReference arrayRef) {
        if (arrayRef.length() >= 1) {
            final Optional<Value> arrayValue = arrayRef.getValues().stream()
                                                       .filter(Objects::nonNull)
                                                       .findFirst();
            if (arrayValue.isEmpty()) {
                return true;
            }
            final String arrayContentType = arrayValue.get().type().name();
            return PrimitiveTypes.isBoxedPrimitiveType(arrayContentType) ||
                    PrimitiveTypes.isNonBoxedPrimitiveType(arrayContentType);
        }
        return true;
    }

    private void convertListOrSet(
            final String name,
            final ObjectReference collectionRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists, int remainingDepthToBeExplored) {
        final ODObject newParent = this.findCollectionParent(
                name,
                collectionRef,
                objectType,
                parentIfExists,
                linkTypeIfExists,
                this.isPrimitiveOrEmptyListOrSet(collectionRef));

        final Iterator<Value> iterator = getIterator(this.thread, collectionRef);
        int i = 0;
        while (iterator.hasNext()) {
            final Value value = iterator.next();
            final String obName = String.valueOf(i);
            this.convertValue(
                    value,
                    obName,
                    value == null ? "" : value.type().name(),
                    newParent,
                    newParent.equals(parentIfExists) ? linkTypeIfExists : obName, // link type is just the index in case of root collections.
                    true,
                    remainingDepthToBeExplored);
            i++;
        }
    }

    private boolean isPrimitiveOrEmptyListOrSet(final ObjectReference collectionRef) {
        final Iterator<Value> iterator = getIterator(this.thread, collectionRef);
        Value next = null;
        while (iterator.hasNext() && next == null) {
            next = iterator.next();
        }
        if (next == null) {
            return true;
        }
        final String collectionContentType = next.type().name();
        return PrimitiveTypes.isBoxedPrimitiveType(collectionContentType) ||
                PrimitiveTypes.isNonBoxedPrimitiveType(collectionContentType);
    }

    @NotNull
    private ODObject findCollectionParent(
            final String name,
            final ObjectReference collectionRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists,
            final boolean primitiveOrEmpty) {
        final ODObject newParent;
        // Always create a new parent for primitive types or empty collections
        if (primitiveOrEmpty) {
            newParent = new ODObject(collectionRef.uniqueID(), objectType, name);
            if (parentIfExists != null) {
                this.debuggingVisualizer.addObject(newParent, false);
                this.debuggingVisualizer.addLinkToObject(parentIfExists, newParent, linkTypeIfExists);
            } else {
                this.debuggingVisualizer.addObject(newParent, true);
            }
        } else {
            newParent = this.createParentIfNeededForCollection(collectionRef, parentIfExists, name, objectType);
        }
        return newParent;
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

    private void convertMap(
            final String name,
            final ObjectReference mapRef,
            final String objectType,
            final ODObject parentIfExists,
            final String linkTypeIfExists,
            int remainingDepthToBeExplored) {
        final ODObject parent = this.createParentIfNeededForCollection(mapRef, parentIfExists, name, objectType);
        final ObjectReference entrySet = (ObjectReference) invokeSimple(this.thread, mapRef, "entrySet");
        final Iterator<Value> iterator = getIterator(this.thread, entrySet);
        int i = 0;
        while (iterator.hasNext()) {
            final ObjectReference entry = (ObjectReference) iterator.next();
            final Value keyValue = invokeSimple(this.thread, entry, "getKey");
            final Value valueValue = invokeSimple(this.thread, entry, "getValue");

            final ODObject entryObject = new ODObject(entry.uniqueID(),
                                                      entry.referenceType().name(),
                                                      String.valueOf(i));

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
                        true,
                        remainingDepthToBeExplored);
            }
            if (valueValue != null) {
                this.convertValue(
                        valueValue,
                        VALUE,
                        valueValue.type() == null ? "" : valueValue.type().name(),
                        entryObject,
                        VALUE,
                        true,
                        remainingDepthToBeExplored);
            }
            i++;
        }
    }

    private void gatherVariable(
            final LocalVariable localVariable,
            final StackFrame stackFrame) {
        final Value variableValue = stackFrame.getValue(localVariable);
        final String variableName = localVariable.name();
        final String variableType = localVariable.typeName();
        this.convertValue(variableValue, variableName, variableType, null, null, false, 0); // depth setting doesnt matter
    }

    private void convertValue(
            final Value variableValue,
            final String variableName,
            final String variableType,
            final ODObject parentIfExists,
            final String linkTypeIfExists,
            final boolean loadingObjectsPhase,
            int remainingDepthToBeExplored) {
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
        if (loadingObjectsPhase) {
            this.exploreObject(obj,
                               odObject,
                               parentIfExists,
                               linkTypeIfExists,
                               remainingDepthToBeExplored);
        } else {
            this.rootObjects.put(odObject, obj);
        }
    }

    private void addVariableToDiagram(final String variableName,
                                      final String variableType,
                                      final String value,
                                      final ODObject parentIfExists) {
        if (parentIfExists != null) {
            this.debuggingVisualizer.addAttributeToObject(parentIfExists, variableName, value, variableType);
        } else {
            this.debuggingVisualizer.addPrimitiveRootValue(variableName, variableType, value);
        }
    }
}
