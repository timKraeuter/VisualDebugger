package no.hvl.tk.visual.debugger.server.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;

public record UIConfig(Integer savedDebugSteps, boolean coloredDiff) {
    private static final Logger LOGGER = Logger.getInstance(UIConfig.class);

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
