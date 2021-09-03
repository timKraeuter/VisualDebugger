package no.hvl.tk.visual.debugger;

import com.intellij.debugger.engine.SuspendContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.sun.jdi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DebugProcessListener implements XDebuggerManagerListener {
    private static final Logger LOGGER = Logger.getInstance(XDebuggerManagerListener.class);

    @Override
    public void processStarted(@NotNull final XDebugProcess debugProcess) {
        final XDebugSession debugSession = debugProcess.getSession();
        debugSession.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                SuspendContext sc = (SuspendContext) debugSession.getSuspendContext();
                ThreadReference thread = sc.getThread().getThreadReference();
                try {
                    for (StackFrame stackFrame : thread.frames()) {
                        // TODO find the right stackframe.
                        ObjectReference objectReference = stackFrame.thisObject();
                        assert objectReference != null;

                        // TODO convert object here and store it for future loading if needed.
                        long thisID = objectReference.uniqueID();
                        Map<Field, Value> fields = objectReference.getValues(objectReference.referenceType().allFields());
                        // TODO convert each field value recursively until the wanted depth is reached.
                    }
                } catch (IncompatibleThreadStateException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}