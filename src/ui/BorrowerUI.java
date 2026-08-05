package ui;

import DAO.BorrowerDAO;
import Model.Borrower;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.addBorrower(new Borrower(0, name, email));
            nameField.setText("");
            emailField.setText("");
            refreshTable();
        });

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a borrower first.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 0);
            dao.deleteBorrower(id);
            nameField.setText("");
            emailField.setText("");
            refreshTable();
        });

        JButton editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a borrower first.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) model.getValueAt(row, 0);
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.updateBorrower(new Borrower(id, name, email));
            refreshTable();
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
        model.setRowCount(0);
        List<Borrower> borrowers = dao.getAllBorrowers();
        for (Borrower b : borrowers) {
            model.addRow(new Object[]{b.getId(), b.getName(), b.getEmail()});
        }
    }
}