package no.hvl.tk.visual.debugger.debugging.stackframe

import com.intellij.debugger.engine.evaluation.EvaluateException
import com.intellij.debugger.jdi.LocalVariableProxyImpl
import com.intellij.openapi.util.Pair
import com.sun.jdi.*
import java.util.*
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.getIterator
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.implementsInterface
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.invokeSimple
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameSessionListenerHelper.isInternalPackage
import no.hvl.tk.visual.debugger.debugging.stackframe.exceptions.StackFrameAnalyzerException
import no.hvl.tk.visual.debugger.domain.ODObject
import no.hvl.tk.visual.debugger.domain.ObjectDiagram
import no.hvl.tk.visual.debugger.domain.ObjectDiagramBuilder
import no.hvl.tk.visual.debugger.domain.PrimitiveTypes

class StackFrameAnalyzer
@JvmOverloads
constructor(
    private val stackFrame: IStackFrame,
    private val loadingDepth: Int = 10,
    private val manuallyExploredObjects: Set<String> = HashSet(),
    private val showNullValues: Boolean = true
) {
  private val objectRefMap: Map<String, Pair<ODObject, ObjectReference>>

  private val seenObjectIds: MutableSet<Long> = HashSet()

  private var builder: ObjectDiagramBuilder

  init {
    this.builder = ObjectDiagramBuilder()
    this.objectRefMap = builder.getObjectRefMap()
  }

  fun analyze(): ObjectDiagram {
    try {
      this.analyzeThisObject(this.stackFrame)
      this.analyzeVariablesInScope(this.stackFrame)

      seenObjectIds.clear()
      return builder.build()
    } catch (e: EvaluateException) {
      throw StackFrameAnalyzerException(e)
    }
  }

  fun getChildren(objectID: String): ObjectDiagram {
    builder = ObjectDiagramBuilder()

    val objectAndReference = objectRefMap[objectID]!!
    val odObject = objectAndReference.getFirst()
    odObject.attributeValues.clear()
    val objectReference = objectAndReference.getSecond()

    this.exploreObject(objectReference, odObject, null, "", 1)

    return builder.build()
  }

  @Throws(EvaluateException::class)
  private fun analyzeThisObject(stackFrame: IStackFrame) {
    val thisObjectReference = stackFrame.thisObject()

    val thisObject =
        ODObject(thisObjectReference.uniqueID(), thisObjectReference.referenceType().name(), "this")

    this.exploreObject(thisObjectReference, thisObject, null, "", loadingDepth)
  }

  @Throws(EvaluateException::class)
  private fun analyzeVariablesInScope(stackFrame: IStackFrame) {
    // All visible variables in the stack frame.
    val methodVariables = stackFrame.visibleVariables()
    for (localVariable in methodVariables) {
      this.analyzeVariable(localVariable, stackFrame)
    }
  }

  private fun exploreObject(
      objectReference: ObjectReference,
      odObject: ODObject,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      remainingDepthToBeExplored: Int
  ) {
    if (remainingDepthToBeExplored < 0 && notManuallyExplored(odObject, parentIfExists)) {
      return
    }

    val objectType = objectReference.referenceType().name()
    if (PrimitiveTypes.isBoxedPrimitiveType(objectType)) {
      val value = objectReference.getValue(objectReference.referenceType().fieldByName(VALUE))
      this.convertValue(
          value,
          odObject.variableName,
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored)
      return
    }
    if (objectReference is ArrayReference && !seenObjectIds.contains(objectReference.uniqueID())) {
      seenObjectIds.add(objectReference.uniqueID())
      this.convertArray(
          odObject.variableName,
          objectReference,
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored)
      return
    }
    if ((implementsInterface(objectReference, "java.util.List") ||
        implementsInterface(objectReference, "java.util.Set")) &&
        isInternalPackage(objectType) &&
        !seenObjectIds.contains(objectReference.uniqueID())) {
      seenObjectIds.add(objectReference.uniqueID())
      this.convertListOrSet(
          odObject.variableName,
          objectReference,
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored)
      return
    }

    if (implementsInterface(objectReference, "java.util.Map") &&
        isInternalPackage(objectType) &&
        !seenObjectIds.contains(objectReference.uniqueID())) {
      seenObjectIds.add(objectReference.uniqueID())
      this.convertMap(
          odObject.variableName,
          objectReference,
          objectType,
          parentIfExists,
          linkTypeIfExists,
          remainingDepthToBeExplored)
      return
    }

    if (parentIfExists != null) {
      builder.addLinkToObject(parentIfExists, odObject, linkTypeIfExists)
    }

    if (seenObjectIds.contains(objectReference.uniqueID())) {
      return
    }
    builder.addObject(odObject, objectReference)
    seenObjectIds.add(objectReference.uniqueID())

    // Load fields
    for ((key, value) in objectReference.getValues(this.getNonStaticFields(objectReference))) {
      val fieldName = key.name()
      this.convertValue(
          value, fieldName, key.typeName(), odObject, fieldName, remainingDepthToBeExplored - 1)
    }
  }

  private fun notManuallyExplored(odObject: ODObject, parentIfExists: ODObject?): Boolean {
    return objectNotManuallyExplored(odObject) && parentNotManuallyExplored(parentIfExists)
  }

  private fun objectNotManuallyExplored(odObject: ODObject): Boolean {
    return !manuallyExploredObjects.contains(odObject.id)
  }

  private fun parentNotManuallyExplored(parentIfExists: ODObject?): Boolean {
    return parentIfExists != null && objectNotManuallyExplored(parentIfExists)
  }

  private fun getNonStaticFields(objectReference: ObjectReference): List<Field> {
    return objectReference
        .referenceType()
        .allFields()
        .stream()
        .filter { field: Field -> !field.isStatic }
        .toList()
  }

  private fun convertArray(
      name: String,
      arrayRef: ArrayReference,
      objectType: String,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      remainingDepthToBeExplored: Int
  ) {
    val newParent =
        this.findCollectionParent(
            name,
            arrayRef,
            objectType,
            parentIfExists,
            linkTypeIfExists,
            this.isPrimitiveOrEmptyArray(arrayRef))

    for (i in 0 until arrayRef.length()) {
      val value = arrayRef.getValue(i)
      val variableName = i.toString()
      this.convertValue(
          value,
          variableName,
          if (value == null) "" else value.type().name(),
          newParent,
          if (newParent == parentIfExists) linkTypeIfExists else variableName,
          remainingDepthToBeExplored)
    }
  }

  private fun isPrimitiveOrEmptyArray(arrayRef: ArrayReference): Boolean {
    if (arrayRef.length() >= 1) {
      val arrayValue =
          arrayRef.values.stream().filter { obj: Value? -> Objects.nonNull(obj) }.findFirst()
      if (arrayValue.isEmpty) {
        return true
      }
      val arrayContentType = arrayValue.get().type().name()
      return (PrimitiveTypes.isBoxedPrimitiveType(arrayContentType) ||
          PrimitiveTypes.isNonBoxedPrimitiveType(arrayContentType))
    }
    return true
  }

  private fun convertListOrSet(
      name: String,
      collectionRef: ObjectReference,
      objectType: String,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      remainingDepthToBeExplored: Int
  ) {
    val newParent =
        this.findCollectionParent(
            name,
            collectionRef,
            objectType,
            parentIfExists,
            linkTypeIfExists,
            this.isPrimitiveOrEmptyListOrSet(collectionRef))

    val iterator = getIterator(threadReference, collectionRef)
    var i = 0
    while (iterator.hasNext()) {
      val value = iterator.next()
      val obName = i.toString()
      this.convertValue(
          value,
          obName,
          if (value == null) "" else value.type().name(),
          newParent,
          if (newParent == parentIfExists) linkTypeIfExists
          else obName, // link type is just the index in case of root collections.
          remainingDepthToBeExplored)
      i++
    }
  }

  private fun isPrimitiveOrEmptyListOrSet(collectionRef: ObjectReference): Boolean {
    val iterator = getIterator(threadReference, collectionRef)
    var next: Value? = null
    while (iterator.hasNext() && next == null) {
      next = iterator.next()
    }
    if (next == null) {
      return true
    }
    val collectionContentType = next.type().name()
    return (PrimitiveTypes.isBoxedPrimitiveType(collectionContentType) ||
        PrimitiveTypes.isNonBoxedPrimitiveType(collectionContentType))
  }

  private fun findCollectionParent(
      name: String,
      collectionRef: ObjectReference,
      objectType: String,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      primitiveOrEmpty: Boolean
  ): ODObject {
    val newParent: ODObject
    // Always create a new parent for primitive types or empty collections
    if (primitiveOrEmpty) {
      newParent = ODObject(collectionRef.uniqueID(), objectType, name)
      if (parentIfExists != null) {
        builder
            .addObject(newParent, collectionRef)
            .addLinkToObject(parentIfExists, newParent, linkTypeIfExists)
      } else {
        builder.addObject(newParent, collectionRef)
      }
    } else {
      newParent =
          this.createParentIfNeededForCollection(collectionRef, parentIfExists, name, objectType)
    }
    return newParent
  }

  private fun createParentIfNeededForCollection(
      obRef: ObjectReference,
      parentIfExists: ODObject?,
      obName: String,
      objectType: String
  ): ODObject {
    val parent: ODObject
    if (parentIfExists != null) {
      parent = parentIfExists
    } else {
      parent = ODObject(obRef.uniqueID(), objectType, obName)
      builder.addObject(parent, obRef)
    }
    return parent
  }

  private fun convertMap(
      name: String,
      mapRef: ObjectReference,
      objectType: String,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      remainingDepthToBeExplored: Int
  ) {
    val parent = this.createParentIfNeededForCollection(mapRef, parentIfExists, name, objectType)
    val entrySet = invokeSimple(threadReference, mapRef, "entrySet") as ObjectReference?
    val iterator = getIterator(threadReference, entrySet)
    var i = 0
    while (iterator.hasNext()) {
      val entry = iterator.next() as ObjectReference
      val keyValue = invokeSimple(threadReference, entry, "getKey")
      val valueValue = invokeSimple(threadReference, entry, "getValue")

      val entryObject = ODObject(entry.uniqueID(), entry.referenceType().name(), i.toString())

      builder
          .addObject(entryObject, entry)
          .addLinkToObject(
              parent,
              entryObject,
              i.toString() + (if (parentIfExists != null) linkTypeIfExists else ""))

      if (keyValue != null) {
        this.convertValue(
            keyValue,
            KEY,
            if (keyValue.type() == null) "" else keyValue.type().name(),
            entryObject,
            KEY,
            remainingDepthToBeExplored)
      }
      if (valueValue != null) {
        this.convertValue(
            valueValue,
            VALUE,
            if (valueValue.type() == null) "" else valueValue.type().name(),
            entryObject,
            VALUE,
            remainingDepthToBeExplored)
      }
      i++
    }
  }

  private val threadReference: ThreadReference?
    get() = stackFrame.threadProxy()?.threadReference

  @Throws(EvaluateException::class)
  private fun analyzeVariable(localVariable: LocalVariableProxyImpl, stackFrame: IStackFrame) {
    val variableValue = stackFrame.getValue(localVariable)
    val variableName = localVariable.name()
    val variableType = localVariable.typeName()
    this.convertValue(variableValue, variableName, variableType, null, "", loadingDepth)
  }

  private fun convertValue(
      variableValue: Value?,
      variableName: String,
      variableType: String,
      parentIfExists: ODObject?,
      linkTypeIfExists: String,
      remainingDepthToBeExplored: Int
  ) {
    if (variableValue is BooleanValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is ByteValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is ShortValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is IntegerValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is LongValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is FloatValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is DoubleValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(variableName, variableType, value, parentIfExists)
      return
    }
    if (variableValue is CharValue) {
      val value: String = variableValue.value().toString()
      this.addVariableToDiagram(
          variableName, variableType, String.format("'%s'", value), parentIfExists)
      return
    }
    if (variableValue is StringReference) {
      val value: String = variableValue.value()
      this.addVariableToDiagram(
          variableName, variableType, String.format("\"%s\"", value), parentIfExists)
      return
    }
    val obj = variableValue as ObjectReference?
    if (obj == null) {
      if (showNullValues) {
        this.addVariableToDiagram(variableName, variableType, "null", parentIfExists)
      }
      return
    }

    val odObject = ODObject(obj.uniqueID(), variableType, variableName)
    this.exploreObject(obj, odObject, parentIfExists, linkTypeIfExists, remainingDepthToBeExplored)
  }

  private fun addVariableToDiagram(
      variableName: String,
      variableType: String,
      value: String,
      parentIfExists: ODObject?
  ) {
    if (parentIfExists != null) {
      builder.addAttributeToObject(parentIfExists, variableName, value, variableType)
    } else {
      builder.addPrimitiveRootValue(variableName, variableType, value)
    }
  }

  companion object {
    private const val KEY = "key"
    private const val VALUE = "value"
  }
}
