package no.hvl.tk.visual.debugger.debugging;

import no.hvl.tk.visual.debugger.debugging.visualization.DebuggingInfoVisualizerBase;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

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
