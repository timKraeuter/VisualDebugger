package no.hvl.tk.visual.debugger.webAPI;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/socket")
public class WebSocketEndpoint {
    // one gets one instance of this class per connection.

    private Session client;

    @OnOpen
    public void onOpen(final Session session) {
        System.out.println("Session open. ID: " + session.getId());
        this.client = session;
    }

    @OnClose
    public void onClose(final Session session) {
        System.out.println("Session closed. ID: " + session.getId());
        this.client = null;
    }

    @OnMessage
    public String handleTextMessage(final String message) {
        System.out.println("New Text Message Received: " + message);
        // Send additional message to test websocket
        this.sendMessageToClient(message);
        return "test";
    }

    private void sendMessageToClient(final String message) {
        if (this.client != null) {
            try {
                this.client.getBasicRemote().sendText(message);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}