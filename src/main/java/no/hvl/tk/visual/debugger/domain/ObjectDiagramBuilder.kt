package no.hvl.tk.visual.debugger.domain

import com.intellij.openapi.util.Pair
import com.sun.jdi.ObjectReference

class ObjectDiagramBuilder {
  private val objectRefMap: MutableMap<String, Pair<ODObject, ObjectReference>> = HashMap()

  private val diagram = ObjectDiagram()

  fun getObjectRefMap(): Map<String, Pair<ODObject, ObjectReference>> {
    return objectRefMap
  }

  fun addObject(`object`: ODObject, objectReference: ObjectReference): ObjectDiagramBuilder {
    diagram.addObject(`object`)
    objectRefMap[`object`.id] = Pair.create(`object`, objectReference)
    return this
  }

  fun addAttributeToObject(
      `object`: ODObject,
      fieldName: String?,
      fieldValue: String?,
      fieldType: String?
  ): ObjectDiagramBuilder {
    `object`.addAttribute(ODAttributeValue(fieldName, fieldType, fieldValue))
    return this
  }

  fun addLinkToObject(from: ODObject, to: ODObject?, linkType: String?): ObjectDiagramBuilder {
    val linkToAdd = ODLink(from, to, linkType)
    from.addLink(linkToAdd)
    diagram.addLink(linkToAdd)
    return this
  }

  fun addPrimitiveRootValue(
      variableName: String?,
      type: String?,
      value: String?
  ): ObjectDiagramBuilder {
    diagram.addPrimitiveRootValue(ODPrimitiveRootValue(variableName, type, value))
    return this
  }

  fun build(): ObjectDiagram {
    return this.diagram
  }
}
