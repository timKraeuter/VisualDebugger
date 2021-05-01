package no.hvl.tk.visualDebugger.debugging;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingVisualizer;

import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);

    public static final int default_debugging_depth = 3;

    private XDebugSession debugSession;
    private int depth;
    private DebuggingVisualizer debuggingVisualizer;

    public DebugListener(final XDebugSession debugSession, final DebuggingVisualizer debuggingVisualizer) {
        this(debugSession, debuggingVisualizer, default_debugging_depth);
    }

    public DebugListener(
            final XDebugSession debugSession,
            final DebuggingVisualizer debuggingVisualizer,
            final int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Debugging depth cannot be negative.");
        }
        Objects.requireNonNull(debugSession, "Debug session must not be null.");
        this.debugSession = debugSession;
        this.debuggingVisualizer = debuggingVisualizer;
        this.depth = depth;
    }

    @Override
    public void sessionPaused() {
        LOGGER.debug("Next step in debugger!");

        final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
        Objects.requireNonNull(currentStackFrame, "Stack frame unexpectedly was null.");

        final NodeDebugVisualizer debugVisualizer = new NodeDebugVisualizer(this.debuggingVisualizer, this.depth);
        currentStackFrame.computeChildren(debugVisualizer);
        this.debuggingVisualizer.finishVisualization();
        this.debuggingVisualizer.reset();
    }
}
