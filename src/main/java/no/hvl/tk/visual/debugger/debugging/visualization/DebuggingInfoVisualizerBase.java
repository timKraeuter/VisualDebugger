package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.openapi.diagnostic.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import no.hvl.tk.visual.debugger.domain.*;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;

public abstract class DebuggingInfoVisualizerBase implements DebuggingInfoVisualizer {
  private static final Logger LOGGER = Logger.getInstance(DebuggingInfoVisualizerBase.class);

  private ObjectDiagram previousDiagram;
  private Set<ODObject> previousRootObjects;
  private final Map<String, ODObject> objectMap;
  private final Set<Long> manuallyExploredObjects;

  protected ObjectDiagram diagram;
  private Set<ODObject> rootObjects;

  private String fileName;
  private Integer line;

  protected DebuggingInfoVisualizerBase() {
    this.diagram = new ObjectDiagram();
    this.previousDiagram = new ObjectDiagram();
    this.objectMap = new HashMap<>();
    this.rootObjects = new HashSet<>();
    manuallyExploredObjects = new HashSet<>();
  }

  @Override
  public void addMetadata(String fileName, Integer line) {
    this.fileName = fileName;
    this.line = line;
  }

  @Override
  public void addAttributeToObject(
      final ODObject object,
      final String fieldName,
      final String fieldValue,
      final String fieldType) {
    object.addAttribute(new ODAttributeValue(fieldName, fieldType, fieldValue));
  }

  @Override
  public void addLinkToObject(final ODObject from, final ODObject to, final String linkType) {
    final ODLink linkToAdd = new ODLink(from, to, linkType);
    from.addLink(linkToAdd);
    this.diagram.addLink(linkToAdd);
  }

  @Override
  public void addPrimitiveRootValue(
      final String variableName, final String type, final String value) {
    this.diagram.addPrimitiveRootValue(new ODPrimitiveRootValue(variableName, type, value));
  }

  @Override
  public void addObject(final ODObject object, boolean root) {
    this.diagram.addObject(object);
    if (root) {
      this.rootObjects.add(object);
    }
  }

  @Override
  public ObjectDiagram getCurrentDiagram() {
    return this.diagram;
  }

  @Override
  public void reprintPreviousDiagram() {
    this.diagram = previousDiagram;
    this.rootObjects = previousRootObjects;
    this.finishVisualization();
  }

  @Override
  public void resetDiagram() {
    this.previousDiagram = this.diagram;
    this.diagram = new ObjectDiagram();
    this.previousRootObjects = this.rootObjects;
    this.rootObjects = new HashSet<>();

    this.objectMap.clear();
    this.previousDiagram.getObjects().forEach(object -> objectMap.put(object.getId(), object));
  }

  @Override
  public void sessionStopped() {
    this.manuallyExploredObjects.clear();
  }

  @Override
  public ObjectDiagram getObjectWithChildrenFromPreviousDiagram(String objectId) {
    final ObjectDiagram objectDiagram = new ObjectDiagram();
    final ODObject odObject = this.objectMap.get(objectId);
    if (odObject != null) {
      manuallyExploredObjects.add(odObject.getIdAsLong());
      objectDiagram.addObject(odObject);
      odObject
          .getLinks()
          .forEach(
              odLink -> {
                final ODObject linkedObject = odLink.getTo();
                objectDiagram.addObject(linkedObject);
                objectDiagram.addLink(odLink);
              });
    } else {
      LOGGER.warn(
          String.format("Object with id \"%s\" does not exist in the object diagram!", objectId));
    }
    return objectDiagram;
  }

  protected ObjectDiagram getDiagramWithDepth() {
    return this.getDiagramWithDepth(PluginSettingsState.getInstance().getVisualisationDepth());
  }

  protected ObjectDiagram getDiagramWithDepth(Integer depth) {
    ObjectDiagram diagramWithDepth = new ObjectDiagram();
    Set<ODObject> seenObjects = new HashSet<>();
    this.rootObjects.forEach(
        odObject -> {
          diagramWithDepth.addObject(odObject);
          this.addFurtherObjectsRespectingDepth(diagramWithDepth, depth, odObject, seenObjects);
        });
    this.diagram.getPrimitiveRootValues().forEach(diagramWithDepth::addPrimitiveRootValue);

    return diagramWithDepth;
  }

  private void addFurtherObjectsRespectingDepth(
      ObjectDiagram diagramWithDepth, Integer depth, ODObject odObject, Set<ODObject> seenObjects) {
    if (seenObjects.contains(odObject)) {
      return;
    }
    if (depth <= 0 && !manuallyExploredObjects.contains(odObject.getIdAsLong())) {
      return;
    }
    seenObjects.add(odObject);
    odObject
        .getLinks()
        .forEach(
            odLink -> {
              ODObject to = odLink.getTo();
              diagramWithDepth.addObject(to);
              diagramWithDepth.addLink(odLink);

              this.addFurtherObjectsRespectingDepth(diagramWithDepth, depth - 1, to, seenObjects);
            });
  }

  /** Returns the file name of the file where the debugger has stopped or null. */
  public String getFileNameIfExists() {
    return fileName;
  }

  /** Returns the line in the file where the debugger has stopped or null. */
  public int getLineIfExists() {
    return line;
  }
}
