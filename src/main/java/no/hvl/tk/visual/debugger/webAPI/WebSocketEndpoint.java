package no.hvl.tk.visual.debugger.webAPI;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import no.hvl.tk.visual.debugger.SharedState;

@ServerEndpoint("/socket")
public class WebSocketEndpoint {
    // One gets one instance of this class per session/client.

    @OnOpen
    public static void onOpen(final Session session) {
        System.out.println("Session open. ID: " + session.getId());
        SharedState.addWebsocketClient(session);
    }

    @OnClose
    public static void onClose(final Session session) {
        System.out.println("Session closed. ID: " + session.getId());
        SharedState.removeWebsocketClient(session);
    }

    @OnMessage
    public static String handleTextMessage(final String message) {
        System.out.println("New Text Message Received: " + message);
        return "test";
    }
}