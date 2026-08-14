package ui;

import DAO.BorrowedBookDAO;

import javax.swing.*;
import java.awt.*;

public class TransactionUI extends JFrame {
    private BorrowedBookDAO dao = new BorrowedBookDAO();
    private JTextField bookIdField, borrowerIdField;

    public TransactionUI() {
        setTitle("Borrow / Return");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        panel.add(bookIdField);

        panel.add(new JLabel("Borrower ID:"));
        borrowerIdField = new JTextField();
        panel.add(borrowerIdField);

        JButton borrowBtn = new JButton("Borrow");
        borrowBtn.addActionListener(e -> dao.borrowBook(Integer.parseInt(bookIdField.getText()), Integer.parseInt(borrowerIdField.getText())));
        JButton returnBtn = new JButton("Return");
        returnBtn.addActionListener(e -> dao.returnBook(Integer.parseInt(bookIdField.getText()), Integer.parseInt(borrowerIdField.getText())));

        panel.add(borrowBtn);
        panel.add(returnBtn);

        add(panel);
        setVisible(true);
    }
}