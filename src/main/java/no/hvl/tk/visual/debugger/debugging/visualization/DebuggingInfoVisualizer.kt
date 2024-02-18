package no.hvl.tk.visual.debugger.debugging.visualization

import no.hvl.tk.visual.debugger.debugging.stackframe.StackFrameAnalyzer
import no.hvl.tk.visual.debugger.domain.ObjectDiagram

interface DebuggingInfoVisualizer {
  fun doVisualization(diagram: ObjectDiagram)

  fun reprintDiagram()

  fun addMetadata(fileName: String, line: Int, stackFrameAnalyzer: StackFrameAnalyzer)

  fun getObjectWithChildren(objectId: String): ObjectDiagram

  fun debuggingActivated()

  fun debuggingDeactivated()

  fun sessionStopped()
}
