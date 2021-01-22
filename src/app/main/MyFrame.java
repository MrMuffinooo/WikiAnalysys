package app.main;

import DataImport.DataImporter;
import app.main.map.SVGMap;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public class MyFrame extends JFrame {

    //public Map ViewsByDomain = new DataImporter().importViewsByDomain(DataImporter.Domain.pl, "Donald_Trump", LocalDate.now().minusDays(1));

    public static String[] ColNames = {"No", "Article", "Views"};
    private String[][] data = new String[1000][4];
    JTable table = new JTable(data, ColNames);

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


        this.add(nav, BorderLayout.WEST);
        this.add(header, BorderLayout.NORTH);
        this.add(footer, BorderLayout.SOUTH);
        this.add(article, BorderLayout.CENTER);

        //----HEADER---------------------
        JLabel naglowek = new JLabel("WikiStat");

        //JButton home = new JButton("Home");
        //home.setBounds(50, 100, 95, 30);

        header.add(naglowek);
        //header.add(home);

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


        DateVetoPolicy veto = new DateVetoPolicy() {
            @Override
            public boolean isDateAllowed(LocalDate l) {
                return l.isBefore(LocalDate.now());
            }
        };

        DatePickerSettings dateSettings1 = new DatePickerSettings();
        DatePickerSettings dateSettings2 = new DatePickerSettings();

        DatePicker dateStart = new DatePicker(dateSettings1);               // wybór okresu
        dateStart.setBounds(0, 50, 200, 30);
        dateStart.setDate(LocalDate.now().minusDays(1));

        DatePicker dateEnd = new DatePicker(dateSettings2);               // wybór okresu
        dateEnd.setBounds(0, 90, 200, 30);
        dateEnd.setDate(LocalDate.now().minusDays(1));

        dateSettings1.setVetoPolicy(veto);
        dateSettings2.setVetoPolicy(veto);

        JTextField domainButt = new JTextField("pl");
        domainButt.setBounds(70, 170, 50, 30);

        JLabel d = new JLabel("Domena:");
        d.setBounds(10, 170, 50, 30);

        JButton searchButton = new JButton("Search");         // przycisk szukania
        searchButton.setBounds(50, 130, 95, 30);

        JButton showMapButt = new JButton("Map");         // przycisk szukania
        showMapButt.setBounds(130, 170, 60, 30);

        searchButton.addActionListener(e -> {
            String term = t1.getText();
            LocalDate date = dateStart.getDate();
            String domainName = domainButt.getText();

            DataImporter.Domain dd;
            try {
                dd = DataImporter.Domain.valueOf(domainName);
            } catch (Exception ex) {
                domainButt.setText("");
                domainButt.requestFocus();
                return;
            }

            if (term.equals("Search") || term.isEmpty()) {
                try {
                    setData(new DataImporter().importTop(dd, date));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    //setData(new DataImporter().importViews(dd, term, dateFormat.format(date)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            table.repaint();
        });

        showMapButt.addActionListener(e -> {
            JFrame mapa = new JFrame();
            mapa.add(new SVGMap().getSvgCanvas());
            mapa.setVisible(true);
            mapa.setSize(1010, 666);
        });

        nav.setLayout(null);
        nav.add(t1);
        nav.add(dateStart);
        nav.add(dateEnd);
        nav.add(domainButt);
        nav.add(d);
        nav.add(showMapButt);
        nav.add(searchButton);

        //----ARTICLE--------------------
        Date yesterday = new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L); //wczoraj
        try {
            setData(new DataImporter().importTop(DataImporter.Domain.pl, LocalDate.now().minusDays(1)));
        } catch (Exception e) {
            e.printStackTrace();
        }




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
            data[i][1] = entry.getKey().replaceAll("_", " ");
            data[i][2] = entry.getValue().toString();
            i++;
            if (i == 1000) {
                break;
            }
        }
    }
}
