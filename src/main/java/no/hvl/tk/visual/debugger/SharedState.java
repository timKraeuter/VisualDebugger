package no.hvl.tk.visual.debugger;

public class SharedState {
    private SharedState() {
    }

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private static int visualizationDepth = 10;
    private static String lastPlantumlDiagram = "";

    public static int getVisualizationDepth() {
        return visualizationDepth;
    }

    public static void setVisualizationDepth(final int visualizationDepth) {
        SharedState.visualizationDepth = visualizationDepth;
    }

    public static String getLastPlantUMLDiagram() {
        return lastPlantumlDiagram;
    }

    public static void setLastPlantUMLDiagram(final String diagram) {
        lastPlantumlDiagram = diagram;
    }
}
