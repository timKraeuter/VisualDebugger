package no.hvl.tk.visualDebugger.debugging.visualization;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import no.hvl.tk.visualDebugger.domain.ODAttributeValue;
import no.hvl.tk.visualDebugger.domain.ODObject;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class PlantUmlVisualizer extends InformationCollectorVisualizer {
    @Override
    public void finishVisualization() {
        final String plantUMLString = toPlantUMLString();

        System.out.println(plantUMLString);
        try {
            System.out.println(toSVG(plantUMLString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toPlantUMLString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@startuml\n");
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

    private String toSVG(String plantUMLDescription) throws IOException {
        SourceStringReader reader = new SourceStringReader(plantUMLDescription);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        return os.toString(StandardCharsets.UTF_8);
    }
}
