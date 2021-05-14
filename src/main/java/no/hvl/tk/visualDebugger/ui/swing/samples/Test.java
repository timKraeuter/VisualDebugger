package no.hvl.tk.visualDebugger.ui.swing.samples;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test::createAndShowGui);
    }

    private static void createAndShowGui() {
        final JPanel panel = new JPanel();
        try {
            final BufferedImage image = ImageIO.read(new File("C:\\Temp/test.png"));
            final ImageIcon icon = new ImageIcon(image);
            final JLabel imageLabel = new JLabel(icon);
            imageLabel.setIcon(icon);
            imageLabel.setIcon(icon);
            imageLabel.setIcon(icon);
            panel.add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Test swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setSize(900, 900);
        frame.setVisible(true);
    }
}
