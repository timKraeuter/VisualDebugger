package no.hvl.tk.visual.debugger.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import no.hvl.tk.visual.debugger.domain.*;
import org.junit.jupiter.api.Test;

class DiagramToXMLConverterTest {

  @Test
  void emptyDiagram() {
    final ObjectDiagram empty = new ObjectDiagram();

    final String xml = DiagramToXMLConverter.toXml(empty);

    assertThat(
        xml,
        is(
            """
                              <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                              <db:objectDiagram xmlns:db="http://tk/schema/db"/>
                                   """));
  }

  @Test
  void primitiveRootValues() {
    final ObjectDiagram diagram = new ObjectDiagram();
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName1", "varType1", "varValue1"));
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName2", "varType2", "varValue2"));

    final String xml = DiagramToXMLConverter.toXml(diagram);

    assertThat(
        xml,
        is(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:primitiveRootValue variableName="varName1" type="varType1" value="varValue1"/>
                                       <db:primitiveRootValue variableName="varName2" type="varType2" value="varValue2"/>
                                   </db:objectDiagram>
                                   """));
  }

  @Test
  void objectsWithAttributes() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject obj1 = new ODObject(1, "type", "varName");
    obj1.addAttribute(new ODAttributeValue("attrName", "attrType", "attrValue"));
    diagram.addObject(obj1);

    final String xml = DiagramToXMLConverter.toXml(diagram);

    assertThat(
        xml,
        is(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:object type="type" variableName="varName" id="Object_1">
                                           <db:attributeValue name="attrName" type="attrType" value="attrValue"/>
                                       </db:object>
                                   </db:objectDiagram>
                                   """));
  }

  @Test
  void objectsAndLinks() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject obj1 = new ODObject(1, "type1", "varName1");
    final ODObject obj2 = new ODObject(2, "type2", "varName2");
    diagram.addObject(obj1);
    diagram.addObject(obj2);
    diagram.addLink(new ODLink(obj1, obj2, "friend"));
    diagram.addLink(new ODLink(obj2, obj1, "enemy"));

    final String xml = DiagramToXMLConverter.toXml(diagram);

    assertThat(
        xml,
        is(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:object type="type1" variableName="varName1" id="Object_1"/>
                                       <db:object type="type2" variableName="varName2" id="Object_2"/>
                                       <db:link type="friend" from="Object_1" to="Object_2" id="Link_Object_1_to_Object_2_type_friend"/>
                                       <db:link type="enemy" from="Object_2" to="Object_1" id="Link_Object_2_to_Object_1_type_enemy"/>
                                   </db:objectDiagram>
                                   """));
  }
}
