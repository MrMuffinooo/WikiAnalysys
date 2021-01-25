package app.main;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ChartPanel extends JPanel {
    private Integer[] values;

    private String[] names;

    private int barWidth = 30;

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

    public void setMap(Map m) {
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


        Font labelFont = new Font("Calibri", Font.PLAIN, 20);
        FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

        int leftBarStart = 0;
        if (maxValue == 0)
            return;
        double scale = clientWidth / maxValue;
        int leftLabelMargin = 10;
        g.setFont(labelFont);

        int valueX = leftBarStart;
        for (int i = 0; i < values.length; i++) {
            int valueY = i * (barWidth);
            int height = (int) (values[i] * scale);

            g.setColor(Color.black);
            g.drawLine(0, i * barWidth, clientWidth, i * barWidth);
            g.setColor(new Color(0, 0, 255, 100));
            g.fillRect(valueX, valueY, height, barWidth);
            g.setColor(new Color(0, 0, 200, 200));
            g.drawRect(valueX, valueY, height, barWidth);
            g.setColor(Color.black);

            int labelHeight = labelFontMetrics.getHeight();
            int y = i * barWidth + (barWidth + 10) / 2;
            g.drawString(names[i].replaceAll("_", " "), leftLabelMargin, y);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), values.length * barWidth);
    }

}