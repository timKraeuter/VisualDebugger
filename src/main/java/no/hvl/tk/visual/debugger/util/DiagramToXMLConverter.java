package no.hvl.tk.visual.debugger.util;

import com.intellij.openapi.diagnostic.Logger;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

import java.io.StringWriter;

public class DiagramToXMLConverter {
    private static final Logger LOGGER = Logger.getInstance(DiagramToXMLConverter.class);

    private static JAXBContext jaxbContext;
    private static Marshaller jaxbMarshaller;

    private DiagramToXMLConverter() {
    }

    public static String toXml(final ObjectDiagram diagram) {
        return ClassloaderUtil.runWithContextClassloader(() -> {
            createJAXBObjectsIfNeeded();
            return marshallDiagram(diagram);
        });

    }

    private static String marshallDiagram(final ObjectDiagram mockDiagram) {
        final StringWriter sw = new StringWriter();
        try {
            jaxbMarshaller.marshal(mockDiagram, sw);
        } catch (final JAXBException e) {
            LOGGER.error(e);
        }
        return sw.toString();
    }

    private static void createJAXBObjectsIfNeeded() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(ObjectDiagram.class);
            } catch (final JAXBException e) {
                LOGGER.error(e);
            }
        }
        if (jaxbMarshaller == null) {
            try {
                jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            } catch (final JAXBException e) {
                LOGGER.error(e);
            }
        }
    }
}
