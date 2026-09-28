package ui;

import DAO.BorrowerDAO;
import Model.Borrower;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class BorrowerUI extends JFrame {
    private final BorrowerDAO dao = new BorrowerDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField nameField;
    private final JTextField emailField;

    public BorrowerUI() {
        setTitle("Manage Borrowers");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"ID", "Name", "Email"}, 0);
        table = new JTable(model);
        refreshTable();

        JPanel input = new JPanel(new GridLayout(2, 2));
        input.add(new JLabel("Name:"));
        nameField = new JTextField();
        input.add(nameField);
        input.add(new JLabel("Email:"));
        emailField = new JTextField();
        input.add(emailField);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    nameField.setText(String.valueOf(model.getValueAt(row, 1)));
                    emailField.setText(String.valueOf(model.getValueAt(row, 2)));
                }
            }
        });

        JButton addBtn = new JButton("Add Borrower");
        addBtn.addActionListener(e -> {
            try {
                String name = InputValidator.requiredText(nameField.getText(), "Name");
                String email = InputValidator.email(emailField.getText());
                dao.addBorrower(new Borrower(0, name, email));
                nameField.setText("");
                emailField.setText("");
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
                UiFeedback.showValidationError(this, "Select a borrower first.");
                return;
            }

            int id = (int) model.getValueAt(row, 0);
            try {
                if (dao.deleteBorrower(id) == 0) {
                    UiFeedback.showValidationError(this, "That borrower no longer exists. Refresh the list.");
                }
                nameField.setText("");
                emailField.setText("");
                refreshTable();
            } catch (SQLException exception) {
                UiFeedback.showDeleteError(this, exception);
            }
        });

        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                UiFeedback.showValidationError(this, "Select a borrower first.");
                return;
            }

            try {
                int id = (int) model.getValueAt(row, 0);
                String name = InputValidator.requiredText(nameField.getText(), "Name");
                String email = InputValidator.email(emailField.getText());
                if (dao.updateBorrower(new Borrower(id, name, email)) == 0) {
                    UiFeedback.showValidationError(this, "That borrower no longer exists. Refresh the list.");
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

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(input, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void refreshTable() {
        try {
            List<Borrower> borrowers = dao.getAllBorrowers();
            model.setRowCount(0);
            for (Borrower borrower : borrowers) {
                model.addRow(new Object[]{borrower.getId(), borrower.getName(), borrower.getEmail()});
            }
        } catch (SQLException exception) {
            UiFeedback.showDatabaseError(this, exception);
        }
    }
}
