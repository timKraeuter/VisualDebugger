package no.hvl.tk.visualDebugger.debugging;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import no.hvl.tk.visualDebugger.debugging.visualization.ConsoleDebuggingVisualizer;
import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingVisualizer;

import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);

    public static final int default_debugging_depth = 10; // TODO increased to 10 for testing

    private XDebugSession debugSession;
    private int depth;

    public DebugListener(final XDebugSession debugSession) {
        this(debugSession, default_debugging_depth);
    }

    public DebugListener(
            final XDebugSession debugSession,
            final int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Debugging depth cannot be negative.");
        }
        Objects.requireNonNull(debugSession, "Debug session must not be null.");
        this.debugSession = debugSession;
        this.depth = depth;
    }

    @Override
    public void sessionPaused() {
        LOGGER.debug("Next step in debugger!");

        final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
        Objects.requireNonNull(currentStackFrame, "Stack frame unexpectedly was null.");

        final DebuggingVisualizer debuggingVisualizer = new ConsoleDebuggingVisualizer();
        final CounterBasedLock lock = new CounterBasedLock();
        final NodeDebugVisualizer nodeVisualizer = new NodeDebugVisualizer(
                debuggingVisualizer,
                this.depth,
                lock);
        // Happens in a different thread!
        currentStackFrame.computeChildren(nodeVisualizer);
        new Thread(() -> {
            // Wait for the computation to be over
            lock.lock();
            debuggingVisualizer.finishVisualization();
        }).start();
    }

    @Override
    public void stackFrameChanged() {
        System.out.println("Stack frame changed");
    }
}
