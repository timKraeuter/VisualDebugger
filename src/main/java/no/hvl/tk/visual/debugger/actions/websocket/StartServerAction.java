package no.hvl.tk.visual.debugger.actions.websocket;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.util.ClassloaderUtil;
import no.hvl.tk.visual.debugger.webAPI.WebSocketServer;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;

public class StartServerAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        ClassloaderUtil.runWithContextClassloader(() -> {
            final Server server = WebSocketServer.runServer();
            SharedState.setServer(server);
            return null; // needed because of generic method.
        });
    }
}
