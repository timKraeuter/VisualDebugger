package no.hvl.tk.visual.debugger.debugging.stackframe;

import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Key;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import java.awt.*;
import javax.swing.*;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.stackframe.exceptions.StackFrameAnalyzerException;
import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.PlantUmlDebuggingVisualizer;
import no.hvl.tk.visual.debugger.debugging.visualization.WebSocketDebuggingVisualizer;
import no.hvl.tk.visual.debugger.settings.PluginSettingsState;
import no.hvl.tk.visual.debugger.ui.VisualDebuggerIcons;
import org.jetbrains.annotations.NotNull;

public class StackFrameSessionListener implements XDebugSessionListener {

  private static final Logger LOGGER = Logger.getInstance(StackFrameSessionListener.class);

  // UI constants
  private static final String CONTENT_ID = "no.hvl.tk.VisualDebugger";
  private static final String TOOLBAR_ACTION =
      "VisualDebugger.VisualizerToolbar"; // has to match with plugin.xml

  private JPanel userInterface;

  private final XDebugSession debugSession;
  private DebuggingInfoVisualizer debuggingVisualizer;

  public StackFrameSessionListener(@NotNull XDebugProcess debugProcess) {
    this.debugSession = debugProcess.getSession();
    debugProcess
        .getProcessHandler()
        .addProcessListener(
            new ProcessListener() {
              @Override
              public void startNotified(@NotNull ProcessEvent event) {
                StackFrameSessionListener.this.initUIIfNeeded();
              }

              @Override
              public void processTerminated(@NotNull ProcessEvent event) {
                SharedState.setLastDiagramJSON("");
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
    StackFrameProxyImpl stackFrame = getStackFrameProxy();

    StackFrameAnalyzer stackFrameAnalyzer =
        new StackFrameAnalyzer(
            new StackFrameProxyImplAdapter(stackFrame),
            PluginSettingsState.getInstance().getVisualisationDepth(),
            SharedState.getManuallyExploredObjects(),
            PluginSettingsState.getInstance().isShowNullValues());

    if (debugSession.getCurrentPosition() != null) {
      String fileName = debugSession.getCurrentPosition().getFile().getNameWithoutExtension();
      int line = debugSession.getCurrentPosition().getLine() + 1;
      debuggingVisualizer.addMetadata(fileName, line, stackFrameAnalyzer);
    }

    this.debuggingVisualizer.doVisualization(stackFrameAnalyzer.analyze());
  }

  @NotNull private StackFrameProxyImpl getStackFrameProxy() {
    JavaStackFrame currentStackFrame = (JavaStackFrame) debugSession.getCurrentStackFrame();
    if (currentStackFrame == null) {
      throw new StackFrameAnalyzerException("Current stack frame could not be found!");
    }

    return currentStackFrame.getStackFrameProxy();
  }

  private void initUIIfNeeded() {
    if (this.userInterface != null) {
      return;
    }
    this.userInterface = new JPanel();
    userInterface.setLayout(new BorderLayout());
    this.getOrCreateDebuggingInfoVisualizer(); // make sure visualizer is initialized
    if (!SharedState.isDebuggingActive()) {
      this.resetUIAndAddActivateDebuggingButton();
    } else {
      this.debuggingVisualizer.debuggingActivated();
    }
    final var uiContainer = new SimpleToolWindowPanel(false, true);

    final var actionManager = ActionManager.getInstance();
    final var actionToolbar =
        actionManager.createActionToolbar(
            TOOLBAR_ACTION, (DefaultActionGroup) actionManager.getAction(TOOLBAR_ACTION), false);
    actionToolbar.setTargetComponent(this.userInterface);
    uiContainer.setToolbar(actionToolbar.getComponent());
    uiContainer.setContent(this.userInterface);

    final RunnerLayoutUi ui = this.debugSession.getUI();
    final var content =
        ui.createContent(
            CONTENT_ID, uiContainer, "Visual Debugger", VisualDebuggerIcons.VD_ICON, null);
    content.setCloseable(false);
    UIUtil.invokeLaterIfNeeded(() -> ui.addContent(content));
    LOGGER.debug("UI initialized!");
  }

  public void resetUIAndAddActivateDebuggingButton() {
    this.userInterface.removeAll();
    SharedState.setEmbeddedBrowserActive(false);
    userInterface.setLayout(new BorderLayout());

    final var activateButton = new JButton("Activate visual debugger");
    activateButton.addActionListener(
        actionEvent -> {
          SharedState.setDebuggingActive(true);
          this.userInterface.remove(activateButton);
          this.debuggingVisualizer.debuggingActivated();
          this.userInterface.revalidate();
        });
    this.userInterface.add(activateButton, BorderLayout.NORTH);

    this.userInterface.revalidate();
    this.userInterface.repaint();
  }

  @NotNull public DebuggingInfoVisualizer getOrCreateDebuggingInfoVisualizer() {
    if (this.debuggingVisualizer == null) {
      switch (PluginSettingsState.getInstance().getVisualizerOption()) {
        case WEB_UI ->
            this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
        case EMBEDDED ->
            this.debuggingVisualizer = new PlantUmlDebuggingVisualizer(this.userInterface);
        default -> {
          LOGGER.warn("Unrecognized debugging visualizer chosen. Defaulting to web visualizer!");
          this.debuggingVisualizer = new WebSocketDebuggingVisualizer(this.userInterface);
        }
      }
    }
    return this.debuggingVisualizer;
  }

  public void reprintDiagram() {
    this.debuggingVisualizer.reprintDiagram();
  }
}
