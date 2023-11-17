package no.hvl.tk.visual.debugger.util;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

public class DiagramToXMLConverter {
  private static final Logger LOGGER = Logger.getInstance(DiagramToXMLConverter.class);

  private static JAXBContext jaxbContext;
  private static Marshaller jaxbMarshaller;

  private DiagramToXMLConverter() {}

  public static String toXml(final ObjectDiagram objectDiagram) {
    return ClassloaderUtil.runWithContextClassloader(
        () -> {
          createJAXBObjectsIfNeeded();
          return marshallDiagram(objectDiagram);
        });
  }

  private static String marshallDiagram(final ObjectDiagram objectDiagram) {
    final StringWriter sw = new StringWriter();
    try {
      jaxbMarshaller.marshal(objectDiagram, sw);
    } catch (final JAXBException e) {
      LOGGER.error(e);
    }
    return sw.toString();
  }

  private static void createJAXBObjectsIfNeeded() {
    if (jaxbContext == null) {
      try {
        jaxbContext = JAXBContext.newInstance(ObjectDiagram.class);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      } catch (final JAXBException e) {
        LOGGER.error(e);
      }
    }
  }
}
