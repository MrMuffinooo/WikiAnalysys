package app.main;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {

    public MyFrame(){
        //JFrame frame = new JFrame();//creating instance of JFrame

        this.setSize(1000,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Wiki-stat");
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        JPanel nav = new JPanel();
        JPanel article = new JPanel();
        JPanel footer = new JPanel();

        article.setBackground(Color.blue);
        header.setBackground(Color.yellow);
        nav.setBackground(Color.orange);
        footer.setBackground(Color.gray);

        header.setPreferredSize(new Dimension(100,100));
        nav.setPreferredSize(new Dimension(200,100));
        article.setPreferredSize(new Dimension(100,100));
        footer.setPreferredSize(new Dimension(100,20));

        this.add(header, BorderLayout.NORTH);
        this.add(nav, BorderLayout.WEST);
        this.add(footer, BorderLayout.SOUTH);
        this.add(article, BorderLayout.CENTER);

        //----HEADER---------------------
        JLabel naglowek = new JLabel("HEADER");

        JButton home=new JButton("Home");
        home.setBounds(50,100,95,30);

        header.add(naglowek);
        header.add(home);

        //----NAV------------------------

        String[] items = { "Dzień", "Miesiąc", "Rok"};
        JList<String> dokladnosc = new JList(items);
        dokladnosc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dokladnosc.setBounds(0,50,200,60);




        JTextField t1=new JTextField("Search");
        t1.setBounds(0,0, 200,30);

        nav.setLayout(null);
        nav.add(t1);
        nav.add(dokladnosc);
        //----ARTICLE--------------------

        //----FOOTER---------------------
        JLabel stopka = new JLabel("Adam Frej  Piotr Marciniak  Paweł Niewiadowski \u00a9");

        footer.add(stopka);
        this.setVisible(true);
    }
}
