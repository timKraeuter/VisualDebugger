package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public interface DebuggingInfoVisualizer {

    void addMetadata(String fileName, Integer line);

    void addObject(ODObject object, boolean root);

    ObjectDiagram getObjectWithChildrenFromPreviousDiagram(String objectId);

    void addAttributeToObject(ODObject object, String fieldName, String fieldValue, String fieldType);

    void addLinkToObject(ODObject from, ODObject to, String linkType);

    void addPrimitiveRootValue(String variableName, String type, String value);

    void finishVisualization();

    void debuggingActivated();

    void debuggingDeactivated();

    ObjectDiagram getCurrentDiagram();

    /**
     * Saves the current diagram as the previous diagram.
     * Resets the current diagram to an empty diagram.
     */
    void resetDiagram();

    void reprintPreviousDiagram();

    void sessionStopped();
}
