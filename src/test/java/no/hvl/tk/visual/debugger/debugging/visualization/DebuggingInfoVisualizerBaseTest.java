package no.hvl.tk.visual.debugger.debugging.visualization;

import com.google.common.collect.Sets;
import no.hvl.tk.visual.debugger.debugging.DebuggingInfoCollector;
import no.hvl.tk.visual.debugger.domain.ODLink;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class DebuggingInfoVisualizerBaseTest {

    @Test
    void getDiagramWithDepthEmptyDiagramTest() {
        DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
        ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);
        assertThat(diagramWithDepth.isEmpty(), is(true));
    }

    @Test
    void getDiagramWithDepthPrimitiveVarsTest() {
        final ObjectDiagram diagram = new ObjectDiagram();
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("int", "Integer", "1"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("double", "Double", "1.2"));
        diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("byte", "Byte", "1"));

        DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
        debuggingInfoCollector.setDiagram(diagram);

        ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);

        assertThat(diagramWithDepth.getPrimitiveRootValues(), is(diagram.getPrimitiveRootValues()));
    }

    @Test
    void getDiagramWithDepthCycleTest() {
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject person1 = new ODObject(1, "Person", "1");
        final ODObject person2 = new ODObject(2, "Person", "2");
        ODLink personLink1 = new ODLink(person1, person2, "friends");
        ODLink personLink2 = new ODLink(person2, person1, "friends");
        person1.addLink(personLink1);
        person2.addLink(personLink2);
        diagram.addObject(person1);
        diagram.addObject(person2);
        diagram.addLink(personLink1);
        diagram.addLink(personLink2);

        DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
        debuggingInfoCollector.setDiagram(diagram);
        debuggingInfoCollector.addObject(person1, true);

        ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);

        assertThat(diagramWithDepth.getObjects(), is(diagram.getObjects()));
        assertThat(diagramWithDepth.links, is(diagram.links));
    }

    @Test
    void getDiagramWithDepth_DepthTest() {
        final ObjectDiagram diagram = new ObjectDiagram();
        final ODObject person1 = new ODObject(0, "Person", "1");
        ODObject lastPerson = person1;
        diagram.addObject(person1);
        for (int i = 1; i < 101; i++) {
            final ODObject nextPerson = new ODObject(i, "Person", String.valueOf(i));
            ODLink personLink = new ODLink(lastPerson, nextPerson, "friends");
            lastPerson.addLink(personLink);
            diagram.addObject(nextPerson);
            diagram.addLink(personLink);
            lastPerson = nextPerson;
        }

        DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
        debuggingInfoCollector.setDiagram(diagram);
        debuggingInfoCollector.addObject(person1, true);

        ObjectDiagram diagramWithDepth0 = debuggingInfoCollector.getDiagramWithDepth(0);

        assertThat(diagramWithDepth0.getObjects(), is(Sets.newHashSet(person1)));
        assertThat(diagramWithDepth0.links.isEmpty(), is(true));

        ObjectDiagram diagramWithDepth10 = debuggingInfoCollector.getDiagramWithDepth(10);

        assertThat(diagramWithDepth10.getObjects().size(), is(11));
        assertThat(diagramWithDepth10.links.size(), is(10));

        ObjectDiagram diagramWithDepth25 = debuggingInfoCollector.getDiagramWithDepth(25);

        assertThat(diagramWithDepth25.getObjects().size(), is(26));
        assertThat(diagramWithDepth25.links.size(), is(25));

        ObjectDiagram diagramWithDepth100 = debuggingInfoCollector.getDiagramWithDepth(100);

        assertThat(diagramWithDepth100.getObjects(), is(diagram.getObjects()));
        assertThat(diagramWithDepth100.links, is(diagram.links));

        ObjectDiagram diagramWithDepth1000 = debuggingInfoCollector.getDiagramWithDepth(1000);

        assertThat(diagramWithDepth1000.getObjects(), is(diagram.getObjects()));
        assertThat(diagramWithDepth1000.links, is(diagram.links));
    }
}