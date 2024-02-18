package no.hvl.tk.visual.debugger.debugging.stackframe.exceptions

import com.intellij.debugger.engine.evaluation.EvaluateException

class StackFrameAnalyzerException : RuntimeException {
  constructor(message: String) : super(message)

  constructor(e: EvaluateException) : super(e)
}
