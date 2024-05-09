package no.hvl.tk.visual.debugger.server.endpoint.message;

import static no.hvl.tk.visual.debugger.util.DiagramToJSONConverter.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.openapi.diagnostic.Logger;

public class DebuggingWSMessage {

  private static final Logger LOGGER = Logger.getInstance(DebuggingWSMessage.class);

  private final DebuggingMessageType type;
  private final String content;

  private final String fileName;
  private final Integer line;

  public DebuggingWSMessage(final DebuggingMessageType type, final String content) {
    this(type, content, null, null);
  }

  public DebuggingWSMessage(
      DebuggingMessageType type, String content, String fileName, Integer line) {
    this.type = type;
    this.content = content;
    this.fileName = fileName;
    this.line = line;
  }

  // Getter needed for serialize
  public String getType() {
    return this.type.getTypeString();
  }

  public String getContent() {
    return this.content;
  }

  public String getFileName() {
    return fileName;
  }

  public Integer getLine() {
    return line;
  }

  public String serialize() {
    try {
      return getMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      LOGGER.error(e);
      return "JsonProcessingException";
    }
  }
}
