package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.ODLink;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PlantUmlDebuggingVisualizerTest {

    @Test
    void toPlantUMLStringEmptyDiagramTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        ObjectDiagram diagram = new ObjectDiagram();
        final String plantUMLString = visualizer.toPlantUMLString(diagram);
        assertThat(plantUMLString, is("@startuml\n" +
                "!pragma layout smetana\n" +
                "@enduml\n"));
    }

    @Test
    void toPlantUMLStringPrimitiveVarsTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        ObjectDiagram diagram = new ObjectDiagram();
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("int", "Integer", "1"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("double", "Double", "1.2"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("byte", "Byte", "1"));
        final String plantUMLString = visualizer.toPlantUMLString(diagram);

        assertThat(plantUMLString, is("@startuml\n" +
                "!pragma layout smetana\n" +
                "object \"LocalPrimitiveVars\" as primitiveRootValues {\n" +
                "byte=1\r\n" +
                "double=1.2\r\n" +
                "int=1\r\n" +
                "}\n" +
                "@enduml\n"));
    }

    @Test
    void toPlantUMLStringLinksTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        ObjectDiagram diagram = new ObjectDiagram();
        final ODObject person1 = new ODObject(1, "Person", "1");
        final ODObject person2 = new ODObject(2, "Person", "2");
        person1.addLink(new ODLink(person1, person2, "friends"));
        person2.addLink(new ODLink(person2, person1, "friends"));
        diagram.addObject(person1);
        diagram.addObject(person2);

        final String plantUMLString = visualizer.toPlantUMLString(diagram);

        assertThat(plantUMLString, is("@startuml\n" +
                "!pragma layout smetana\n" +
                "object \"1:Person\" as 1 {\n" +
                "}\n" +
                "object \"2:Person\" as 2 {\n" +
                "}\n" +
                "1 --> 2 : friends\r\n" +
                "2 --> 1 : friends\r\n" +
                "@enduml\n"));
    }


}