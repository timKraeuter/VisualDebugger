package no.hvl.tk.visualDebugger.debugging;

import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import no.hvl.tk.visualDebugger.DebuggVisualizerListener;
import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingVisualizer;
import no.hvl.tk.visualDebugger.debugging.visualization.PlantUmlVisualizer;

import javax.swing.*;
import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";

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

        initUI();

        final DebuggingVisualizer debuggingVisualizer = new PlantUmlVisualizer();
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

    private void initUI() {
        final JPanel panel = new JPanel();
        panel.add(new JLabel("Test", SwingConstants.CENTER));

        final SimpleToolWindowPanel container = new SimpleToolWindowPanel(false, true);
        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(
                "DebugVisualizer.VisualizerToolbar",
                (DefaultActionGroup) actionManager.getAction("DebugVisualizer.VisualizerToolbar"),
                false
        );
        actionToolbar.setTargetComponent(panel);
        container.setToolbar(actionToolbar.getComponent());
        container.setContent(panel);

        RunnerLayoutUi ui = debugSession.getUI();
        Content content = ui.createContent(
                CONTENT_ID,
                container,
                "Visual Debugger",
                IconLoader.getIcon("/icons/viz.png", DebuggVisualizerListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
    }
}
