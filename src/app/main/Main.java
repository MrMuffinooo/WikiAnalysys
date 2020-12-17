package app.main;


import app.main.map.SVGMap;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * A simple sample application that shows
 * a OSM map of World
 * @author Martin Steiger
 */
public class Main
{
    /**
     * @param args the program args (ignored)
     */
    public static void main(String[] args) throws IOException {
        // Create a new JFrame.
        JFrame frame = new JFrame("Batik");

        frame.setSize(800, 800);
        // Add components to the frame.
        frame.getContentPane().add(new SVGMap().getSvgCanvas());


        // Display the frame.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}
