package no.hvl.tk.visual.debugger.webAPI;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.webAPI.endpoint.DebugAPIEndpoint;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class can start a websocket server which runs an API which provides the client with live debug data.
 * See {@link DebugAPIEndpoint} for the Endpoint.
 */
public class DebugAPIServerStarter {
    private static final Logger LOGGER = Logger.getInstance(DebugAPIServerStarter.class);


    public static Server runNewServer() {
        final Server server = new Server(ServerConstants.HOST_NAME, ServerConstants.DEBUG_SERVER_PORT, "", new HashMap<>(), DebugAPIEndpoint.class);
        try {
            server.start();
            LOGGER.info("Debug API server started successfully.");
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