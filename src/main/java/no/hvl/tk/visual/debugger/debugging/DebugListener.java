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
import no.hvl.tk.visual.debugger.debugging.visualization.WebSocketDebuggingVisualizer;
import no.hvl.tk.visual.debugger.settings.AppSettingsState;
import no.hvl.tk.visual.debugger.util.ClassloaderUtil;
import no.hvl.tk.visual.debugger.webAPI.DebugAPIServerStarter;
import no.hvl.tk.visual.debugger.webAPI.ServerConstants;
import no.hvl.tk.visual.debugger.webAPI.UIServerStarter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class DebugListener implements XDebugSessionListener {
    private static final Logger LOGGER = Logger.getInstance(DebugListener.class);
    private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";
    private static final String TOOLBAR_ACTION = "VisualDebugger.VisualizerToolbar"; // has to match with plugin.xml

    private final XDebugSession debugSession;
    private JPanel userInterface;
    private DebuggingInfoVisualizer debuggingVisualizer;
    private XStackFrame currentStackFrame;

    public DebugListener(final XDebugSession debugSession) {
        Objects.requireNonNull(debugSession, "Debug session must not be null.");
        this.debugSession = debugSession;
        SharedState.setDebugListener(this);
    }

    @Override
    public void sessionPaused() {
        LOGGER.debug("Next step in debugger!");
        this.initUIIfNeeded();

        this.currentStackFrame = this.debugSession.getCurrentStackFrame();
        Objects.requireNonNull(this.currentStackFrame, "Stack frame unexpectedly was null.");
        this.startVisualDebugging();
    }

    public void startVisualDebugging() {
        if (!SharedState.isDebuggingActive()) {
            return;
        }
        final var debuggingInfoCollector = this.getDebuggingInfoVisualizer();
        final var lock = new CounterBasedLock();
        final var nodeVisualizer = new NodeDebugVisualizer(
                debuggingInfoCollector,
                AppSettingsState.getInstance().visualisationDepth,
                lock);
        // Happens in a different thread!
        this.currentStackFrame.computeChildren(nodeVisualizer);
        new Thread(() -> {
            // Wait for the computation to be over
            lock.lock();
            debuggingInfoCollector.finishVisualization();
        }).start();
    }

    @NotNull
    public DebuggingInfoVisualizer getDebuggingInfoVisualizer() {
        if (this.debuggingVisualizer == null) {
            this.debuggingVisualizer = new WebSocketDebuggingVisualizer();
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
        if (!SharedState.isDebuggingActive()) {
            this.addActivateDebuggingButton();
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
                IconLoader.getIcon("/icons/icon_16x16.png", DebugVisualizerListener.class),
                null);
        content.setCloseable(false);
        UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
        LOGGER.debug("UI initialized!");
    }

    public void addActivateDebuggingButton() {
        this.userInterface.removeAll();
        this.userInterface.setLayout(new FlowLayout());

        final var activateButton = new JButton("Activate visual debugger");
        activateButton.addActionListener(actionEvent -> {
            DebugListener.startDebuggingWebsocketServer();
            DebugListener.startUIServer();
            final var uiButton = new JButton(
                    String.format("Launch user interface (%s)", ServerConstants.UI_SERVER_URL));
            uiButton.addActionListener(e -> DebugListener.launchUIInBrowser());

            SharedState.setDebuggingActive(true);
            this.userInterface.remove(activateButton);
            this.userInterface.add(uiButton);
            this.userInterface.revalidate();
            this.startVisualDebugging();
        });
        this.userInterface.add(activateButton);

        this.userInterface.revalidate();
        this.userInterface.repaint();
    }

    private static void launchUIInBrowser() {
        if (Desktop.isDesktopSupported()) {
            // Windows
            try {
                Desktop.getDesktop().browse(new URI(ServerConstants.UI_SERVER_URL));
            } catch (final IOException | URISyntaxException ex) {
                LOGGER.error(ex);
            }
        } else {
            // Ubuntu
            final Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("/usr/bin/firefox -new-window " + ServerConstants.UI_SERVER_URL);
            } catch (final IOException ex) {
                LOGGER.error(ex);
            }
        }
    }

    private static void startDebuggingWebsocketServer() {
        ClassloaderUtil.runWithContextClassloader(() -> {
            final Server server = DebugAPIServerStarter.runNewServer();
            SharedState.setDebugAPIServer(server);
            return null; // needed because of generic method.
        });
    }

    private static void startUIServer() {
        ClassloaderUtil.runWithContextClassloader(() -> {
            final HttpServer server = UIServerStarter.runNewServer();
            SharedState.setUIServer(server);
            return null; // needed because of generic method.
        });
    }
}
