package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.sun.jdi.ObjectReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameAnalyzer;
import no.hvl.tk.visual.debugger.domain.*;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;

public abstract class DebuggingInfoVisualizerBase implements DebuggingInfoVisualizer {

  private static final Logger LOGGER = Logger.getInstance(DebuggingInfoVisualizerBase.class);

  private ObjectDiagram previousDiagram;
  private Set<ODObject> previousRootObjects;
  private final Map<String, ODObject> objectMap;
  private final Map<String, ObjectReference> objectRefMap;

  protected ObjectDiagram diagram;
  private Set<ODObject> rootObjects;

  private String fileName;
  private Integer line;
  private StackFrameProxyImpl stackframe;

  protected DebuggingInfoVisualizerBase() {
    this.diagram = new ObjectDiagram();
    this.previousDiagram = new ObjectDiagram();
    this.objectMap = new HashMap<>();
    this.rootObjects = new HashSet<>();
    this.objectRefMap = new HashMap<>();
  }

  @Override
  public void addMetadata(String fileName, Integer line, StackFrameProxyImpl stackframe) {
    this.fileName = fileName;
    this.line = line;
    this.stackframe = stackframe;
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
  public void addObject(final ODObject object, boolean root, ObjectReference objectReference) {
    this.diagram.addObject(object);
    // TODO: when to clear this map?
    this.objectRefMap.put(object.getId(), objectReference);
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
    SharedState.getManuallyExploredObjects().clear();
  }

  @Override
  public ObjectDiagram getObjectWithChildrenFromPreviousDiagram(String objectId) {
    DebuggingInfoCollector infoCollector = new DebuggingInfoCollector();
    final ODObject odObject = this.objectMap.get(objectId);
    ObjectReference objectReference = this.objectRefMap.get(objectId);
    if (odObject != null && objectReference != null) {
      infoCollector.addObject(odObject, true, null);

      SharedState.getManuallyExploredObjects().add(odObject.getIdAsLong());

      // Explore the object --> Should explore one level deeper but not duplicate the attributes!
      new StackFrameAnalyzer(stackframe,
          infoCollector, 1, new HashSet<>()).exploreObject(objectReference, odObject, null, "", 1);

    } else {
      LOGGER.warn(
          String.format("Object with id \"%s\" does not exist in the object diagram!", objectId));
    }
    return infoCollector.diagram;
  }

  // TODO: Should not be needed anymore at the end.
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
    if (depth <= 0 && !SharedState.getManuallyExploredObjects().contains(odObject.getIdAsLong())) {
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

  /**
   * Returns the file name of the file where the debugger has stopped or null.
   */
  public String getFileNameIfExists() {
    return fileName;
  }

  /**
   * Returns the line in the file where the debugger has stopped or null.
   */
  public int getLineIfExists() {
    return line;
  }
}
