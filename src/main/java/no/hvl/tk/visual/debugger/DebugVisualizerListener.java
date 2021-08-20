package no.hvl.tk.visual.debugger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManagerListener;
import no.hvl.tk.visual.debugger.debugging.DebugListener;
import org.jetbrains.annotations.NotNull;

public class DebugVisualizerListener implements XDebuggerManagerListener {
    private static final Logger LOGGER = Logger.getInstance(XDebuggerManagerListener.class);

    @Override
    public void processStarted(@NotNull final XDebugProcess debugProcess) {
        final XDebugSession debugSession = debugProcess.getSession();

        if (debugSession != null) {
            debugSession.addSessionListener(new DebugListener(debugSession));
        } else {
            LOGGER.info("No debugging session active but plugin was invoked.");
        }
    }
}