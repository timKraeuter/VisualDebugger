package no.hvl.tk.visual.debugger.actions.stop;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.SharedState;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;

public class StopAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        stopWebsocketDebuggingServer();
        
        SharedState.setDebuggingActive(false);
        SharedState.getDebugListener().addActivateDebuggingButton();
    }

    private static void stopWebsocketDebuggingServer() {
        final Server server = SharedState.getServer();
        if (server != null) {
            server.stop();
            SharedState.setServer(null);
        }
    }
}
