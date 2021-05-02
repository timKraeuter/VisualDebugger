package no.hvl.tk.visualDebugger.debugging.visualization;

import no.hvl.tk.visualDebugger.domain.ODObject;

public interface DebuggingVisualizer {

    DebuggingVisualizer addObject(ODObject object);

    // Maybe we need type here because "1" not equal to 1.
    DebuggingVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue);

    DebuggingVisualizer addLinkToObject(String objectFromName, String objectToName, String linkName);

    void addPrimitiveRootValue(String variableName, String type, String value);

    void finishVisualization();
}
