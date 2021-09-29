package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.SuspendContext;
import com.intellij.debugger.engine.jdi.ThreadReferenceProxy;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Key;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import no.hvl.tk.visual.debugger.DebugProcessListener;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.WebSocketDebuggingVisualizer;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class StackFrameSessionListener implements XDebugSessionListener {

    private static final Logger LOGGER = Logger.getInstance(StackFrameSessionListener.class);

    // UI constants
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";
    private static final String TOOLBAR_ACTION = "VisualDebugger.VisualizerToolbar"; // has to match with plugin.xml

    private static final int SUFFIX_LENGTH = ".java".length();


    private JPanel userInterface;

    private final XDebugSession debugSession;
    private DebuggingInfoVisualizer debuggingVisualizer;
    private ThreadReference thread;

    public StackFrameSessionListener(@NotNull XDebugProcess debugProcess) {
        this.debugSession = debugProcess.getSession();
        debugProcess.getProcessHandler().addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                StackFrameSessionListener.this.initUIIfNeeded();
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                // not relevant
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                // not relevant
            }
        });
        SharedState.setDebugListener(this);
    }

    @Override
    public void sessionStopped() {
        this.debuggingVisualizer.sessionStopped();
    }

    @Override
    public void sessionPaused() {
        this.startVisualDebugging();
    }

    private void startVisualDebugging() {
        if (!SharedState.isDebuggingActive()) {
            return;
        }
        StackFrame stackFrame = this.getCorrectStackFrame(this.debugSession);

        StackFrameAnalyzer stackFrameAnalyzer = new StackFrameAnalyzer(
                stackFrame,
                this.thread,
                this.debuggingVisualizer);
        stackFrameAnalyzer.analyze();

        this.debuggingVisualizer.finishVisualization();
    }

    private void initUIIfNeeded() {
        if (this.userInterface != null) {
            return;
        }
        this.userInterface = new JPanel();
        this.getOrCreateDebuggingInfoVisualizer(); // make sure visualizer is initialized
        if (!SharedState.isDebuggingActive()) {
            this.resetUIAndAddActivateDebuggingButton();
        } else {
            this.debuggingVisualizer.debuggingActivated();
        }
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
                IconLoader.getIcon("/icons/icon_16x16.png", DebugProcessListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
        LOGGER.debug("UI initialized!");
    }

    public void resetUIAndAddActivateDebuggingButton() {
        this.userInterface.removeAll();
        this.userInterface.setLayout(new FlowLayout());

        final var activateButton = new JButton("Activate visual debugger");
        activateButton.addActionListener(actionEvent -> {

            SharedState.setDebuggingActive(true);
            this.userInterface.remove(activateButton);
            this.debuggingVisualizer.debuggingActivated();
            this.userInterface.revalidate();
        });
        this.userInterface.add(activateButton);

        this.userInterface.revalidate();
        this.userInterface.repaint();
    }

    @NotNull
    public DebuggingInfoVisualizer getOrCreateDebuggingInfoVisualizer() {
        if (this.debuggingVisualizer == null) {
            switch (PluginSettingsState.getInstance().getVisualizerOption()) {
                case WEB_UI:
                    this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
                    break;
                case EMBEDDED:
                    this.debuggingVisualizer = new PlantUmlDebuggingVisualizer(this.userInterface);
                    break;
                default:
                    LOGGER.warn("Unrecognized debugging visualizer chosen. Defaulting to web visualizer!");
                    this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
            }
        }
        return this.debuggingVisualizer;
    }

    private StackFrame getCorrectStackFrame(XDebugSession debugSession) {
        SuspendContext sc = (SuspendContext) debugSession.getSuspendContext();
        ThreadReferenceProxy scThread = sc.getThread();
        if (scThread == null) {
            throw new RuntimeException("Suspend context thread was unexpectedly nulL!");
        }

        this.thread = scThread.getThreadReference();
        try {
            final Optional<StackFrame> first = this.thread.frames().stream()
                                                          .filter(this::isCorrectStackFrame)
                                                          .findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        } catch (IncompatibleThreadStateException e) {
            LOGGER.error(e);
            throw new RuntimeException("Correct stack frame for debugging not found!", e);
        }
        throw new RuntimeException("Correct stack frame for debugging not found!");
    }

    private boolean isCorrectStackFrame(StackFrame stackFrame) {
        final XStackFrame currentStackFrame = this.debugSession.getCurrentStackFrame();
        if (currentStackFrame == null || currentStackFrame.getSourcePosition() == null) {
            throw new RuntimeException("Current stack frame or source position was unexpectedly nulL!");

        }
        final String canonicalName = currentStackFrame.getSourcePosition().getFile().getName();
        // cut the .java
        final String wantedTypeName = canonicalName.substring(0, canonicalName.length() - SUFFIX_LENGTH);

        final String typeName = stackFrame.location().declaringType().name();

        return typeName.contains(wantedTypeName);
    }

    public void reprintDiagram() {
        this.debuggingVisualizer.reprintPreviousDiagram();
    }
}
