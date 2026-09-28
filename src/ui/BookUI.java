package ui;

import DAO.BookDAO;
import Model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BookUI extends JPanel {
    private final BookDAO bookDAO = new BookDAO();
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField titleField;
    private final JTextField authorField;
    private final JTextField copiesField;
    private final JLabel statusLabel;

    public BookUI(Runnable showDashboard) {
        super(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Copies"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = UiStyles.emptyTable(model, "No books yet. Add one using the form.");
        UiStyles.highlightAvailableCopies(table);
        titleField = new JTextField();
        authorField = new JTextField();
        copiesField = new JTextField();
        UiStyles.field(titleField);
        UiStyles.field(authorField);
        UiStyles.field(copiesField);
        statusLabel = UiStyles.muted(" ");

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                titleField.setText(String.valueOf(model.getValueAt(row, 1)));
                authorField.setText(String.valueOf(model.getValueAt(row, 2)));
                copiesField.setText(String.valueOf(model.getValueAt(row, 3)));
            }
        });

        JButton addBtn = UiStyles.button("Add", true);
        addBtn.addActionListener(e -> {
            try {
                String title = InputValidator.requiredText(titleField.getText(), "Title");
                String author = InputValidator.requiredText(authorField.getText(), "Author");
                int copies = InputValidator.nonNegativeInt(copiesField.getText(), "Copies");
                bookDAO.addBook(new Book(0, title, author, copies));
                refreshTable();
                clearForm();
            } catch (IllegalArgumentException exception) {
                UiFeedback.showValidationError(this, exception.getMessage());
            } catch (SQLException exception) {
                UiFeedback.showDatabaseError(this, exception);
            }
        });

        JButton deleteBtn = UiStyles.button("Delete", false);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UiFeedback.showValidationError(this, "Select a book first.");
                return;
            }
            row = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(row, 0);
            String title = String.valueOf(model.getValueAt(row, 1));
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + title + "\"? This cannot be undone.",
                    "Delete Book", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                if (bookDAO.deleteBook(id) == 0) {
                    UiFeedback.showValidationError(this, "That book no longer exists. Refresh the list.");
                }
                refreshTable();
                clearForm();
            } catch (SQLException exception) {
                UiFeedback.showDeleteError(this, exception);
            }
        });

        JButton editBtn = UiStyles.button("Update", false);
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UiFeedback.showValidationError(this, "Select a book first.");
                return;
            }
            try {
                int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
                String title = InputValidator.requiredText(titleField.getText(), "Title");
                String author = InputValidator.requiredText(authorField.getText(), "Author");
                int copies = InputValidator.nonNegativeInt(copiesField.getText(), "Copies");
                if (bookDAO.updateBook(new Book(id, title, author, copies)) == 0) {
                    UiFeedback.showValidationError(this, "That book no longer exists. Refresh the list.");
                }
                refreshTable();
                clearForm();
            } catch (IllegalArgumentException exception) {
                UiFeedback.showValidationError(this, exception.getMessage());
            } catch (SQLException exception) {
                UiFeedback.showDatabaseError(this, exception);
            }
        });

        JButton refreshBtn = UiStyles.button("Refresh", false);
        refreshBtn.addActionListener(event -> {
            refreshTable();
            clearForm();
        });

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        JPanel page = UiStyles.page();
        page.add(UiStyles.screenHeader(showDashboard, "Books", "Manage titles and available copies."),
                BorderLayout.NORTH);

        JPanel tablePanel = UiStyles.card(new BorderLayout(0, 12));
        tablePanel.add(UiStyles.sectionTitle("Catalog"), BorderLayout.NORTH);
        tablePanel.add(UiStyles.scrollPane(table), BorderLayout.CENTER);
        tablePanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel formPanel = UiStyles.card(new BorderLayout(0, 8));
        formPanel.setPreferredSize(new Dimension(280, 0));
        JPanel formFields = new JPanel();
        formFields.setOpaque(false);
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.add(UiStyles.sectionTitle("Book details"));
        formFields.add(Box.createVerticalStrut(12));
        UiStyles.addField(formFields, "Title", titleField);
        UiStyles.addField(formFields, "Author", authorField);
        UiStyles.addField(formFields, "Available copies", copiesField);
        formPanel.add(formFields, BorderLayout.NORTH);
        formPanel.add(btnPanel, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(16, 0));
        content.setOpaque(false);
        content.add(tablePanel, BorderLayout.CENTER);
        content.add(formPanel, BorderLayout.EAST);
        page.add(content, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    void onShow() {
        refreshTable();
        clearForm();
    }

    private void refreshTable() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            model.setRowCount(0);
            for (Book book : books) {
                model.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getAvailableCopies()});
            }
            statusLabel.setText(books.isEmpty() ? "0 books"
                    : books.size() + (books.size() == 1 ? " book" : " books") + " shown.");
        } catch (SQLException exception) {
            UiFeedback.showDatabaseError(this, exception);
        }
    }

    private void clearForm() {
        table.clearSelection();
        titleField.setText("");
        authorField.setText("");
        copiesField.setText("");
    }
}
