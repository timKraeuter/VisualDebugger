package no.hvl.tk.visual.debugger.debugging.stackframe.exceptions;

import com.intellij.debugger.engine.evaluation.EvaluateException;

public class StackFrameAnalyzerException extends RuntimeException {
  public StackFrameAnalyzerException(String message) {
    super(message);
  }

  public StackFrameAnalyzerException(EvaluateException e) {
    super(e);
  }
}
