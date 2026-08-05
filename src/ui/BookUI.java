package ui;

import DAO.BookDAO;
import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookUI extends JFrame {
    private BookDAO bookDAO = new BookDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField titleField, authorField, copiesField;

    public BookUI() {
        setTitle("Manage Books");
        setSize(600, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Copies"}, 0);
        table = new JTable(model);
        refreshTable();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Copies:"));
        copiesField = new JTextField();
        inputPanel.add(copiesField);

        JButton addBtn = new JButton("Add Book");
        addBtn.addActionListener(e -> {
            bookDAO.addBook(new Book(0, titleField.getText(), authorField.getText(), Integer.parseInt(copiesField.getText())));
            refreshTable();
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                bookDAO.deleteBook(id);
                refreshTable();
            }
        });

        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                String title = titleField.getText();
                String author = authorField.getText();
                int copies = Integer.parseInt(copiesField.getText());
                bookDAO.updateBook(new Book(id, title, author, copies));
                refreshTable();
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getAvailableCopies()});
        }
    }
}