package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Library System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> System.exit(0));
        file.add(quit);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, "Created by Isabella Da Silva"));
        help.add(about);

        menuBar.add(file);
        menuBar.add(help);
        setJMenuBar(menuBar);

        JButton manageBooksBtn = new JButton("📚 Manage Books");
        JButton manageBorrowersBtn = new JButton("👤 Manage Borrowers");
        JButton transactionBtn = new JButton("🔁 Borrow/Return");

        manageBooksBtn.addActionListener(e -> new BookUI());
        manageBorrowersBtn.addActionListener(e -> new BorrowerUI());
        transactionBtn.addActionListener(e -> new TransactionUI());

        setLayout(new GridLayout(3, 1, 10, 10));
        add(manageBooksBtn);
        add(manageBorrowersBtn);
        add(transactionBtn);

        setVisible(true);
    }
}