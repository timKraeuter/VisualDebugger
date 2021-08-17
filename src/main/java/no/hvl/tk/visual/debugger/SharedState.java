package no.hvl.tk.visual.debugger;

import no.hvl.tk.visual.debugger.debugging.DebugListener;

public class SharedState {


    private SharedState() {
    }

    private static boolean debuggingActive = false;

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    private static DebugListener debugListener;
    private static String lastPlantUMLDiagram = "";

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

    public static DebugListener getDebugListener() {
        return debugListener;
    }

    public static void setDebugListener(final DebugListener debugListener) {
        SharedState.debugListener = debugListener;
    }
}
