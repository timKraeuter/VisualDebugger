package no.hvl.tk.visual.debugger;

public class SharedState {
    private SharedState() {
    }

    private static boolean debuggingActive = false;

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private static int visualizationDepth = 5;
    private static String lastPlantUMLDiagram = "";

    public static int getVisualizationDepth() {
        return visualizationDepth;
    }

    public static void setVisualizationDepth(final int visualizationDepth) {
        SharedState.visualizationDepth = visualizationDepth;
    }

    public static String getLastPlantUMLDiagram() {
        return lastPlantUMLDiagram;
    }

    public static void setLastPlantUMLDiagram(final String diagram) {
        lastPlantUMLDiagram = diagram;
    }

    public static boolean isDebuggingActive() {
        return debuggingActive;
    }

    public static void setDebuggingActive(final boolean debuggingActive) {
        SharedState.debuggingActive = debuggingActive;
    }
}
