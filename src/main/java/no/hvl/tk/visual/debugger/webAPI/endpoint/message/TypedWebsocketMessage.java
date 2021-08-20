package no.hvl.tk.visual.debugger.webAPI.endpoint.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

public class TypedWebsocketMessage {

    private static final Logger LOGGER = Logger.getInstance(TypedWebsocketMessage.class);

    private final WebsocketMessageType type;
    private final String content;

    public TypedWebsocketMessage(final WebsocketMessageType type, final String content) {
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
            throw new RuntimeException(e);
        }
    }
}
