package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.jdi.StackFrameProxyImpl;
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameAnalyzer;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public interface DebuggingInfoVisualizer {

  void doVisualization(ObjectDiagram diagram);

  void reprintDiagram();

  void addMetadata(String fileName, Integer line, StackFrameAnalyzer stackFrame);

  ObjectDiagram getObjectWithChildren(String objectId);

  void debuggingActivated();

  void debuggingDeactivated();

  void sessionStopped();
}
