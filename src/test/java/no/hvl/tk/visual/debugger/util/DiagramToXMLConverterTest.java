package no.hvl.tk.visual.debugger.util;

import no.hvl.tk.visual.debugger.domain.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class DiagramToXMLConverterTest {

    @Test
    void emptyDiagram() {
        final ObjectDiagram empty = new ObjectDiagram();

        final String xml = DiagramToXMLConverter.toXml(empty);

        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<objectDiagram/>\n"));
    }

    @Test
    void primitiveRootValues() {
        final ObjectDiagram diagram = new ObjectDiagram();
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName1", "varType1", "varValue1"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName2", "varType2", "varValue2"));

        final String xml = DiagramToXMLConverter.toXml(diagram);

        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<objectDiagram>\n" +
                "    <primitiveRootValue variableName=\"varName1\" type=\"varType1\" value=\"varValue1\"/>\n" +
                "    <primitiveRootValue variableName=\"varName2\" type=\"varType2\" value=\"varValue2\"/>\n" +
                "</objectDiagram>\n"));
    }

    @Test
    void objectsWithAttributes() {
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject obj1 = new ODObject(1, "type", "varName");
        obj1.addAttribute(new ODAttributeValue("attrName", "attrType", "attrValue"));
        diagram.addObject(obj1);

        final String xml = DiagramToXMLConverter.toXml(diagram);

        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<objectDiagram>\n" +
                "    <object type=\"type\" variableName=\"varName\" id=\"1\">\n" +
                "        <attributeValue attributeName=\"attrName\" attributeType=\"attrType\" attributeValue=\"attrValue\"/>\n" +
                "    </object>\n" +
                "</objectDiagram>\n"));
    }

    @Test
    void objectsAndLinks() {
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject obj1 = new ODObject(1, "type1", "varName1");
        final ODObject obj2 = new ODObject(2, "type2", "varName2");
        diagram.addObject(obj1);
        diagram.addObject(obj2);
        // random UUID's have to be mocked
        //noinspection ResultOfMethodCallIgnored
        mockStatic(UUID.class);
        // Set UUID's to 00000000-0000-0001-0000-000000000001
        when(UUID.randomUUID()).thenReturn(new UUID(1, 1));
        diagram.addLink(new ODLink(obj1, obj2, "friend"));
        diagram.addLink(new ODLink(obj2, obj1, "enemy"));

        final String xml = DiagramToXMLConverter.toXml(diagram);

        assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<objectDiagram>\n" +
                "    <object type=\"type1\" variableName=\"varName1\" id=\"1\"/>\n" +
                "    <object type=\"type2\" variableName=\"varName2\" id=\"2\"/>\n" +
                "    <link id=\"00000000-0000-0001-0000-000000000001\" type=\"friend\" from=\"1\" to=\"2\"/>\n" +
                "    <link id=\"00000000-0000-0001-0000-000000000001\" type=\"enemy\" from=\"2\" to=\"1\"/>\n" +
                "</objectDiagram>\n"));
    }
}