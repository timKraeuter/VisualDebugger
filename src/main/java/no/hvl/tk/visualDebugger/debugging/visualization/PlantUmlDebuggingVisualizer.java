package no.hvl.tk.visualDebugger.debugging.visualization;

import com.intellij.openapi.diagnostic.Logger;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import no.hvl.tk.visualDebugger.domain.ODAttributeValue;
import no.hvl.tk.visualDebugger.domain.ODObject;
import no.hvl.tk.visualDebugger.domain.ObjectDiagram;
import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class PlantUmlDebuggingVisualizer extends DebuggingInfoVisualizerBase {
    private static final Logger LOGGER = Logger.getInstance(PlantUmlDebuggingVisualizer.class);
    private final JPanel pluginUI;
    private final JLabel currentImage;

    public PlantUmlDebuggingVisualizer(JPanel jPanel) {
        this.pluginUI = jPanel;
        currentImage = new JLabel();
        pluginUI.add(currentImage);
    }

    @Override
    public void finishVisualization() {
        final String plantUMLString = toPlantUMLString();
        try {
            final byte[] pngData = toPNG(plantUMLString);
            addImageToUI(pngData);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        // Reset diagram
        this.diagram = new ObjectDiagram();
    }

    private void addImageToUI(byte[] pngData) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(pngData);
        final ImageIcon image = new ImageIcon(ImageIO.read(input));
        currentImage.setIcon(image);
    }

    private String toPlantUMLString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@startuml\n");
        // Use this so we are not dependent on a Graphviz/Dot installation on the host machine.
        stringBuilder.append("!pragma layout smetana\n");
        final Set<Pair<Integer, Integer>> links = new HashSet<>();

        // Add objects with attributes and collect links. They habe to be added after objects.
        for (final ODObject object : diagram.getObjects()) {
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
            object.getLinks().forEach(odLink -> links.add(Pair.of(odLink.getFrom().hashCode(), odLink.getTo().hashCode())));
        }
        // Add links
        links.forEach(pair -> stringBuilder.append(String.format("%s --> %s\n", pair.getLeft(), pair.getRight())));
        stringBuilder.append("@enduml\n");
        return stringBuilder.toString();
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
