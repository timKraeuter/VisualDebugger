package no.hvl.tk.visual.debugger;

import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManagerListener;
import org.jetbrains.annotations.NotNull;

public class DebugProcessListener implements XDebuggerManagerListener {

    @Override
    public void processStarted(@NotNull final XDebugProcess debugProcess) {
        final XDebugSession debugSession = debugProcess.getSession();
        debugSession.addSessionListener(new StackFrameSessionListener(debugSession));
    }
}