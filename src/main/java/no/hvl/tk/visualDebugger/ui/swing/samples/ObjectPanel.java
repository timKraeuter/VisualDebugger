package no.hvl.tk.visualDebugger.ui.swing.samples;

import no.hvl.tk.visualDebugger.domain.ODAttributeValue;
import no.hvl.tk.visualDebugger.domain.ODObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an Object shown in a JPanel.
 */
public class ObjectPanel extends JPanel {

    private final ODObject object;
    private final int padding_between_attributes = 4;

    public ObjectPanel(ODObject object) {
        this.object = object;
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        // One label for the name and type: "abc:Observer"
        final JLabel nameAndTypeLabel = new JLabel(
                String.format("%s:%s", this.object.getVariableName(), this.object.getType()));
        this.add(nameAndTypeLabel, BorderLayout.PAGE_START);
        // Separator between name/type and attributes
        final JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        this.add(separator);

        // One label for each Attribute: "key=value"
        final List<JLabel> attributeLabels = this.object.getAttributeValues().stream()
                .map(odAttributeValue -> new JLabel(this.createAttributeText(odAttributeValue), JLabel.CENTER))
                .collect(Collectors.toList());

        final List<JLabel> allLabels = new ArrayList<>(attributeLabels);
        allLabels.add(nameAndTypeLabel);
        // Find the widest JLabel
        @SuppressWarnings("OptionalGetWithoutIsPresent") final JLabel widestJLabel = allLabels.stream()
                .max(Comparator.comparingDouble(jLabel -> jLabel.getPreferredSize().getWidth()))
                .get(); // Must be present since the collection is never empty.
        final int objectPanelWidth = widestJLabel.getWidth();
        nameAndTypeLabel.setBounds(0, 0, objectPanelWidth, 20);
        // Add labels for each attribute and move them to the right position
        int y = nameAndTypeLabel.getHeight();
        for (JLabel attributeLabel : attributeLabels) {
            this.add(attributeLabel, BorderLayout.CENTER);
            attributeLabel.setBounds(0, y, objectPanelWidth, 20);
            y += attributeLabel.getHeight();
        }
        this.setPreferredSize(new Dimension(objectPanelWidth, y));
        this.setVisible(true);
    }

    private String createAttributeText(ODAttributeValue odAttributeValue) {
        return String.format("%s=%s", odAttributeValue.getAttributeName(), odAttributeValue.getAttributeValue());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ObjectPanel::createAndShowGui);
    }

    private static void createAndShowGui() {
        final ODObject object = new ODObject("Type", "varName");
        for (int i = 0; i < 5; i++) {
            object.addAttribute(new ODAttributeValue("name" + i, "type" + i, "value" + i));
        }
        final ObjectPanel mainPanel = new ObjectPanel(object);

        JFrame frame = new JFrame("Object in OD test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(900, 900);
        frame.setVisible(true);
    }
}
