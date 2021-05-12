package no.hvl.tk.visualDebugger.debugging.visualization;

import no.hvl.tk.visualDebugger.domain.*;

public abstract class InformationCollectorVisualizer implements DebuggingVisualizer {
    protected ObjectDiagram diagram;

    public InformationCollectorVisualizer() {
        this.diagram = new ObjectDiagram();
    }

    @Override
    public DebuggingVisualizer addObject(ODObject object) {
        this.diagram.addObject(object);
        return this;
    }

    @Override
    public DebuggingVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType) {
        assert this.diagram.getObjects().contains(object);
        object.addAttribute(new ODAttributeValue(fieldName, fieldType, fieldValue));
        return this;
    }

    @Override
    public DebuggingVisualizer addLinkToObject(ODObject from, ODObject to, String linkType) {
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
