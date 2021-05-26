package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class ConsoleDebuggingInfoVisualizer extends DebuggingInfoVisualizerBase {
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
        // Reset diagram
        this.diagram = new ObjectDiagram();
    }
}
