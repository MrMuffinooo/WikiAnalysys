package app.main;

import DataImport.DataImporter;
import com.github.lgooddatepicker.components.*;

import javax.sql.rowset.serial.SerialArray;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MyFrame extends JFrame {

    public static final DateFormat dateFormat = new SimpleDateFormat("YYY/MM/DD");

    public static String[] ColNames = {"No", "Article", "Views"};
    private String[][] data = new String[1000][4];

    public MyFrame() {

        this.setSize(1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Wiki-stat");
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        JPanel nav = new JPanel();
        JPanel article = new JPanel(new GridLayout());
        JPanel footer = new JPanel();

        article.setBackground(Color.white);
        header.setBackground(Color.yellow);
        nav.setBackground(Color.orange);
        footer.setBackground(Color.gray);

        header.setPreferredSize(new Dimension(100, 100));
        nav.setPreferredSize(new Dimension(200, 100));
        article.setPreferredSize(new Dimension(100, 100));
        footer.setPreferredSize(new Dimension(100, 20));

        this.add(header, BorderLayout.NORTH);
        this.add(nav, BorderLayout.WEST);
        this.add(footer, BorderLayout.SOUTH);
        this.add(article, BorderLayout.CENTER);

        //----HEADER---------------------
        JLabel naglowek = new JLabel("HEADER");

        JButton home = new JButton("Home");
        home.setBounds(50, 100, 95, 30);

        header.add(naglowek);
        header.add(home);

        //----NAV------------------------


       /* String[] items = { "Dzień", "Miesiąc", "Rok"};
        JList<String> dokladnosc = new JList(items);
        dokladnosc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dokladnosc.setBounds(0,50,200,60);
        */
        JTextField t1 = new JTextField("Search");   //input text
        t1.setForeground(Color.GRAY);
        t1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (t1.getText().equals("Search")) {
                    t1.setText("");
                    t1.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (t1.getText().isEmpty()) {
                    t1.setForeground(Color.GRAY);
                    t1.setText("Search");
                }
            }
        });
        t1.setBounds(0, 0, 200, 30);

        DatePicker dateStart = new DatePicker();               // wybór okresu
        dateStart.setBounds(0, 50, 200, 30);

        DatePicker dateEnd = new DatePicker();
        dateEnd.setBounds(0, 100, 200, 30);

        JButton searchButton = new JButton("Search");         // przycisk szukania
        searchButton.setBounds(50, 150, 95, 30);

        nav.setLayout(null);
        nav.add(t1);
        nav.add(dateStart);
        nav.add(dateEnd);// oddzielny kalendarz do danych
        nav.add(searchButton);

        //----ARTICLE--------------------
        Date yesterday = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L); //wczoraj
        try {
            setData(new DataImporter().importTop(DataImporter.Domain.pl, dateFormat.format(yesterday)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(data, ColNames);


        table.getColumnModel().getColumn(0).setPreferredWidth(25);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setRowHeight(25);

        table.setFont(new Font("Calibri", Font.PLAIN, 20));
        String fonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (String font : fonts) {
            System.out.println(font);
        }


        JScrollPane scrollPane = new JScrollPane(table);
        article.add(scrollPane);


        //----FOOTER---------------------
        JLabel stopka = new JLabel("Adam Frej  Piotr Marciniak  Paweł Niewiadowski \u00a9");

        footer.add(stopka);
        this.setVisible(true);
    }


    public void setData(Map<String, Integer> d) {


        Integer i = 0;
        for (Map.Entry<String, Integer> entry : d.entrySet()) {
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = entry.getKey();
            data[i][2] = entry.getValue().toString();
            i++;
            if (i == 1000) {
                break;
            }
        }
    }
}
