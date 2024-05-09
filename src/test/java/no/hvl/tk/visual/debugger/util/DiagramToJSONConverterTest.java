package no.hvl.tk.visual.debugger.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import no.hvl.tk.visual.debugger.domain.*;
import org.junit.jupiter.api.Test;

class DiagramToJSONConverterTest {

  @Test
  void emptyDiagram() {
    final ObjectDiagram empty = new ObjectDiagram();

    final String json = DiagramToJSONConverter.toJSON(empty);

    assertThat(json, is("{\"empty\":true}"));
  }

  @Test
  void primitiveRootValues() {
    final ObjectDiagram diagram = new ObjectDiagram();
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName1", "varType1", "varValue1"));
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("varName2", "varType2", "varValue2"));

    final String json = DiagramToJSONConverter.toJSON(diagram);
    assertThat(
        json,
        is(
            """
            {
              "primitiveRootValues" : [
                {
                  "variableName" : "varName1",
                  "type" : "varType1",
                  "value" : "varValue1"
                },
                {
                  "variableName" : "varName2",
                  "type" : "varType2",
                  "value" : "varValue2"
                }
              ],
              "empty" : false
            }
            """
                .replace("\n", "")
                .replace(" ", "")));
  }

  @Test
  void objectsWithAttributes() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject obj1 = new ODObject(1, "type", "varName");
    obj1.addAttribute(new ODAttributeValue("attrName", "attrType", "attrValue"));
    diagram.addObject(obj1);

    final String json = DiagramToJSONConverter.toJSON(diagram);

    assertThat(
        json,
        is(
            """
        {
          "objects" : [
            {
              "id" : "Object_1",
              "type" : "type",
              "variableName" : "varName",
              "attributeValues" : [
                {
                  "name" : "attrName",
                  "type" : "attrType",
                  "value" : "attrValue"
                }
              ],
              "links" : [ ]
            }
          ],
          "empty" : false
        }
        """
                .replace("\n", "")
                .replace(" ", "")));
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

    final String json = DiagramToJSONConverter.toJSON(diagram);

    assertThat(
        json,
        is(
            """
        {
          "objects" : [
            {
              "id" : "Object_1",
              "type" : "type1",
              "variableName" : "varName1",
              "attributeValues" : [ ],
              "links" : [ ]
            },
            {
              "id" : "Object_2",
              "type" : "type2",
              "variableName" : "varName2",
              "attributeValues" : [ ],
              "links" : [ ]
            }
          ],
          "links" : [
            {
              "id" : "Link_Object_1_to_Object_2_type_friend",
              "type" : "friend",
              "from" : "Object_1",
              "to" : "Object_2"
            },
            {
              "id" : "Link_Object_2_to_Object_1_type_enemy",
              "type" : "enemy",
              "from" : "Object_2",
              "to" : "Object_1"
            }
          ],
          "empty" : false
        }
        """
                .replace("\n", "")
                .replace(" ", "")));
  }
}
