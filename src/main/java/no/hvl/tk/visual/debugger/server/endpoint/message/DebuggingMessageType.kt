package no.hvl.tk.visual.debugger.server.endpoint.message

enum class DebuggingMessageType(val typeString: String) {
    CONFIG("config"),
    NEXT_DEBUG_STEP("nextDebugStep"),
    LOAD_CHILDREN("loadChildren"),
    ERROR("error")
}
