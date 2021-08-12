package no.hvl.tk.visual.debugger.webAPI.endpoint.message;

public enum WebsocketMessageType {
    NEXT_DEBUG_STEP("nextDebugStep"),
    LOAD_CHILDREN("loadChildren"),
    ERROR("error");

    private final String type;

    WebsocketMessageType(final String type) {
        this.type = type;
    }

    public String getTypeString() {
        return this.type;
    }
}
