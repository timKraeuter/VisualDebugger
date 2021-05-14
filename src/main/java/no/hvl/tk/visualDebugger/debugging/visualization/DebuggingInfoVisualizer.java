package no.hvl.tk.visualDebugger.debugging.visualization;

import no.hvl.tk.visualDebugger.domain.ODObject;

public interface DebuggingInfoVisualizer {

    DebuggingInfoVisualizer addObject(ODObject object);

    // Maybe we need type here because "1" not equal to 1.
    DebuggingInfoVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType);

    DebuggingInfoVisualizer addLinkToObject(ODObject from, ODObject to, String linkType);

    void addPrimitiveRootValue(String variableName, String type, String value);

    void finishVisualization();
}
