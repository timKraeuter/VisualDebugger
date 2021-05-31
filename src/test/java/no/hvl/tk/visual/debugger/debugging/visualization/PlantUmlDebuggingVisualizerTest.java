package no.hvl.tk.visual.debugger.debugging.visualization;

import no.hvl.tk.visual.debugger.domain.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class PlantUmlDebuggingVisualizerTest {

    private final AtomicInteger counter = new AtomicInteger();

    @Test
    void toPlantUMLStringEmptyDiagramTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        final ObjectDiagram diagram = new ObjectDiagram();
        final String plantUMLString = PlantUmlDebuggingVisualizer.toPlantUMLString(diagram);
        assertThat(PlantUmlDebuggingVisualizerTest.normalizeString(plantUMLString), is("@startuml\n" +
                "!pragma layout smetana\n" +
                "@enduml\n"));
    }

    @Test
    void toPlantUMLStringPrimitiveVarsTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        final ObjectDiagram diagram = new ObjectDiagram();
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("int", "Integer", "1"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("double", "Double", "1.2"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("byte", "Byte", "1"));
        final String plantUMLString = PlantUmlDebuggingVisualizer.toPlantUMLString(diagram);

        assertThat(PlantUmlDebuggingVisualizerTest.normalizeString(plantUMLString), is("@startuml\n" +
                "!pragma layout smetana\n" +
                "object \"PrimitiveVariables\" as primitiveVariables {\n" +
                "byte=1\n" +
                "double=1.2\n" +
                "int=1\n" +
                "}\n" +
                "@enduml\n"));
    }

    @NotNull
    private static String normalizeString(final String plantUMLString) {
        return plantUMLString.replace("\r\n", "\n");
    }

    @Test
    void toPlantUMLStringLinksTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject person1 = new ODObject(1, "Person", "1");
        final ODObject person2 = new ODObject(2, "Person", "2");
        person1.addLink(new ODLink(person1, person2, "friends"));
        person2.addLink(new ODLink(person2, person1, "friends"));
        diagram.addObject(person1);
        diagram.addObject(person2);

        final String plantUMLString = PlantUmlDebuggingVisualizer.toPlantUMLString(diagram);

        assertThat(PlantUmlDebuggingVisualizerTest.normalizeString(plantUMLString), is("@startuml\n" +
                "!pragma layout smetana\n" +
                "object \"1:Person\" as 1\n" +
                "object \"2:Person\" as 2\n" +
                "1 --> 2 : friends\n" +
                "2 --> 1 : friends\n" +
                "@enduml\n"));
    }

    @Test
    void toPlantUMLStringPrimitiveMapsTest() {
        final PlantUmlDebuggingVisualizer visualizer = new PlantUmlDebuggingVisualizer(null);
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject hashMap = new ODObject(1, "java.util.HashMap", "hashMap");
        final ODObject hashMapNode1 = new ODObject(2, "java.util.HashMap$Node", "0");
        final ODObject hashMapNode2 = new ODObject(3, "java.util.HashMap$Node", "1");
        final ODObject hashMapNode3 = new ODObject(4, "java.util.HashMap$Node", "2");
        this.addKeyValueToNode(hashMapNode1);
        this.addKeyValueToNode(hashMapNode2);
        this.addKeyValueToNode(hashMapNode3);
        hashMap.addLink(new ODLink(hashMap, hashMapNode1, "0"));
        hashMap.addLink(new ODLink(hashMap, hashMapNode2, "1"));
        hashMap.addLink(new ODLink(hashMap, hashMapNode3, "2"));

        final ODObject otherMap = new ODObject(5, "java.util.SomeOtherMap", "otherMap");
        final ODObject otherMapNode1 = new ODObject(6, "java.util.SomeOtherMap$Node", "0");
        final ODObject otherMapNode2 = new ODObject(7, "java.util.SomeOtherMap$Node", "1");
        this.addKeyValueToNode(otherMapNode1);
        this.addKeyValueToNode(otherMapNode2);
        otherMap.addLink(new ODLink(otherMap, otherMapNode1, "0"));
        otherMap.addLink(new ODLink(otherMap, otherMapNode2, "1"));

        diagram.addObject(hashMap);
        diagram.addObject(hashMapNode1);
        diagram.addObject(hashMapNode2);
        diagram.addObject(hashMapNode3);
        diagram.addObject(otherMap);
        diagram.addObject(otherMapNode1);
        diagram.addObject(otherMapNode2);

        final String plantUMLString = PlantUmlDebuggingVisualizer.toPlantUMLString(diagram);
        System.out.println(plantUMLString);

        assertThat(PlantUmlDebuggingVisualizerTest.normalizeString(plantUMLString), is("@startuml\n" +
                "!pragma layout smetana\n" +
                "map \"hashMap:HashMap\" as 1 {\n" +
                "key1 => value2\n" +
                "key3 => value4\n" +
                "key5 => value6\n" +
                "}\n" +
                "map \"otherMap:SomeOtherMap\" as 5 {\n" +
                "key7 => value8\n" +
                "key9 => value10\n" +
                "}\n" +
                "@enduml\n"));
    }

    private void addKeyValueToNode(final ODObject hashMapNode1) {
        hashMapNode1.addAttribute(new ODAttributeValue("key", "someType", "key" + this.counter.incrementAndGet()));
        hashMapNode1.addAttribute(new ODAttributeValue("value", "someType", "value" + this.counter.incrementAndGet()));
    }
}