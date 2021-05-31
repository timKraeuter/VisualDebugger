package no.hvl.tk.visual.debugger;

public class Settings {
    private Settings() {
    }

    /**
     * Decides if the visualisation of nodes for sets and lists should be skipped.
     */
    public static final boolean SKIP_COLLECTION_VISUALIZATION = true;

    /**
     * The visualization depth set by the user.
     */
    public static int VISUALIZATION_DEPTH = 10;
}
