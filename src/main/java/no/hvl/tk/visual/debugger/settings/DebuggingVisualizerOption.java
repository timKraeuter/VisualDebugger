package no.hvl.tk.visual.debugger.settings;

public enum DebuggingVisualizerOption {
  /** Web ui visualizer using javascript and the browser. */
  WEB_UI,
  /** Embedded visualizer using plant uml. */
  EMBEDDED;

  @Override
  public String toString() {
    return switch (this) {
      case WEB_UI -> "Browser visualizer";
      case EMBEDDED -> "Embedded visualizer (no interaction)";
    };
  }
}
