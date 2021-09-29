package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.*;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DebuggingInfoVisualizerBase implements DebuggingInfoVisualizer {
    private ObjectDiagram diagram;
    protected Map<Long, ODObject> objectMap;

    private final Set<ODObject> rootObjects;

    protected DebuggingInfoVisualizerBase() {
        this.diagram = new ObjectDiagram();
        this.objectMap = new HashMap<>();
        this.rootObjects = new HashSet<>();
    }


    @Override
    public DebuggingInfoVisualizer addAttributeToObject(final ODObject object, final String fieldName, final String fieldValue, final String fieldType) {
        object.addAttribute(new ODAttributeValue(fieldName, fieldType, fieldValue));
        return this;
    }

    @Override
    public DebuggingInfoVisualizer addLinkToObject(final ODObject from, final ODObject to, final String linkType) {
        final ODLink linkToAdd = new ODLink(from, to, linkType);
        from.addLink(linkToAdd);
        this.diagram.addLink(linkToAdd);
        return this;
    }

    @Override
    public void addPrimitiveRootValue(final String variableName, final String type, final String value) {
        this.diagram.addPrimitiveRootValue(new ODPrimitiveRootValue(variableName, type, value));
    }

    @Override
    public DebuggingInfoVisualizer addObject(final ODObject object, boolean root) {
        this.diagram.addObject(object);
        this.objectMap.put(object.getIdAsLong(), object);
        if (root) {
            this.rootObjects.add(object);
        }
        return this;
    }

    @Override
    public ObjectDiagram getDiagram() {
        return this.diagram;
    }

    @Override
    public void setDiagram(ObjectDiagram objectDiagram) {
        this.diagram = objectDiagram;
    }

    @Override
    public ObjectDiagram getDiagramIncludingObject(String objectId) {
        // TODO
        return null;
    }

    protected ObjectDiagram getDiagramWithDepth() {
        return this.getDiagramWithDepth(PluginSettingsState.getInstance().getVisualisationDepth());
    }

    protected ObjectDiagram getDiagramWithDepth(Integer depth) {
        ObjectDiagram diagramWithDepth = new ObjectDiagram();
        Set<ODObject> seenObjects = new HashSet<>();
        this.rootObjects.forEach(odObject -> {
            diagramWithDepth.addObject(odObject);
            this.addFurtherObjectsRespectingDepth(diagramWithDepth, depth, odObject, seenObjects);
        });
        this.diagram.getPrimitiveRootValues().forEach(diagramWithDepth::addPrimitiveRootValue);

        return diagramWithDepth;
    }

    private void addFurtherObjectsRespectingDepth(
            ObjectDiagram diagramWithDepth,
            Integer depth,
            ODObject odObject,
            Set<ODObject> seenObjects) {
        if (depth <= 0 || seenObjects.contains(odObject)) {
            return;
        }
        seenObjects.add(odObject);
        odObject.getLinks().forEach(odLink -> {
            ODObject to = odLink.getTo();
            diagramWithDepth.addObject(to);
            diagramWithDepth.addLink(odLink);

            this.addFurtherObjectsRespectingDepth(diagramWithDepth, depth - 1, to, seenObjects);
        });
    }
}
