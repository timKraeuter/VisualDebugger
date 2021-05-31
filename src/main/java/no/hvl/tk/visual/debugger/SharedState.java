package no.hvl.tk.visual.debugger;

public class SharedState {
    private SharedState() {
    }

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    /**
     * The visualization depth set by the user.
     */
    public static int visualizationDepth = 10;

    /**
     * The visualization depth set by the user.
     */
    public static String last_plantuml_diagram = "";
}
