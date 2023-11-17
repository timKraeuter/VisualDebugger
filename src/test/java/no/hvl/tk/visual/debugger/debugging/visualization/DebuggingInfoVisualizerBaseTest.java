package no.hvl.tk.visual.debugger.debugging.visualization;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Sets;
import no.hvl.tk.visual.debugger.debugging.DebuggingInfoCollector;
import no.hvl.tk.visual.debugger.domain.ODLink;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ODPrimitiveRootValue;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class DebuggingInfoVisualizerBaseTest {

  @Test
  void getDiagramWithDepthEmptyDiagramTest() {
    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
    final ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);
    assertThat(diagramWithDepth.isEmpty(), is(true));
  }

  @Test
  void getDiagramWithDepthPrimitiveVarsTest() {
    final ObjectDiagram diagram = new ObjectDiagram();
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("int", "Integer", "1"));
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("double", "Double", "1.2"));
    diagram.addPrimitiveRootValue(new ODPrimitiveRootValue("byte", "Byte", "1"));

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
    debuggingInfoCollector.setDiagram(diagram);

    final ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);

    assertThat(diagramWithDepth.getPrimitiveRootValues(), is(diagram.getPrimitiveRootValues()));
  }

  @Test
  void getDiagramWithDepthCycleTest() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject person1 = this.populateDiagramWithPersons(diagram);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
    debuggingInfoCollector.setDiagram(diagram);
    debuggingInfoCollector.addObject(person1, true);

    final ObjectDiagram diagramWithDepth = debuggingInfoCollector.getDiagramWithDepth(42);

    assertThat(diagramWithDepth.getObjects(), is(diagram.getObjects()));
    assertThat(diagramWithDepth.getLinks(), is(diagram.getLinks()));
  }

  @Test
  void getObjectWithChildrenFromPreviousDiagramTest() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject person1 = this.populateDiagramWithPersons(diagram);

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
    debuggingInfoCollector.setDiagram(diagram);
    debuggingInfoCollector.addObject(person1, true);

    debuggingInfoCollector.resetDiagram();
    final ObjectDiagram person1Diagram =
        debuggingInfoCollector.getObjectWithChildrenFromPreviousDiagram("Object_1");

    assertThat(person1Diagram.getObjects(), is(diagram.getObjects()));
    // Only one link from person 1 found but all objects
    assertThat(person1Diagram.getLinks().size(), is(1));
  }

  @NotNull private ODObject populateDiagramWithPersons(final ObjectDiagram diagram) {
    final ODObject person1 = new ODObject(1, "Person", "1");
    final ODObject person2 = new ODObject(2, "Person", "2");
    final ODLink personLink1 = new ODLink(person1, person2, "friends");
    final ODLink personLink2 = new ODLink(person2, person1, "friends");
    person1.addLink(personLink1);
    person2.addLink(personLink2);
    diagram.addObject(person1);
    diagram.addObject(person2);
    diagram.addLink(personLink1);
    diagram.addLink(personLink2);
    return person1;
  }

  @Test
  void getDiagramWithDepthTest() {
    final ObjectDiagram diagram = new ObjectDiagram();
    final ODObject person1 = new ODObject(0, "Person", "1");
    ODObject lastPerson = person1;
    diagram.addObject(person1);
    for (int i = 1; i < 101; i++) {
      final ODObject nextPerson = new ODObject(i, "Person", String.valueOf(i));
      final ODLink personLink = new ODLink(lastPerson, nextPerson, "friends");
      lastPerson.addLink(personLink);
      diagram.addObject(nextPerson);
      diagram.addLink(personLink);
      lastPerson = nextPerson;
    }

    final DebuggingInfoCollector debuggingInfoCollector = new DebuggingInfoCollector();
    debuggingInfoCollector.setDiagram(diagram);
    debuggingInfoCollector.addObject(person1, true);

    final ObjectDiagram diagramWithDepth0 = debuggingInfoCollector.getDiagramWithDepth(0);

    assertThat(diagramWithDepth0.getObjects(), is(Sets.newHashSet(person1)));
    assertThat(diagramWithDepth0.getLinks().isEmpty(), is(true));

    final ObjectDiagram diagramWithDepth10 = debuggingInfoCollector.getDiagramWithDepth(10);

    assertThat(diagramWithDepth10.getObjects().size(), is(11));
    assertThat(diagramWithDepth10.getLinks().size(), is(10));

    final ObjectDiagram diagramWithDepth25 = debuggingInfoCollector.getDiagramWithDepth(25);

    assertThat(diagramWithDepth25.getObjects().size(), is(26));
    assertThat(diagramWithDepth25.getLinks().size(), is(25));

    final ObjectDiagram diagramWithDepth100 = debuggingInfoCollector.getDiagramWithDepth(100);

    assertThat(diagramWithDepth100.getObjects(), is(diagram.getObjects()));
    assertThat(diagramWithDepth100.getLinks(), is(diagram.getLinks()));

    final ObjectDiagram diagramWithDepth1000 = debuggingInfoCollector.getDiagramWithDepth(1000);

    assertThat(diagramWithDepth1000.getObjects(), is(diagram.getObjects()));
    assertThat(diagramWithDepth1000.getLinks(), is(diagram.getLinks()));
  }
}
