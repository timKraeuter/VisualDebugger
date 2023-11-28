package no.hvl.tk.visual.debugger.domain;

import com.intellij.openapi.util.Pair;
import com.sun.jdi.ObjectReference;
import java.util.HashMap;
import java.util.Map;

public class ObjectDiagramBuilder {

  private final Map<String, Pair<ODObject, ObjectReference>> objectRefMap;

  private final ObjectDiagram diagram;

  public ObjectDiagramBuilder() {
    this.objectRefMap = new HashMap<>();
    this.diagram = new ObjectDiagram();
  }

  public Map<String, Pair<ODObject, ObjectReference>> getObjectRefMap() {
    return objectRefMap;
  }

  public ObjectDiagramBuilder addObject(final ODObject object, ObjectReference objectReference) {
    this.diagram.addObject(object);
    this.objectRefMap.put(object.getId(), Pair.create(object, objectReference));
    return this;
  }

  public ObjectDiagramBuilder addAttributeToObject(
      final ODObject object,
      final String fieldName,
      final String fieldValue,
      final String fieldType) {
    object.addAttribute(new ODAttributeValue(fieldName, fieldType, fieldValue));
    return this;
  }

  public ObjectDiagramBuilder addLinkToObject(
      final ODObject from, final ODObject to, final String linkType) {
    final ODLink linkToAdd = new ODLink(from, to, linkType);
    from.addLink(linkToAdd);
    this.diagram.addLink(linkToAdd);
    return this;
  }

  public ObjectDiagramBuilder addPrimitiveRootValue(
      final String variableName, final String type, final String value) {
    this.diagram.addPrimitiveRootValue(new ODPrimitiveRootValue(variableName, type, value));
    return this;
  }

  public ObjectDiagram build() {
    return this.diagram;
  }
}
