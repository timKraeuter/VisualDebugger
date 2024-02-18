package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameAnalyzer;
import no.hvl.tk.visual.debugger.domain.*;

public abstract class DebuggingInfoVisualizerBase implements DebuggingInfoVisualizer {

  private ObjectDiagram diagram;
  private StackFrameAnalyzer analyzer;

  protected DebuggingInfoVisualizerBase() {
    this.diagram = new ObjectDiagram();
  }

  @Override
  public void addMetadata(String fileName, Integer line, StackFrameAnalyzer stackFrameAnalyzer) {
    SharedState.debugLine = line;
    SharedState.debugFileName = fileName;
    this.analyzer = stackFrameAnalyzer;
  }

  @Override
  public void reprintDiagram() {
    this.doVisualizationFurther(diagram);
  }

  protected abstract void doVisualizationFurther(ObjectDiagram diagram);

  @Override
  public void sessionStopped() {
    SharedState.manuallyExploredObjects.clear();
  }

  @Override
  public ObjectDiagram getObjectWithChildren(String objectID) {
    SharedState.manuallyExploredObjects.add(objectID);
    return analyzer.getChildren(objectID);
  }

  @Override
  public void doVisualization(ObjectDiagram diagram) {
    this.diagram = diagram;
    this.doVisualizationFurther(diagram);
  }
}
