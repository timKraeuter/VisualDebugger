package no.hvl.tk.visualDebugger.ui.plantUML;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws IOException {
        String source = "@startuml\n" +
                "testdot\n" +
                "@enduml";

        SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
// Write the first image to "os"
        String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

// The XML is stored into svg
        final String svg = os.toString(StandardCharsets.UTF_8);
        System.out.println(svg);
    }
}
