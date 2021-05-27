package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.ODObject;

public interface DebuggingInfoVisualizer {

    DebuggingInfoVisualizer addObject(ODObject object);

    DebuggingInfoVisualizer addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType);

    DebuggingInfoVisualizer addLinkToObject(ODObject from, ODObject to, String linkType);

    void addPrimitiveRootValue(String variableName, String type, String value);

    void finishVisualization();
}
