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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        final String plantUMLString = toPlantUMLString();
        System.out.println(plantUMLString);
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

    private String toPlantUMLString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@startuml\n");
        // Use this so we are not dependent on a Graphviz/Dot installation on the host machine.
        stringBuilder.append("!pragma layout smetana\n");

        final Set<ODLink> links = new HashSet<>();
        // Sort ojects so the visualisation does not change when there are no objects changes.
        final List<ODObject> sortedObjects = diagram.getObjects()
                                                    .stream()
                                                    .sorted()
                                                    .collect(Collectors.toList());

        // Add objects with attributes and collect links. They have to be added after objects.
        addObjectsToDiagramAndCollectLinks(stringBuilder, links, sortedObjects);

        // Add links.
        addLinksToDiagram(stringBuilder, links);

        // Add primitive root values if there are any.
        if (!this.diagram.getPrimitiveRootValues().isEmpty()) {
            addPrimitiveRootValuesToDiagram(stringBuilder);
        }

        stringBuilder.append("@enduml\n");
        return stringBuilder.toString();
    }

    private void addPrimitiveRootValuesToDiagram(StringBuilder stringBuilder) {
        stringBuilder.append(String.format("object \"%s\" as %s", "LocalPrimitiveVars", "primitiveRootValues"));
        stringBuilder.append(" {\n");
        this.diagram.getPrimitiveRootValues()
                    .stream()
                    .sorted()
                    .forEach(primitiveRootValue -> stringBuilder.append(
                            String.format("%s=%s\n",
                                    primitiveRootValue.getVariableName(),
                                    primitiveRootValue.getValue())));
        stringBuilder.append("}\n");
    }

    private void addLinksToDiagram(StringBuilder stringBuilder, Set<ODLink> links) {
        links.forEach(link -> stringBuilder.append(
                String.format("%s --> %s : %s\n",
                        link.getFrom().hashCode(),
                        link.getTo().hashCode(),
                        link.getType())));
    }

    private void addObjectsToDiagramAndCollectLinks(StringBuilder stringBuilder, Set<ODLink> links, List<ODObject> sortedObjects) {
        for (final ODObject object : sortedObjects) {
            stringBuilder.append(String.format("object \"%s:%s\" as %s",
                    object.getVariableName(),
                    this.shortenTypeName(object.getType()),
                    object.hashCode()));
            if (object.getAttributeValues().size() > 0) {
                stringBuilder.append(" {\n");
                object.getAttributeValues().stream()
                      // Sort so that objects with the same type have the same order of attributes
                      .sorted(Comparator.comparing(ODAttributeValue::getAttributeName))
                      .forEach(odAttributeValue -> stringBuilder.append(
                              String.format(
                                      "%s=%s\n",
                                      odAttributeValue.getAttributeName(),
                                      odAttributeValue.getAttributeValue())));
                stringBuilder.append("}\n");
            } else {
                stringBuilder.append("\n");
            }
            links.addAll(object.getLinks());
        }
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
