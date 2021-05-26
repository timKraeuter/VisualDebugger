package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.*;

public abstract class DebuggingInfoVisualizerBase implements DebuggingInfoVisualizer {
    protected ObjectDiagram diagram;

    DebuggingInfoVisualizerBase() {
        this.diagram = new ObjectDiagram();
    }

    @Override
    public DebuggingInfoVisualizer addObject(ODObject object) {
        this.diagram.addObject(object);
        return this;
    }

    @Override
    public DebuggingInfoVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType) {
        assert this.diagram.getObjects().contains(object);
        object.addAttribute(new ODAttributeValue(fieldName, fieldType, fieldValue));
        return this;
    }

    @Override
    public DebuggingInfoVisualizer addLinkToObject(ODObject from, ODObject to, String linkType) {
        assert this.diagram.getObjects().contains(from);
        assert this.diagram.getObjects().contains(to);
        from.addLink(new ODLink(from, to, linkType));
        return this;
    }

    @Override
    public void addPrimitiveRootValue(final String variableName, final String type, final String value) {
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue(variableName, type, value));
    }
}
