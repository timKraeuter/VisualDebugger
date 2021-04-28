package no.hvl.tk.visualDebugger.visualization;

public interface DebuggingVisualizer {

    DebuggingVisualizer addObject(String type, String name);

    // Maybe we need type here because "1" not equal to 1.
    DebuggingVisualizer addAttributeToObject(String objectName, String fieldName, String fieldValue);

    DebuggingVisualizer addLinkToObject(String objectFromName, String objectToName, String linkName);

    void finishVisualization();
}
