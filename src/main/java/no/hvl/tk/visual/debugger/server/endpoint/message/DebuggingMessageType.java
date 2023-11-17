package no.hvl.tk.visual.debugger.server.endpoint.message;

public enum DebuggingMessageType {
  CONFIG("config"),
  NEXT_DEBUG_STEP("nextDebugStep"),
  LOAD_CHILDREN("loadChildren"),
  ERROR("error");

  private final String type;

  DebuggingMessageType(final String type) {
    this.type = type;
  }

  public String getTypeString() {
    return this.type;
  }
}
