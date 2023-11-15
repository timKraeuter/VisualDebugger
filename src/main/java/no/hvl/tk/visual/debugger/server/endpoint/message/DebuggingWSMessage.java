package no.hvl.tk.visual.debugger.server.endpoint.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

public class DebuggingWSMessage {

    private static final Logger LOGGER = Logger.getInstance(DebuggingWSMessage.class);

    private final DebuggingMessageType type;
    private final String content;

    public DebuggingWSMessage(final DebuggingMessageType type, final String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return this.type.getTypeString();
    }

    public String getContent() {
        return this.content;
    }

    public String serialize() {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            LOGGER.error(e);
            return "JsonProcessingException: " + e;
        }
    }
}
