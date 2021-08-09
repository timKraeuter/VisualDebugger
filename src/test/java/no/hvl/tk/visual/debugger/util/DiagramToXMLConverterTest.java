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
                "    <primitiveRootValues>\n" +
                "        <variableName>varName1</variableName>\n" +
                "        <type>varType1</type>\n" +
                "        <value>varValue1</value>\n" +
                "    </primitiveRootValues>\n" +
                "    <primitiveRootValues>\n" +
                "        <variableName>varName2</variableName>\n" +
                "        <type>varType2</type>\n" +
                "        <value>varValue2</value>\n" +
                "    </primitiveRootValues>\n" +
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
                "    <objects>\n" +
                "        <type>type</type>\n" +
                "        <variableName>varName</variableName>\n" +
                "        <attributeValues>\n" +
                "            <attributeName>attrName</attributeName>\n" +
                "            <attributeType>attrType</attributeType>\n" +
                "            <attributeValue>attrValue</attributeValue>\n" +
                "        </attributeValues>\n" +
                "        <id>1</id>\n" +
                "    </objects>\n" +
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
                "    <objects>\n" +
                "        <type>type1</type>\n" +
                "        <variableName>varName1</variableName>\n" +
                "        <id>1</id>\n" +
                "    </objects>\n" +
                "    <objects>\n" +
                "        <type>type2</type>\n" +
                "        <variableName>varName2</variableName>\n" +
                "        <id>2</id>\n" +
                "    </objects>\n" +
                "    <links>\n" +
                "        <id>00000000-0000-0001-0000-000000000001</id>\n" +
                "        <type>friend</type>\n" +
                "        <from>1</from>\n" +
                "        <to>2</to>\n" +
                "    </links>\n" +
                "    <links>\n" +
                "        <id>00000000-0000-0001-0000-000000000001</id>\n" +
                "        <type>enemy</type>\n" +
                "        <from>2</from>\n" +
                "        <to>1</to>\n" +
                "    </links>\n" +
                "</objectDiagram>\n"));
    }
}