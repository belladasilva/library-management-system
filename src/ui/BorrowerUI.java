package ui;

import DAO.BorrowerDAO;
import Model.Borrower;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BorrowerUI extends JPanel {
    private final BorrowerDAO dao = new BorrowerDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JLabel statusLabel;

    public BorrowerUI(Runnable showDashboard) {
        super(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = UiStyles.emptyTable(model, "No borrowers yet. Add one using the form.");
        nameField = new JTextField();
        emailField = new JTextField();
        UiStyles.field(nameField);
        UiStyles.field(emailField);
        statusLabel = UiStyles.muted(" ");

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int row = table.convertRowIndexToModel(selectedRow);
                    nameField.setText(String.valueOf(model.getValueAt(row, 1)));
                    emailField.setText(String.valueOf(model.getValueAt(row, 2)));
                }
            }
        });

        JButton addBtn = UiStyles.button("Add", true);
        addBtn.addActionListener(e -> {
            try {
                String name = InputValidator.requiredText(nameField.getText(), "Name");
                String email = InputValidator.email(emailField.getText());
                dao.addBorrower(new Borrower(0, name, email));
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
                UiFeedback.showValidationError(this, "Select a borrower first.");
                return;
            }

            row = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(row, 0);
            String name = String.valueOf(model.getValueAt(row, 1));
            int choice = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + name + "\"? This cannot be undone.",
                    "Delete Borrower", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                if (dao.deleteBorrower(id) == 0) {
                    UiFeedback.showValidationError(this, "That borrower no longer exists. Refresh the list.");
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
                UiFeedback.showValidationError(this, "Select a borrower first.");
                return;
            }

            try {
                int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
                String name = InputValidator.requiredText(nameField.getText(), "Name");
                String email = InputValidator.email(emailField.getText());
                if (dao.updateBorrower(new Borrower(id, name, email)) == 0) {
                    UiFeedback.showValidationError(this, "That borrower no longer exists. Refresh the list.");
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
        page.add(UiStyles.screenHeader(showDashboard, "Borrowers", "Manage registered library users."),
                BorderLayout.NORTH);

        JPanel tablePanel = UiStyles.card(new BorderLayout(0, 12));
        tablePanel.add(UiStyles.sectionTitle("Borrowers"), BorderLayout.NORTH);
        tablePanel.add(UiStyles.scrollPane(table), BorderLayout.CENTER);
        tablePanel.add(statusLabel, BorderLayout.SOUTH);

        JPanel formPanel = UiStyles.card(new BorderLayout(0, 8));
        formPanel.setPreferredSize(new Dimension(280, 0));
        JPanel formFields = new JPanel();
        formFields.setOpaque(false);
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.add(UiStyles.sectionTitle("Borrower details"));
        formFields.add(Box.createVerticalStrut(12));
        UiStyles.addField(formFields, "Name", nameField);
        UiStyles.addField(formFields, "Email", emailField);
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
            List<Borrower> borrowers = dao.getAllBorrowers();
            model.setRowCount(0);
            for (Borrower borrower : borrowers) {
                model.addRow(new Object[]{borrower.getId(), borrower.getName(), borrower.getEmail()});
            }
            statusLabel.setText(borrowers.isEmpty() ? "0 borrowers"
                    : borrowers.size() + (borrowers.size() == 1 ? " borrower" : " borrowers") + " shown.");
        } catch (SQLException exception) {
            UiFeedback.showDatabaseError(this, exception);
        }
    }

    private void clearForm() {
        table.clearSelection();
        nameField.setText("");
        emailField.setText("");
    }
}
