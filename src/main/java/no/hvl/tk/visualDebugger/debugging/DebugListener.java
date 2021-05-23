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
import no.hvl.tk.visualDebugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visualDebugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";

    public static final int default_debugging_depth = 10; // TODO increased to 10 for testing

    private final XDebugSession debugSession;
    private final int depth;
    private JPanel userInterface;
    private DebuggingInfoVisualizer debuggingVisualizer;

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
        initUIIfNeeded();

        final XStackFrame currentStackFrame = debugSession.getCurrentStackFrame();
        Objects.requireNonNull(currentStackFrame, "Stack frame unexpectedly was null.");

        final DebuggingInfoVisualizer debuggingInfoCollector = getDebuggingInfoVisualizer();
        final CounterBasedLock lock = new CounterBasedLock();
        final NodeDebugVisualizer nodeVisualizer = new NodeDebugVisualizer(
                debuggingInfoCollector,
                this.depth,
                lock);
        // Happens in a different thread!
        currentStackFrame.computeChildren(nodeVisualizer);
        new Thread(() -> {
            // Wait for the computation to be over
            lock.lock();
            debuggingInfoCollector.finishVisualization();
        }).start();
    }

    @NotNull
    private DebuggingInfoVisualizer getDebuggingInfoVisualizer() {
        if (debuggingVisualizer == null) {
            debuggingVisualizer = new PlantUmlDebuggingVisualizer(userInterface);
        }
        return debuggingVisualizer;
    }

    @Override
    public void stackFrameChanged() {
        System.out.println("Stack frame changed");
    }

    private void initUIIfNeeded() {
        if (userInterface != null) {
            return;
        }
        userInterface = new JPanel();
        SimpleToolWindowPanel uiContainer = new SimpleToolWindowPanel(false, true);

        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(
                "DebugVisualizer.VisualizerToolbar",
                (DefaultActionGroup) actionManager.getAction("DebugVisualizer.VisualizerToolbar"),
                false
        );
        actionToolbar.setTargetComponent(userInterface);
        uiContainer.setToolbar(actionToolbar.getComponent());
        uiContainer.setContent(userInterface);

        RunnerLayoutUi ui = debugSession.getUI();
        Content content = ui.createContent(
                CONTENT_ID,
                uiContainer,
                "Visual Debugger",
                IconLoader.getIcon("/icons/icon_16x16.png", DebuggVisualizerListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
        LOGGER.debug("UI initialized!");
    }
}
