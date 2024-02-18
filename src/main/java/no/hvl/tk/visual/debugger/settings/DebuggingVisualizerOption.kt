package no.hvl.tk.visual.debugger.settings

enum class DebuggingVisualizerOption {
  /** Web ui visualizer using javascript and the browser. */
  WEB_UI,

  /** Embedded visualizer using plant uml. */
  EMBEDDED;

  override fun toString(): String {
    return when (this) {
      WEB_UI -> "Browser visualizer"
      EMBEDDED -> "Embedded visualizer (no interaction)"
    }
  }
}
