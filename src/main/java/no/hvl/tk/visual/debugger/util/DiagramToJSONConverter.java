package no.hvl.tk.visual.debugger.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class DiagramToJSONConverter {

  private static final Logger LOGGER = Logger.getInstance(DiagramToJSONConverter.class);

  public static ObjectMapper mapper = createJacksonMapper();

  private DiagramToJSONConverter() {}

  public static String toJSON(final ObjectDiagram objectDiagram) {
    return ClassloaderUtil.runWithContextClassloader(() -> convert(objectDiagram));
  }

  private static String convert(final ObjectDiagram objectDiagram) {
    try {
      return mapper.writeValueAsString(objectDiagram);
    } catch (JsonProcessingException e) {
      LOGGER.error(e);
    }
    return "";
  }

  private static ObjectMapper createJacksonMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper;
  }
}
