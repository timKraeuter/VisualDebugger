package no.hvl.tk.visualDebugger.visualization;

import no.hvl.tk.visualDebugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visualDebugger.domain.ObjectDiagram;

public class ConsoleDebuggingVisualizer implements DebuggingVisualizer {
    private final ObjectDiagram diagram;

    public ConsoleDebuggingVisualizer() {
        this.diagram = new ObjectDiagram();
    }

    @Override
    public DebuggingVisualizer addObject(String type, String name) {
        return null;
    }

    @Override
    public DebuggingVisualizer addAttributeToObject(String objectName, String fieldName, String fieldValue) {
        return null;
    }

    @Override
    public DebuggingVisualizer addLinkToObject(String objectFromName, String objectToName, String linkName) {
        return null;
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
    }

    @Override
    public void addPrimitiveRootValue(final String variableName, final String type, final String value) {
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue(variableName, type, value));
    }
}
