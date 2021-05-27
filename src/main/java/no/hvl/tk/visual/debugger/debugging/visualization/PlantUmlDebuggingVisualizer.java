package no.hvl.tk.visual.debugger.debugging.visualization;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBScrollPane;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
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

    public PlantUmlDebuggingVisualizer(JPanel jPanel) {
        this.pluginUI = jPanel;
    }

    @Override
    public void finishVisualization() {
        final String plantUMLString = toPlantUMLString(this.diagram);
        // Reset diagram
        this.diagram = new ObjectDiagram();
        try {
            final byte[] pngData = toPNG(plantUMLString);
            addImageToUI(pngData);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void addImageToUI(byte[] pngData) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(pngData);
        final ImageIcon imageIcon = new ImageIcon(ImageIO.read(input));

        if (imgLabel == null) {
            imgLabel = new JLabel(imageIcon);
            final JBScrollPane scrollPane = new JBScrollPane(imgLabel);
            pluginUI.setLayout(new BorderLayout());
            pluginUI.add(scrollPane, BorderLayout.CENTER);
        } else {
            imgLabel.setIcon(imageIcon);
        }
    }

    String toPlantUMLString(ObjectDiagram objectDiagram) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@startuml\n");
        // Use this so we are not dependent on a Graphviz/Dot installation on the host machine.
        stringBuilder.append("!pragma layout smetana\n");

        final Set<ODLink> links = new HashSet<>();
        // Sort ojects so the visualisation does not change when there are no objects changes.
        final List<ODObject> sortedObjects = objectDiagram.getObjects()
                                                          .stream()
                                                          .sorted()
                                                          .collect(Collectors.toList());

        // Add objects with attributes and collect links. They have to be added after objects.
        addObjectsToDiagramAndCollectLinks(stringBuilder, links, sortedObjects);

        // Add links.
        addLinksToDiagram(stringBuilder, links);

        // Add primitive root values if there are any.
        if (!objectDiagram.getPrimitiveRootValues().isEmpty()) {
            addPrimitiveRootValuesToDiagram(objectDiagram, stringBuilder);
        }

        stringBuilder.append("@enduml\n");
        return stringBuilder.toString();
    }

    private void addPrimitiveRootValuesToDiagram(ObjectDiagram objectDiagram, StringBuilder stringBuilder) {
        stringBuilder.append(String.format("object \"%s\" as %s", "LocalPrimitiveVars", "primitiveRootValues"));
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

    private void addLinksToDiagram(StringBuilder stringBuilder, Set<ODLink> links) {
        links.stream()
             .sorted()
             .forEach(link -> stringBuilder.append(
                     String.format("%s --> %s : %s%n",
                             link.getFrom().hashCode(),
                             link.getTo().hashCode(),
                             link.getType())));
    }

    private void addObjectsToDiagramAndCollectLinks(StringBuilder stringBuilder, Set<ODLink> links, List<ODObject> sortedObjects) {
        final HashSet<ODObject> ignoredObjects = new HashSet<>();
        for (final ODObject object : sortedObjects) {
            if (ignoredObjects.contains(object)) {
                continue;
            }
            // Primitive maps are visualised differently
            if (isPrimitiveJavaMap(object)) {
                doPrimitiveMapVisualisation(stringBuilder, ignoredObjects, object);
                continue;
            }

            // Add the object
            stringBuilder.append(String.format("object \"%s:%s\" as %s",
                    object.getVariableName(),
                    this.shortenTypeName(object.getType()),
                    object.hashCode()));

            // Add object attributes
            if (!object.getAttributeValues().isEmpty()) {
                stringBuilder.append(" {\n");
                object.getAttributeValues().stream()
                      // Sort so that objects with the same type have the same order of attributes
                      .sorted(Comparator.comparing(ODAttributeValue::getAttributeName))
                      .forEach(odAttributeValue -> stringBuilder.append(
                              String.format(
                                      "%s=%s%n",
                                      odAttributeValue.getAttributeName(),
                                      odAttributeValue.getAttributeValue())));
                stringBuilder.append("}\n");
            } else {
                stringBuilder.append("\n");
            }
            links.addAll(object.getLinks());
        }
    }

    private void doPrimitiveMapVisualisation(StringBuilder stringBuilder, HashSet<ODObject> ignoredObjects, ODObject object) {
        stringBuilder.append(String.format("map \"%s:%s\" as %s",
                object.getVariableName(),
                this.shortenTypeName(object.getType()),
                object.hashCode()));
        stringBuilder.append(" {\n");

        object.getLinks().forEach(odLink -> {
            final ODObject mapNode = odLink.getTo();
            ignoredObjects.add(mapNode); // Dont visualize the node as an object anymore!

            final Optional<ODAttributeValue> key = mapNode.getAttributeByName("key");
            final Optional<ODAttributeValue> value = mapNode.getAttributeByName("value");
            if (key.isPresent() && value.isPresent()) {
                stringBuilder.append(
                        String.format("%s => %s%n",
                                key.get().getAttributeValue(),
                                value.get().getAttributeValue()));
            }
        });

        stringBuilder.append("}\n");
    }

    private boolean isPrimitiveJavaMap(ODObject object) {
        return isMap(object) && isPrimitive(object);
    }

    private boolean isPrimitive(ODObject object) {
        // Nodes attached to the link must not have have any more links.
        // Key and value are then attributes i.e. primitive.
        return !object.getLinks().isEmpty()
                && object.getLinks()
                         .stream()
                         .anyMatch(odLink -> odLink.getTo().getLinks().isEmpty());
    }

    private boolean isMap(ODObject object) {
        return object.getType().startsWith("java.util") && object.getType().endsWith("Map");
    }

    private Object shortenTypeName(String type) {
        return type.substring(type.lastIndexOf(".") + 1);
    }

    private byte[] toPNG(String plantUMLDescription) throws IOException {
        SourceStringReader reader = new SourceStringReader(plantUMLDescription);
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            reader.outputImage(outputStream, new FileFormatOption(FileFormat.PNG));
            return outputStream.toByteArray();
        }
    }
}
