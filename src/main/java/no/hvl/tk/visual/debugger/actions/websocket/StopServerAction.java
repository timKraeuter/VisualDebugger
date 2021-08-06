package no.hvl.tk.visual.debugger.actions.websocket;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.SharedState;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;

public class StopServerAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Server server = SharedState.getServer();
        if (server != null) {
            server.stop();
            SharedState.setServer(null);
        }
    }
}
