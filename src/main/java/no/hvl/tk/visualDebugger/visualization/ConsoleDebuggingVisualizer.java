package no.hvl.tk.visualDebugger.visualization;

public class ConsoleDebuggingVisualizer implements DebuggingVisualizer {
    public ConsoleDebuggingVisualizer() {
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

    }
}
