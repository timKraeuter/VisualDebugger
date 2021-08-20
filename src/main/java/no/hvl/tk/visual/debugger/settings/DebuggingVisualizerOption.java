package no.hvl.tk.visual.debugger.settings;

public enum DebuggingVisualizerOption {
    /**
     * Web ui visualizer using javascript and the browser.
     */
    WEB_UI,
    /**
     * Embedded visualizer using plant uml.
     */
    EMBEDDED;

    @Override
    public String toString() {
        switch (this) {
            case WEB_UI:
                return "Browser visualizer";
            case EMBEDDED:
                return "Embedded visualizer (no interaction)";
            default:
                throw new Error("Uncovered visualizer option!");
        }
    }
}
