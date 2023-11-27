package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

/**
 * Only collects the diagram and ignores everything else.
 */
public class DebuggingInfoCollector extends DebuggingInfoVisualizerBase {
  @Override
  public void finishVisualization() {}

  @Override
  public void debuggingActivated() {}

  @Override
  public void debuggingDeactivated() {}

  public void setDiagram(ObjectDiagram diagram) {
    this.diagram = diagram;
  }
}
