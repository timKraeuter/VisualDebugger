package no.hvl.tk.visual.debugger.webAPI;

import no.hvl.tk.visual.debugger.webAPI.endpoint.WebSocketEndpoint;
import org.glassfish.tyrus.server.Server;

import java.util.HashMap;


public class WebSocketServer {

    public static Server runServer() {
        final Server server = new Server("localhost", 8080, "", new HashMap<>(), WebSocketEndpoint.class);
        try {
            server.start();
            return server;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

}