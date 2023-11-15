package no.hvl.tk.visual.debugger.server.endpoint.message;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TypedWebsocketMessageTest {

    private final String content = "content";

    @Test
    void serializeLoadChildren() {
        String serializedMessage = new DebuggingWSMessage(DebuggingMessageType.LOAD_CHILDREN, content).serialize();
        assertThat(serializedMessage, is("{\"type\":\"loadChildren\",\"content\":\"content\"}"));
    }

    @Test
    void serializeError() {
        String serializedMessage = new DebuggingWSMessage(DebuggingMessageType.ERROR, content).serialize();
        assertThat(serializedMessage, is("{\"type\":\"error\",\"content\":\"content\"}"));
    }

    @Test
    void serializeNextDebugStep() {
        String serializedMessage = new DebuggingWSMessage(DebuggingMessageType.NEXT_DEBUG_STEP, content).serialize();
        assertThat(serializedMessage, is("{\"type\":\"nextDebugStep\",\"content\":\"content\"}"));
    }
}