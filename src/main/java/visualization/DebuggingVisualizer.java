package visualization;

public interface DebuggingVisualizer {

    public DebuggingVisualizer addObject(String type, String name);

    // Maybe we need type here because "1" not equal to 1.
    public DebuggingVisualizer addAttributeToObject(String objectName, String fieldName, String fieldValue);

    public DebuggingVisualizer addLinkToObject(String objectFromName, String objectToName, String linkName);

    public void finishVisualization();
}
