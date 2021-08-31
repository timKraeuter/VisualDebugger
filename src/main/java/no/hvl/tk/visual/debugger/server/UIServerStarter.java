package no.hvl.tk.visual.debugger.server;

import com.intellij.openapi.diagnostic.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;

public class UIServerStarter {
    private static final Logger LOGGER = Logger.getInstance(UIServerStarter.class);

    private UIServerStarter() {
        // Only helper methods.
    }

    public static HttpServer runNewServer() {
        final HttpServer server = new HttpServer();
        final NetworkListener networkListener = new NetworkListener(
                "UI",
                ServerConstants.HOST_NAME,
                ServerConstants.UI_SERVER_PORT);
        server.addListener(networkListener);

        server.getServerConfiguration().addHttpHandler(
                new CLStaticHttpHandler(
                        UIServerStarter.class.getClassLoader(),
                        ServerConstants.STATIC_RESOURCE_PATH),
                "/");
        try {
            server.start();
            LOGGER.info("UI server started successfully.");
            return server;
        } catch (final Exception e) {
            LOGGER.error(e);
            return null;
        }
    }
}
