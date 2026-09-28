package ui;

import DAO.BorrowedBookDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

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
        borrowBtn.addActionListener(e -> runTransaction(true));
        JButton returnBtn = new JButton("Return");
        returnBtn.addActionListener(e -> runTransaction(false));

        panel.add(borrowBtn);
        panel.add(returnBtn);

        add(panel);
        setVisible(true);
    }

    private void runTransaction(boolean borrow) {
        try {
            int bookId = InputValidator.positiveId(bookIdField.getText(), "Book ID");
            int borrowerId = InputValidator.positiveId(borrowerIdField.getText(), "Borrower ID");
            if (borrow) {
                BorrowedBookDAO.BorrowResult result = dao.borrowBook(bookId, borrowerId);
                switch (result) {
                    case BORROWED -> JOptionPane.showMessageDialog(this, "Book borrowed successfully.");
                    case BOOK_NOT_FOUND -> UiFeedback.showValidationError(this, "Book ID not found.");
                    case BORROWER_NOT_FOUND -> UiFeedback.showValidationError(this, "Borrower ID not found.");
                    case NO_COPIES -> UiFeedback.showValidationError(this, "No copies are available for this book.");
                }
            } else {
                BorrowedBookDAO.ReturnResult result = dao.returnBook(bookId, borrowerId);
                if (result == BorrowedBookDAO.ReturnResult.RETURNED) {
                    JOptionPane.showMessageDialog(this, "Book returned successfully.");
                } else {
                    UiFeedback.showValidationError(this, "No active loan matches these IDs.");
                }
            }
        } catch (IllegalArgumentException exception) {
            UiFeedback.showValidationError(this, exception.getMessage());
        } catch (SQLException exception) {
            UiFeedback.showDatabaseError(this, exception);
        }
    }
}
