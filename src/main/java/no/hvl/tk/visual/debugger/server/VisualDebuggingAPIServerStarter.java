package no.hvl.tk.visual.debugger.server;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.websocket.Session;
import no.hvl.tk.visual.debugger.server.endpoint.UIConfig;
import no.hvl.tk.visual.debugger.server.endpoint.VisualDebuggingAPIEndpoint;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingWSMessage;
import no.hvl.tk.visual.debugger.server.endpoint.message.DebuggingMessageType;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;
import org.glassfish.tyrus.server.Server;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class can start a websocket server which runs an API which provides the client with live debug data.
 * See {@link VisualDebuggingAPIEndpoint} for the Endpoint.
 */
public class VisualDebuggingAPIServerStarter {
    private static final Logger LOGGER = Logger.getInstance(VisualDebuggingAPIServerStarter.class);

    private VisualDebuggingAPIServerStarter() {
        // Only helper methods.
    }

    public static Server runNewServer() {
        final Server server = new Server(
                ServerConstants.HOST_NAME,
                ServerConstants.VISUAL_DEBUGGING_API_SERVER_PORT,
                "",
                new HashMap<>(),
                VisualDebuggingAPIEndpoint.class);
        try {
            server.start();
            LOGGER.info("Debug API server started successfully.");
            return server;
        } catch (final Exception e) {
            LOGGER.error(e);
            return null;
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
                LOGGER.error(e);
            }
        }
    }

    /**
     * Sends the configuration to the client.
     * @param client client
     */
    public static void sendUIConfig(Session client) {
        UIConfig uiConfig = PluginSettingsState.getInstance().getUIConfig();
        final DebuggingWSMessage configMessage = new DebuggingWSMessage(
                DebuggingMessageType.CONFIG,
                uiConfig.serialize());

        sendMessageToClient(client, configMessage.serialize());
    }

}