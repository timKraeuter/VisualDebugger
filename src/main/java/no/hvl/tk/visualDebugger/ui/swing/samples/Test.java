package no.hvl.tk.visualDebugger.ui.swing.samples;

import javax.swing.*;
import java.awt.*;

public class Test extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test::createAndShowGui);
    }

    private static void createAndShowGui() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
        panel.setSize(300, 300);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(new JLabel("1"));
        panel.add(new JSeparator());
        panel.add(new JLabel("2"));
        panel.add(new JLabel("3"));

        JFrame frame = new JFrame("Test swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
