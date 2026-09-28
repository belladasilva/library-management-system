package ui;

import DAO.BookDAO;
import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
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
            try {
                String title = InputValidator.requiredText(titleField.getText(), "Title");
                String author = InputValidator.requiredText(authorField.getText(), "Author");
                int copies = InputValidator.nonNegativeInt(copiesField.getText(), "Copies");
                bookDAO.addBook(new Book(0, title, author, copies));
                refreshTable();
            } catch (IllegalArgumentException exception) {
                UiFeedback.showValidationError(this, exception.getMessage());
            } catch (SQLException exception) {
                UiFeedback.showDatabaseError(this, exception);
            }
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UiFeedback.showValidationError(this, "Select a book first.");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            try {
                if (bookDAO.deleteBook(id) == 0) {
                    UiFeedback.showValidationError(this, "That book no longer exists. Refresh the list.");
                }
                refreshTable();
            } catch (SQLException exception) {
                UiFeedback.showDeleteError(this, exception);
            }
        });

        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UiFeedback.showValidationError(this, "Select a book first.");
                return;
            }
            try {
                int id = (int) model.getValueAt(row, 0);
                String title = InputValidator.requiredText(titleField.getText(), "Title");
                String author = InputValidator.requiredText(authorField.getText(), "Author");
                int copies = InputValidator.nonNegativeInt(copiesField.getText(), "Copies");
                if (bookDAO.updateBook(new Book(id, title, author, copies)) == 0) {
                    UiFeedback.showValidationError(this, "That book no longer exists. Refresh the list.");
                }
                refreshTable();
            } catch (IllegalArgumentException exception) {
                UiFeedback.showValidationError(this, exception.getMessage());
            } catch (SQLException exception) {
                UiFeedback.showDatabaseError(this, exception);
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
        try {
            List<Book> books = bookDAO.getAllBooks();
            model.setRowCount(0);
            for (Book book : books) {
                model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getAvailableCopies()});
            }
        } catch (SQLException exception) {
            UiFeedback.showDatabaseError(this, exception);
        }
    }
}
