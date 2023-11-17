package no.hvl.tk.visual.debugger.debugging.stackframe.exceptions;

public class StackFrameAnalyzerException extends RuntimeException {
  public StackFrameAnalyzerException(String message) {
    super(message);
  }

  public StackFrameAnalyzerException(String message, Exception e) {
    super(message, e);
  }
}
