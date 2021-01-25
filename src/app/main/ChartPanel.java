package app.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ChartPanel extends JPanel {
    private Integer[] values;

    private String[] names;

    public ChartPanel(Integer[] v, String[] n) {
        names = n;
        values = v;
    }

    public ChartPanel(Map m) {
        Set<String> keys = m.keySet();
        Collection<Integer> vals = m.values();
        names = keys.toArray(new String[keys.size()]);
        values = vals.toArray(new Integer[0]);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.length == 0)
            return;
        double maxValue = 0;
        for (int i = 0; i < values.length; i++) {
            if (maxValue < values[i])
                maxValue = values[i];
        }

        Dimension d = getSize();
        int clientWidth = d.width;
        int barWidth = 30;

        Font labelFont = new Font("SansSerif", Font.PLAIN, 10);
        FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

        int leftBarStart = 0;
        if (maxValue == 0)
            return;
        double scale = clientWidth / maxValue;
        int leftLabelMargin = 30;
        g.setFont(labelFont);

        int valueX = leftBarStart;
        for (int i = 0; i < values.length; i++) {
            int valueY = i * (barWidth + 1);
            int height = (int) (values[i] * scale);


            g.setColor(Color.red);
            g.fillRect(valueX, valueY, height, barWidth);
            g.setColor(Color.black);
            g.drawRect(valueX, valueY, height, barWidth);
            int labelWidth = labelFontMetrics.getHeight();
            int y = i * barWidth + (barWidth + labelWidth) / 2;
            g.drawString(names[i], leftLabelMargin, y);
        }
    }

    public static void main(String[] argv) {
        JFrame f = new JFrame();
        f.setSize(400, 300);
        Integer[] values = new Integer[3];
        String[] names = new String[3];
        values[0] = 1;
        names[0] = "Item 1";

        values[1] = 2;
        names[1] = "Item 2";

        values[2] = 4;
        names[2] = "Item 3";

        f.getContentPane().add(new ChartPanel(values, names));

        WindowListener wndCloser = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        };
        f.addWindowListener(wndCloser);
        f.setVisible(true);
    }
}