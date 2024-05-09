package no.hvl.tk.visual.debugger.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class DiagramToJSONConverter {

  private static final Logger LOGGER = Logger.getInstance(DiagramToJSONConverter.class);

  private static ObjectMapper mapper;

  private DiagramToJSONConverter() {}

  public static String toJSON(final ObjectDiagram objectDiagram) {
    return toJSON(objectDiagram, false);
  }

  public static String toJSON(final ObjectDiagram objectDiagram, final boolean prettyPrint) {
    return ClassloaderUtil.runWithContextClassloader(
        () -> {
          createJacksonMapperIfNeeded();
          return marshallDiagram(objectDiagram, prettyPrint);
        });
  }

  private static String marshallDiagram(final ObjectDiagram objectDiagram, boolean prettyPrint) {
    try {
      if (prettyPrint) {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectDiagram);
      }
      return mapper.writeValueAsString(objectDiagram);
    } catch (JsonProcessingException e) {
      LOGGER.error(e);
    }
    return "";
  }

  private static void createJacksonMapperIfNeeded() {
    if (mapper == null) {
      mapper = new ObjectMapper();
    }
  }
}
