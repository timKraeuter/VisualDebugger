package no.hvl.tk.visual.debugger.actions.stop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import no.hvl.tk.visual.debugger.SharedState;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;

public class StopAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(StopAction.class);

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        StopAction.stopDebugAPIServer();
        StopAction.stopUIServer();

        SharedState.setDebuggingActive(false);
        SharedState.getDebugListener().addActivateDebuggingButton();
    }

    private static void stopUIServer() {
        final HttpServer server = SharedState.getUiServer();
        if (server != null) {
            server.shutdownNow();
            LOGGER.info("UI server stopped.");
            SharedState.setUIServer(null);
        }

    }

    private static void stopDebugAPIServer() {
        final Server server = SharedState.getDebugAPIServer();
        if (server != null) {
            server.stop();
            LOGGER.info("Debug API server stopped.");
            SharedState.setDebugAPIServer(null);
        }
    }
}
