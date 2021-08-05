package no.hvl.tk.visual.debugger.actions.websocket.startServer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import no.hvl.tk.visual.debugger.webAPI.WebSocketServer;
import org.jetbrains.annotations.NotNull;

public class StartServerAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            // code working with ServiceLoader here
            WebSocketServer.runServer();
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
}
