package no.hvl.tk.visual.debugger.webAPI;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.webAPI.endpoint.WebSocketEndpoint;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.util.HashMap;


public class WebSocketServer {
    private static final Logger LOGGER = Logger.getInstance(WebSocketServer.class);

    public static final String HOST_NAME = "localhost";
    public static final int PORT = 8080;

    public static Server runServer() {
        final Server server = new Server(HOST_NAME, PORT, "", new HashMap<>(), WebSocketEndpoint.class);
        try {
            server.start();
            LOGGER.info("Websocket server started successfully.");
            return server;
        } catch (final Exception e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the given message to the given client, if the client is not null.
     *
     * @param client  client.
     * @param message message for the client.
     */
    public static void sendMessageToClient(final Session client, final String message) {
        if (client != null) {
            try {
                client.getBasicRemote().sendText(message);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}