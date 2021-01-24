package app.main;

import app.main.DataImport.DataImporter;
import app.main.GlobalMap.SVGMap;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateVetoPolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyFrame extends JFrame {

    //public Map ViewsByDomain = new DataImporter().importViewsByDomain(DataImporter.Domain.pl, "Donald_Trump", LocalDate.now().minusDays(1));

    Map DataSet;

    MapTableModel TableModel;

    {
        try {
            DataSet = new DataImporter().importTop(DataImporter.Domain.pl, LocalDate.now().minusDays(1));
            TableModel = new MapTableModel(DataSet, "No", "Article", "Views");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JTable table = new JTable(TableModel) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


    public MyFrame() {

        this.setSize(1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Wiki-stat");
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        JPanel nav = new JPanel();
        JPanel article = new JPanel(new GridLayout());
        JPanel footer = new JPanel();

        //BoxLayout b = new BoxLayout(header, BoxLayout.Y_AXIS);
        //header.setLayout(b);

        article.setBackground(Color.white);
        header.setBackground(Color.yellow);
        nav.setBackground(Color.orange);
        footer.setBackground(Color.gray);

        header.setPreferredSize(new Dimension(100, 80));
        nav.setPreferredSize(new Dimension(200, 100));
        article.setPreferredSize(new Dimension(100, 100));
        footer.setPreferredSize(new Dimension(100, 20));


        this.add(nav, BorderLayout.WEST);
        this.add(header, BorderLayout.NORTH);
        this.add(footer, BorderLayout.SOUTH);
        this.add(article, BorderLayout.CENTER);


        //----HEADER---------------------
        JLabel naglowek = new JLabel("Most popular articles in ");
        naglowek.setFont(new Font("SansSerif", Font.BOLD, 26));
        JLabel naglowek2 = new JLabel(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " pl");
        naglowek2.setFont(new Font("SansSerif", Font.BOLD, 26));

        header.add(naglowek);
        header.add(naglowek2);

        //----NAV------------------------

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
        t1.setEnabled(false);

        ///////////////////////////////////////////
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
        dateEnd.setEnabled(false);
        ///////////////////////////////////////////

        JTextField domainButt = new JTextField("pl");
        domainButt.setBounds(70, 170, 50, 30);

        JLabel d = new JLabel("Domain:");
        d.setBounds(10, 170, 50, 30);

        JButton searchButton = new JButton("Search");         // przycisk szukania
        searchButton.setBounds(50, 130, 95, 30);

        JButton showMapButt = new JButton("Map");         // przycisk szukania
        showMapButt.setBounds(130, 170, 60, 30);
        showMapButt.setEnabled(false);


        showMapButt.addActionListener(e -> { // nowe okno z mapa
            JFrame mapa = new JFrame();
            mapa.add(new SVGMap().getSvgCanvas());
            mapa.setVisible(true);
            mapa.setSize(1010, 666);
        });


        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Popular articles in domain");
        listModel.addElement("Article popularity in domains");
        listModel.addElement("Article popularity over time");

        JList<String> viewNav = new JList<>(listModel);
        viewNav.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        viewNav.setBounds(0, 220, 200, 55);
        viewNav.setSelectedIndex(0);

        viewNav.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String s = viewNav.getSelectedValue();
                if (s.equals("Popular articles in domain")) {
                    t1.setEnabled(false);
                    dateStart.setEnabled(true);
                    dateEnd.setEnabled(false);
                    showMapButt.setEnabled(false);
                } else if (s.equals("Article popularity in domains")) {
                    t1.setEnabled(true);
                    dateStart.setEnabled(true);
                    dateEnd.setEnabled(true);
                    showMapButt.setEnabled(true);
                } else {
                    t1.setEnabled(true);
                    dateStart.setEnabled(true);
                    dateEnd.setEnabled(true);
                    showMapButt.setEnabled(false);
                }
            }
        });

        nav.setLayout(null);
        nav.add(t1);
        nav.add(dateStart);
        nav.add(dateEnd);
        nav.add(domainButt);
        nav.add(d);
        nav.add(showMapButt);
        nav.add(searchButton);
        nav.add(viewNav);

        //----ARTICLE--------------------

        table.getColumnModel().getColumn(0).setPreferredWidth(25);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setRowHeight(25);

        table.setFont(new Font("Calibri", Font.PLAIN, 20));

        JScrollPane scrollPane = new JScrollPane(table);
        article.add(scrollPane);


        //----FOOTER---------------------
        JLabel stopka = new JLabel("Adam Frej  Piotr Marciniak  Paweł Niewiadowski \u00a9");

        footer.add(stopka);
        this.setVisible(true);

        /////////////////////////////////////////////////
        searchButton.addActionListener(e -> {
            String term = t1.getText().replaceAll(" ", "_");
            LocalDate dateS = dateStart.getDate();
            LocalDate dateE = dateEnd.getDate();
            String domainName = domainButt.getText();
            int s = viewNav.getSelectedIndex();

            DataImporter.Domain dd;
            try {
                dd = DataImporter.Domain.valueOf(domainName);
            } catch (Exception ex) {
                domainButt.setText("");
                domainButt.requestFocus();
                return;
            }

            if (dateE.isBefore(dateS)) {
                dateStart.setText("");
                dateStart.requestFocus();
                return;
            }

            if (s == 0) {
                try {
                    DataSet = new DataImporter().importTop(dd, dateS);
                    TableModel.setMap(DataSet);
                    TableModel.setColumnNames("No", "Article", "Views");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                naglowek.setText("Most popular articles in ");
                naglowek2.setText(dateS.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " " + dd.toString());

            } else if (s == 1) {
                if (term.isEmpty() || term.equals("Search")) {
                    t1.requestFocus();
                    return;
                }
                try {
                    DataSet.clear();
                    Map<String, Integer> tempMap = new DataImporter().importViewsByDomain(dd, term, dateS, dateE);
                    tempMap.values().removeAll(Collections.singleton(null));
                    tempMap.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEachOrdered(x -> DataSet.put(x.getKey(), x.getValue()));

                    TableModel.setMap(DataSet); //TODO cos nie dziala z pl znakami
                    TableModel.setColumnNames("No", "Domain", "Views");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (dateS.isEqual(dateE)) {
                    naglowek.setText("Most popular domains for " + term.replaceAll("_", " ") + " in");
                    naglowek2.setText(dateS.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                } else {
                    naglowek.setText("Most popular domains for " + term.replaceAll("_", " ") + " between ");
                    naglowek2.setText(dateS.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " and " + dateE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                }
            }

            table.repaint();
        });
    }


    public void setData(Map<String, Integer> d) {
        if (d.isEmpty())
            return;

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap();

        d.values().removeAll(Collections.singleton(null));
        d.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        Integer i = 0;

        /*for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = entry.getKey().replaceAll("_", " ");
            data[i][2] = entry.getValue().toString();
            i++;
            if (i == 1001) {
                break;}
            */


    }

}
