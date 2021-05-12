package no.hvl.tk.visualDebugger.debugging.visualization;

import no.hvl.tk.visualDebugger.domain.ODObject;
import no.hvl.tk.visualDebugger.domain.ODPrimitiveRootValue;

public class ConsoleDebuggingVisualizer extends InformationCollectorVisualizer {
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
