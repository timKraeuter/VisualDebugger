package no.hvl.tk.visualDebugger.debugging.visualization;

import com.intellij.util.concurrency.Semaphore;
import no.hvl.tk.visualDebugger.domain.*;

public class ConsoleDebuggingVisualizer implements DebuggingVisualizer {
    private ObjectDiagram diagram;

    public ConsoleDebuggingVisualizer() {
        this.diagram = new ObjectDiagram();
    }

    @Override
    public DebuggingVisualizer addObject(ODObject object) {
        this.diagram.addObject(object);
        return this;
    }

    @Override
    public DebuggingVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue) {
        assert this.diagram.getObjects().contains(object);
        object.addAttribute(new ODAttributeValue(fieldName, fieldValue));
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

    @Override
    public void finishVisualization() {
        for (final ODPrimitiveRootValue primitiveRootValue : diagram.getPrimitiveRootValues()) {
            System.out.printf(
                    "%s=%s (%s) \n",
                    primitiveRootValue.getVariableName(),
                    primitiveRootValue.getValue(),
                    primitiveRootValue.getType());
        }
        for (final ODObject object : diagram.getObjects()) {
            System.out.println(object);
        }
        // print new line.
        System.out.println();
    }
}
