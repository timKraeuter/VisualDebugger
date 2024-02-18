package no.hvl.tk.visual.debugger.util

import no.hvl.tk.visual.debugger.domain.*
import no.hvl.tk.visual.debugger.util.DiagramToXMLConverter.toXml
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

internal class DiagramToXMLConverterTest {
  @Test
  fun emptyDiagram() {
    val empty = ObjectDiagram()

    val xml = toXml(empty)

    MatcherAssert.assertThat(
        xml,
        `is`(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<db:objectDiagram xmlns:db=\"http://tk/schema/db\"/>\n"))
  }

  @Test
  fun primitiveRootValues() {
    val diagram = ObjectDiagram()
    diagram.addPrimitiveRootValue(ODPrimitiveRootValue("varName1", "varType1", "varValue1"))
    diagram.addPrimitiveRootValue(ODPrimitiveRootValue("varName2", "varType2", "varValue2"))

    val xml = toXml(diagram)

    MatcherAssert.assertThat(
        xml,
        `is`(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:primitiveRootValue variableName="varName1" type="varType1" value="varValue1"/>
                                       <db:primitiveRootValue variableName="varName2" type="varType2" value="varValue2"/>
                                   </db:objectDiagram>
                                   
                                   """
                .trimIndent()))
  }

  @Test
  fun objectsWithAttributes() {
    val diagram = ObjectDiagram()
    val obj1 = ODObject(1, "type", "varName")
    obj1.addAttribute(ODAttributeValue("attrName", "attrType", "attrValue"))
    diagram.addObject(obj1)

    val xml = toXml(diagram)

    MatcherAssert.assertThat(
        xml,
        `is`(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:object type="type" variableName="varName" id="Object_1">
                                           <db:attributeValue name="attrName" type="attrType" value="attrValue"/>
                                       </db:object>
                                   </db:objectDiagram>
                                   
                                   """
                .trimIndent()))
  }

  @Test
  fun objectsAndLinks() {
    val diagram = ObjectDiagram()
    val obj1 = ODObject(1, "type1", "varName1")
    val obj2 = ODObject(2, "type2", "varName2")
    diagram.addObject(obj1)
    diagram.addObject(obj2)
    diagram.addLink(ODLink(obj1, obj2, "friend"))
    diagram.addLink(ODLink(obj2, obj1, "enemy"))

    val xml = toXml(diagram)

    MatcherAssert.assertThat(
        xml,
        `is`(
            """
                                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                   <db:objectDiagram xmlns:db="http://tk/schema/db">
                                       <db:object type="type1" variableName="varName1" id="Object_1"/>
                                       <db:object type="type2" variableName="varName2" id="Object_2"/>
                                       <db:link type="friend" from="Object_1" to="Object_2" id="Link_Object_1_to_Object_2_type_friend"/>
                                       <db:link type="enemy" from="Object_2" to="Object_1" id="Link_Object_2_to_Object_1_type_enemy"/>
                                   </db:objectDiagram>
                                   
                                   """
                .trimIndent()))
  }
}
