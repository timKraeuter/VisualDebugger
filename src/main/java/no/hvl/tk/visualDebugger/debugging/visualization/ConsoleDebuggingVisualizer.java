package no.hvl.tk.visualDebugger.debugging.visualization;

import com.intellij.util.concurrency.Semaphore;
import no.hvl.tk.visualDebugger.domain.ODAttributeValue;
import no.hvl.tk.visualDebugger.domain.ODObject;
import no.hvl.tk.visualDebugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visualDebugger.domain.ObjectDiagram;

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
    public DebuggingVisualizer addLinkToObject(String objectFromName, String objectToName, String linkName) {
        return null;
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
    }
}
