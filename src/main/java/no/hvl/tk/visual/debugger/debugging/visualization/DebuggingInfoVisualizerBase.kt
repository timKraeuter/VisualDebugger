package no.hvl.tk.visual.debugger.debugging.visualization

import no.hvl.tk.visual.debugger.SharedState
import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameAnalyzer
import no.hvl.tk.visual.debugger.domain.ObjectDiagram

abstract class DebuggingInfoVisualizerBase protected constructor() : DebuggingInfoVisualizer {
  private var diagram: ObjectDiagram
  private var analyzer: StackFrameAnalyzer? = null

  init {
    this.diagram = ObjectDiagram()
  }

  override fun addMetadata(fileName: String, line: Int, stackFrameAnalyzer: StackFrameAnalyzer) {
    SharedState.debugLine = line
    SharedState.debugFileName = fileName
    this.analyzer = stackFrameAnalyzer
  }

  override fun reprintDiagram() {
    this.doVisualizationFurther(diagram)
  }

  protected abstract fun doVisualizationFurther(diagram: ObjectDiagram)

  override fun sessionStopped() {
    SharedState.manuallyExploredObjects.clear()
  }

  override fun getObjectWithChildren(objectId: String): ObjectDiagram {
    SharedState.manuallyExploredObjects.add(objectId)
    return analyzer!!.getChildren(objectId)
  }

  override fun doVisualization(diagram: ObjectDiagram) {
    this.diagram = diagram
    this.doVisualizationFurther(diagram)
  }
}
