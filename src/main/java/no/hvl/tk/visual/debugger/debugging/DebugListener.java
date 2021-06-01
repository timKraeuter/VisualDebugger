package no.hvl.tk.visual.debugger.debugging;

import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import no.hvl.tk.visual.debugger.DebugVisualizerListener;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.concurrency.CounterBasedLock;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";
    private static final String TOOLBAR_ACTION = "VisualDebugger.VisualizerToolbar"; // has to match with plugin.xml

    private final XDebugSession debugSession;
    private JPanel userInterface;
    private DebuggingInfoVisualizer debuggingVisualizer;

    public DebugListener(final XDebugSession debugSession) {
        Objects.requireNonNull(debugSession, "Debug session must not be null.");
        this.debugSession = debugSession;
    }

    @Override
    public void sessionPaused() {
        LOGGER.debug("Next step in debugger!");
        this.initUIIfNeeded();

        final XStackFrame currentStackFrame = this.debugSession.getCurrentStackFrame();
        Objects.requireNonNull(currentStackFrame, "Stack frame unexpectedly was null.");

        final var debuggingInfoCollector = this.getDebuggingInfoVisualizer();
        final var lock = new CounterBasedLock();
        final var nodeVisualizer = new NodeDebugVisualizer(
                debuggingInfoCollector,
                SharedState.getVisualizationDepth(),
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
        if (this.debuggingVisualizer == null) {
            this.debuggingVisualizer = new PlantUmlDebuggingVisualizer(this.userInterface);
        }
        return this.debuggingVisualizer;
    }

    @Override
    public void stackFrameChanged() {
        // nop
    }

    private void initUIIfNeeded() {
        if (this.userInterface != null) {
            return;
        }
        this.userInterface = new JPanel();
        final var uiContainer = new SimpleToolWindowPanel(false, true);

        final var actionManager = ActionManager.getInstance();
        final var actionToolbar = actionManager.createActionToolbar(
                TOOLBAR_ACTION,
                (DefaultActionGroup) actionManager.getAction(TOOLBAR_ACTION),
                false
        );
        actionToolbar.setTargetComponent(this.userInterface);
        uiContainer.setToolbar(actionToolbar.getComponent());
        uiContainer.setContent(this.userInterface);

        final RunnerLayoutUi ui = this.debugSession.getUI();
        final var content = ui.createContent(
                CONTENT_ID,
                uiContainer,
                "Visual Debugger",
                IconLoader.getIcon("/icons/icon_16x16.png", DebugVisualizerListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
        LOGGER.debug("UI initialized!");
    }
}
