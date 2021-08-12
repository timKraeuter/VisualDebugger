package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.components.JBScrollPane;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import no.hvl.tk.visual.debugger.SharedState;
import no.hvl.tk.visual.debugger.domain.ODAttributeValue;
import no.hvl.tk.visual.debugger.domain.ODLink;
import no.hvl.tk.visual.debugger.domain.ODObject;
import no.hvl.tk.visual.debugger.domain.ObjectDiagram;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PlantUmlDebuggingVisualizer extends DebuggingInfoVisualizerBase {
    private static final Logger LOGGER = Logger.getInstance(PlantUmlDebuggingVisualizer.class);
    private final JPanel pluginUI;
    private JLabel imgLabel;

    public PlantUmlDebuggingVisualizer(final JPanel jPanel) {
        this.pluginUI = jPanel;
    }

    @Override
    public void finishVisualization() {
        final var plantUMLString = PlantUmlDebuggingVisualizer.toPlantUMLString(this.diagram);
        SharedState.setLastPlantUMLDiagram(plantUMLString);
        // Reset diagram
        this.diagram = new ObjectDiagram();
        try {
            final byte[] pngData = PlantUmlDebuggingVisualizer.toImage(plantUMLString, FileFormat.PNG);
            this.addImageToUI(pngData);
        } catch (final IOException e) {
            LOGGER.error(e);
        }
    }

    private void addImageToUI(final byte[] pngData) throws IOException {
        final var input = new ByteArrayInputStream(pngData);
        final var imageIcon = new ImageIcon(ImageIO.read(input));

        if (this.imgLabel == null || this.pluginUI.getComponents().length == 0) {
            this.createImageAndAddToUI(imageIcon);
        } else {
            this.imgLabel.setIcon(imageIcon);
        }
        this.pluginUI.revalidate();
    }

    private void createImageAndAddToUI(final ImageIcon imageIcon) {
        this.imgLabel = new JLabel(imageIcon);
        final var scrollPane = new JBScrollPane(this.imgLabel);
        this.pluginUI.setLayout(new BorderLayout());
        this.pluginUI.add(scrollPane, BorderLayout.CENTER);
    }

    static String toPlantUMLString(final ObjectDiagram objectDiagram) {
        final var stringBuilder = new StringBuilder();
        stringBuilder.append("@startuml\n");
        // Use this so we are not dependent on a Graphviz/Dot installation on the host machine.
        stringBuilder.append("!pragma layout smetana\n");

        final Set<ODLink> links = new HashSet<>();
        // Sort objects so the visualisation does not change when there are no objects changes.
        final List<ODObject> sortedObjects = objectDiagram.getObjects()
                                                          .stream()
                                                          .sorted()
                                                          .collect(Collectors.toList());

        // Add objects with attributes and collect links. They have to be added after objects.
        PlantUmlDebuggingVisualizer.addObjectsToDiagramAndCollectLinks(stringBuilder, links, sortedObjects);

        // Add links.
        PlantUmlDebuggingVisualizer.addLinksToDiagram(stringBuilder, links);

        // Add primitive root values if there are any.
        if (!objectDiagram.getPrimitiveRootValues().isEmpty()) {
            PlantUmlDebuggingVisualizer.addPrimitiveRootValuesToDiagram(objectDiagram, stringBuilder);
        }

        stringBuilder.append("@enduml\n");
        return stringBuilder.toString();
    }

    private static void addPrimitiveRootValuesToDiagram(final ObjectDiagram objectDiagram, final StringBuilder stringBuilder) {
        stringBuilder.append(String.format("object \"%s\" as %s", "PrimitiveVariables", "primitiveVariables"));
        stringBuilder.append(" {\n");
        objectDiagram.getPrimitiveRootValues()
                     .stream()
                     .sorted()
                     .forEach(primitiveRootValue -> stringBuilder.append(
                             String.format("%s=%s%n",
                                     primitiveRootValue.getVariableName(),
                                     primitiveRootValue.getValue())));
        stringBuilder.append("}\n");
    }

    private static void addLinksToDiagram(final StringBuilder stringBuilder, final Set<ODLink> links) {
        links.stream()
             .sorted()
             .forEach(link -> stringBuilder.append(
                     String.format("%s --> %s : %s%n",
                             link.getFrom().hashCode(),
                             link.getTo().hashCode(),
                             link.getType())));
    }

    private static void addObjectsToDiagramAndCollectLinks(final StringBuilder stringBuilder, final Set<ODLink> links, final List<ODObject> sortedObjects) {
        final HashSet<ODObject> ignoredObjects = new HashSet<>();
        for (final ODObject object : sortedObjects) {
            if (ignoredObjects.contains(object)) {
                continue;
            }
            // Primitive maps are visualised differently
            if (PlantUmlDebuggingVisualizer.isPrimitiveJavaMap(object)) {
                PlantUmlDebuggingVisualizer.doPrimitiveMapVisualisation(stringBuilder, ignoredObjects, object);
                continue;
            }

            // Add the object
            stringBuilder.append(String.format("object \"%s:%s\" as %s",
                    object.getVariableName(),
                    PlantUmlDebuggingVisualizer.shortenTypeName(object.getType()),
                    object.hashCode()));

            // Add object attributes
            if (!object.getAttributeValues().isEmpty()) {
                stringBuilder.append(" {\n");
                object.getAttributeValues().stream()
                      // Sort so that objects with the same type have the same order of attributes
                      .sorted(Comparator.comparing(ODAttributeValue::getName))
                      .forEach(odAttributeValue -> stringBuilder.append(
                              String.format(
                                      "%s=%s%n",
                                      odAttributeValue.getName(),
                                      odAttributeValue.getValue())));
                stringBuilder.append("}\n");
            } else {
                stringBuilder.append("\n");
            }
            links.addAll(object.getLinks());
        }
    }

    private static void doPrimitiveMapVisualisation(final StringBuilder stringBuilder, final HashSet<ODObject> ignoredObjects, final ODObject object) {
        stringBuilder.append(String.format("map \"%s:%s\" as %s",
                object.getVariableName(),
                PlantUmlDebuggingVisualizer.shortenTypeName(object.getType()),
                object.hashCode()));
        stringBuilder.append(" {\n");

        object.getLinks()
              .stream()
              .sorted()
              .forEach(odLink -> {
                  final ODObject mapNode = odLink.getTo();
                  ignoredObjects.add(mapNode); // Dont visualize the node as an object anymore!

                  final Optional<ODAttributeValue> key = mapNode.getAttributeByName("key");
                  final Optional<ODAttributeValue> value = mapNode.getAttributeByName("value");
                  if (key.isPresent() && value.isPresent()) {
                      stringBuilder.append(
                              String.format("%s => %s%n",
                                      key.get().getValue(),
                                      value.get().getValue()));
                  }
              });

        stringBuilder.append("}\n");
    }

    private static boolean isPrimitiveJavaMap(final ODObject object) {
        return PlantUmlDebuggingVisualizer.isMap(object) && PlantUmlDebuggingVisualizer.isPrimitive(object);
    }

    private static boolean isPrimitive(final ODObject object) {
        // Nodes attached to the link must not have have any more links.
        // Key and value are then attributes i.e. primitive.
        return !object.getLinks().isEmpty()
                && object.getLinks()
                         .stream()
                         .anyMatch(odLink -> odLink.getTo().getLinks().isEmpty());
    }

    private static boolean isMap(final ODObject object) {
        return object.getType().startsWith("java.util") && object.getType().endsWith("Map");
    }

    private static Object shortenTypeName(final String type) {
        return type.substring(type.lastIndexOf(".") + 1);
    }

    public static byte[] toImage(final String plantUMLDescription, final FileFormat format) throws IOException {
        final var reader = new SourceStringReader(plantUMLDescription);
        try (final var outputStream = new ByteArrayOutputStream()) {
            reader.outputImage(outputStream, new FileFormatOption(format));
            return outputStream.toByteArray();
        }
    }

    @Override
    protected void preAddObject() {
        // NOOP
    }

    @Override
    public DebuggingInfoVisualizer addDebugNodeForObject(final ODObject object, final JavaValue jValue) {
        // NOOP
        return this;
    }

    @Override
    public Pair<ODObject, JavaValue> getDebugNodeAndObjectForObjectId(final String objectId) {
        throw new UnsupportedOperationException();
    }
}
