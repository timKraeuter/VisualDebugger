package no.hvl.tk.visual.debugger.debugging.stackframe;

import static no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.*;

import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.jdi.LocalVariableProxyImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.sun.jdi.*;
import java.util.*;
import no.hvl.tk.visual.debugger.debugging.stackframe.exceptions.StackFrameAnalyzerException;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import no.hvl.tk.visual.debugger.domain.ObjectDiagramBuilder;
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes;
import org.jetbrains.annotations.NotNull;

public class StackFrameAnalyzer {

  private static final Logger LOGGER = Logger.getInstance(StackFrameAnalyzer.class);
  private static final String KEY = "key";
  private static final String VALUE = "value";
  private final Set<String> manuallyExploredObjects;
  private final boolean showNullValues;
  private final Map<String, Pair<ODObject, ObjectReference>> objectRefMap;

  private final IStackFrame stackFrame;
  private final int loadingDepth;

  private final Set<Long> seenObjectIds;

  private ObjectDiagramBuilder builder;

  public StackFrameAnalyzer(
      final IStackFrame stackFrame,
      final int loadingDepth,
      final Set<String> manuallyExploredObjects,
      boolean showNullValues) {
    this.stackFrame = stackFrame;
    this.loadingDepth = loadingDepth;
    this.manuallyExploredObjects = manuallyExploredObjects;
    this.showNullValues = showNullValues;
    this.seenObjectIds = new HashSet<>();
    this.builder = new ObjectDiagramBuilder();
    this.objectRefMap = builder.getObjectRefMap();
  }

  /** Used for testing. */
  protected StackFrameAnalyzer(final IStackFrame stackFrame) {
    this(stackFrame, 10, new HashSet<>(), true);
  }

  /** Used for testing. */
  protected StackFrameAnalyzer(final IStackFrame stackFrame, final int loadingDepth) {
    this(stackFrame, loadingDepth, new HashSet<>(), true);
  }

  public ObjectDiagram analyze() {
    try {
      this.analyzeThisObject(this.stackFrame);
      this.analyzeVariablesInScope(this.stackFrame);

      seenObjectIds.clear();
      return builder.build();
    } catch (EvaluateException e) {
      throw new StackFrameAnalyzerException(e);
    }
  }

  public ObjectDiagram getChildren(String objectID) {
    builder = new ObjectDiagramBuilder();

    Pair<ODObject, ObjectReference> objectAndReference = objectRefMap.get(objectID);
    ODObject odObject = objectAndReference.getFirst();
    odObject.getAttributeValues().clear();
    ObjectReference objectReference = objectAndReference.getSecond();

    this.exploreObject(objectReference, odObject, null, "", 1);

    return builder.build();
  }

  private void analyzeThisObject(final IStackFrame stackFrame) throws EvaluateException {
    final ObjectReference thisObjectReference = stackFrame.thisObject();
    if (thisObjectReference == null) {
      LOGGER.warn("this object was null!");
      return;
    }

    final ODObject thisObject =
        new ODObject(
            thisObjectReference.uniqueID(), thisObjectReference.referenceType().name(), "this");

    this.exploreObject(thisObjectReference, thisObject, null, "", loadingDepth);
  }

  private void analyzeVariablesInScope(final IStackFrame stackFrame) throws EvaluateException {
    // All visible variables in the stack frame.
    final List<LocalVariableProxyImpl> methodVariables = stackFrame.visibleVariables();
    for (LocalVariableProxyImpl localVariable : methodVariables) {
      this.analyzeVariable(localVariable, stackFrame);
    }
  }

  private void exploreObject(
      final ObjectReference objectReference,
      final ODObject odObject,
      final ODObject parentIfExists,
      final String linkTypeIfExists,
      int remainingDepthToBeExplored) {
    if (remainingDepthToBeExplored < 0 && notManuallyExplored(odObject, parentIfExists)) {
      return;
    }

    final String objectType = objectReference.referenceType().name();
    if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
      final Value value =
          objectReference.getValue(objectReference.referenceType().fieldByName(VALUE));
      this.convertValue(
          value,
          odObject.getVariableName(),
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored);
      return;
    }
    if (objectReference instanceof ArrayReference arrayReference
        && !this.seenObjectIds.contains(objectReference.uniqueID())) {
      this.seenObjectIds.add(objectReference.uniqueID());
      this.convertArray(
          odObject.getVariableName(),
          arrayReference,
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
      this.convertListOrSet(
          odObject.getVariableName(),
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
      this.convertMap(
          odObject.getVariableName(),
          objectReference,
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored);
      return;
    }

    if (parentIfExists != null) {
      this.builder.addLinkToObject(parentIfExists, odObject, linkTypeIfExists);
    }

    if (this.seenObjectIds.contains(objectReference.uniqueID())) {
      return;
    }
    this.builder.addObject(odObject, objectReference);
    this.seenObjectIds.add(objectReference.uniqueID());

    // Load fields
    for (final Map.Entry<Field, Value> fieldValueEntry :
        objectReference.getValues(this.getNonStaticFields(objectReference)).entrySet()) {
      final String fieldName = fieldValueEntry.getKey().name();
      this.convertValue(
          fieldValueEntry.getValue(),
          fieldName,
          fieldValueEntry.getKey().typeName(),
          odObject,
          fieldName,
          remainingDepthToBeExplored - 1);
    }
  }

  private boolean notManuallyExplored(ODObject odObject, ODObject parentIfExists) {
    return objectNotManuallyExplored(odObject) && parentNotManuallyExplored(parentIfExists);
  }

  private boolean objectNotManuallyExplored(ODObject odObject) {
    return !manuallyExploredObjects.contains(odObject.getId());
  }

  private boolean parentNotManuallyExplored(ODObject parentIfExists) {
    return parentIfExists != null && objectNotManuallyExplored(parentIfExists);
  }

  @NotNull private List<Field> getNonStaticFields(final ObjectReference objectReference) {
    return objectReference.referenceType().allFields().stream()
        .filter(field -> !field.isStatic())
        .toList();
  }

  private void convertArray(
      final String name,
      final ArrayReference arrayRef,
      final String objectType,
      final ODObject parentIfExists,
      final String linkTypeIfExists,
      int remainingDepthToBeExplored) {
    final ODObject newParent =
        this.findCollectionParent(
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
          remainingDepthToBeExplored);
    }
  }

  private boolean isPrimitiveOrEmptyArray(final ArrayReference arrayRef) {
    if (arrayRef.length() >= 1) {
      final Optional<Value> arrayValue =
          arrayRef.getValues().stream().filter(Objects::nonNull).findFirst();
      if (arrayValue.isEmpty()) {
        return true;
      }
      final String arrayContentType = arrayValue.get().type().name();
      return PrimitiveTypes.isBoxedPrimitiveType(arrayContentType)
          || PrimitiveTypes.isNonBoxedPrimitiveType(arrayContentType);
    }
    return true;
  }

  private void convertListOrSet(
      final String name,
      final ObjectReference collectionRef,
      final String objectType,
      final ODObject parentIfExists,
      final String linkTypeIfExists,
      int remainingDepthToBeExplored) {
    final ODObject newParent =
        this.findCollectionParent(
            name,
            collectionRef,
            objectType,
            parentIfExists,
            linkTypeIfExists,
            this.isPrimitiveOrEmptyListOrSet(collectionRef));

    final Iterator<Value> iterator = getIterator(getThreadReference(), collectionRef);
    int i = 0;
    while (iterator.hasNext()) {
      final Value value = iterator.next();
      final String obName = String.valueOf(i);
      this.convertValue(
          value,
          obName,
          value == null ? "" : value.type().name(),
          newParent,
          newParent.equals(parentIfExists)
              ? linkTypeIfExists
              : obName, // link type is just the index in case of root collections.
          remainingDepthToBeExplored);
      i++;
    }
  }

  private boolean isPrimitiveOrEmptyListOrSet(final ObjectReference collectionRef) {
    final Iterator<Value> iterator = getIterator(getThreadReference(), collectionRef);
    Value next = null;
    while (iterator.hasNext() && next == null) {
      next = iterator.next();
    }
    if (next == null) {
      return true;
    }
    final String collectionContentType = next.type().name();
    return PrimitiveTypes.isBoxedPrimitiveType(collectionContentType)
        || PrimitiveTypes.isNonBoxedPrimitiveType(collectionContentType);
  }

  @NotNull private ODObject findCollectionParent(
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
        this.builder
            .addObject(newParent, collectionRef)
            .addLinkToObject(parentIfExists, newParent, linkTypeIfExists);
      } else {
        this.builder.addObject(newParent, collectionRef);
      }
    } else {
      newParent =
          this.createParentIfNeededForCollection(collectionRef, parentIfExists, name, objectType);
    }
    return newParent;
  }

  @NotNull private ODObject createParentIfNeededForCollection(
      final ObjectReference obRef,
      final ODObject parentIfExists,
      final String obName,
      final String objectType) {
    final ODObject parent;
    if (parentIfExists != null) {
      parent = parentIfExists;
    } else {
      parent = new ODObject(obRef.uniqueID(), objectType, obName);
      this.builder.addObject(parent, obRef);
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
    final ODObject parent =
        this.createParentIfNeededForCollection(mapRef, parentIfExists, name, objectType);
    final ObjectReference entrySet =
        (ObjectReference) invokeSimple(getThreadReference(), mapRef, "entrySet");
    final Iterator<Value> iterator = getIterator(getThreadReference(), entrySet);
    int i = 0;
    while (iterator.hasNext()) {
      final ObjectReference entry = (ObjectReference) iterator.next();
      final Value keyValue = invokeSimple(getThreadReference(), entry, "getKey");
      final Value valueValue = invokeSimple(getThreadReference(), entry, "getValue");

      final ODObject entryObject =
          new ODObject(entry.uniqueID(), entry.referenceType().name(), String.valueOf(i));

      this.builder
          .addObject(entryObject, entry)
          .addLinkToObject(
              parent, entryObject, i + (parentIfExists != null ? linkTypeIfExists : ""));

      if (keyValue != null) {
        this.convertValue(
            keyValue,
            KEY,
            keyValue.type() == null ? "" : keyValue.type().name(),
            entryObject,
            KEY,
            remainingDepthToBeExplored);
      }
      if (valueValue != null) {
        this.convertValue(
            valueValue,
            VALUE,
            valueValue.type() == null ? "" : valueValue.type().name(),
            entryObject,
            VALUE,
            remainingDepthToBeExplored);
      }
      i++;
    }
  }

  private ThreadReference getThreadReference() {
    return stackFrame.threadProxy().getThreadReference();
  }

  private void analyzeVariable(
      final LocalVariableProxyImpl localVariable, final IStackFrame stackFrame)
      throws EvaluateException {
    final Value variableValue = stackFrame.getValue(localVariable);
    final String variableName = localVariable.name();
    final String variableType = localVariable.typeName();
    this.convertValue(variableValue, variableName, variableType, null, "", loadingDepth);
  }

  private void convertValue(
      final Value variableValue,
      final String variableName,
      final String variableType,
      final ODObject parentIfExists,
      final String linkTypeIfExists,
      int remainingDepthToBeExplored) {
    if (variableValue instanceof BooleanValue booleanValue) {
      final String value = String.valueOf(booleanValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof ByteValue byteValue) {
      final String value = String.valueOf(byteValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof ShortValue shortValue) {
      final String value = String.valueOf(shortValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof IntegerValue integerValue) {
      final String value = Integer.toString(integerValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof LongValue longValue) {
      final String value = Long.toString(longValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof FloatValue floatValue) {
      final String value = Float.toString(floatValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof DoubleValue doubleValue) {
      final String value = Double.toString(doubleValue.value());
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists);
      return;
    }
    if (variableValue instanceof CharValue charValue) {
      final String value = Character.toString(charValue.value());
      this.addVariableToDiagram(
          variableName, variableType, String.format("'%s'", value), parentIfExists);
      return;
    }
    if (variableValue instanceof StringReference stringReference) {
      final String value = stringReference.value();
      this.addVariableToDiagram(
          variableName, variableType, String.format("\"%s\"", value), parentIfExists);
      return;
    }
    final ObjectReference obj = (ObjectReference) variableValue;
    if (obj == null) {
      if (showNullValues) {
        this.addVariableToDiagram(variableName, variableType, "null", parentIfExists);
      }
      return;
    }

    final ODObject odObject = new ODObject(obj.uniqueID(), variableType, variableName);
    this.exploreObject(obj, odObject, parentIfExists, linkTypeIfExists, remainingDepthToBeExplored);
  }

  private void addVariableToDiagram(
      final String variableName,
      final String variableType,
      final String value,
      final ODObject parentIfExists) {
    if (parentIfExists != null) {
      this.builder.addAttributeToObject(parentIfExists, variableName, value, variableType);
    } else {
      this.builder.addPrimitiveRootValue(variableName, variableType, value);
    }
  }
}
