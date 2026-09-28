package ui;

import DAO.BorrowedBookDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class TransactionUI extends JPanel {
    private final BorrowedBookDAO dao = new BorrowedBookDAO();
    private final JTextField bookIdField;
    private final JTextField borrowerIdField;
    private final JLabel statusLabel;

    public TransactionUI(Runnable showDashboard) {
        super(new BorderLayout());

        bookIdField = new JTextField();
        borrowerIdField = new JTextField();
        UiStyles.field(bookIdField);
        UiStyles.field(borrowerIdField);
        statusLabel = UiStyles.statusLabel("Ready to record a transaction.");

        JButton borrowBtn = UiStyles.button("Borrow", true);
        borrowBtn.addActionListener(e -> runTransaction(true));
        JButton returnBtn = UiStyles.button("Return", false);
        returnBtn.addActionListener(e -> runTransaction(false));

        JPanel page = UiStyles.page();
        page.add(UiStyles.screenHeader(showDashboard, "Borrow / Return",
                "Manage lending transactions with a book and borrower ID."), BorderLayout.NORTH);

        JPanel formPanel = UiStyles.card(new BorderLayout());
        JPanel formContent = new JPanel();
        formContent.setOpaque(false);
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        UiStyles.addField(formContent, "Book ID", bookIdField);
        UiStyles.addField(formContent, "Borrower ID", borrowerIdField);

        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        actions.add(borrowBtn);
        actions.add(returnBtn);
        formContent.add(actions);
        formContent.add(Box.createVerticalStrut(14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContent.add(statusLabel);
        formPanel.add(formContent, BorderLayout.NORTH);
        page.add(formPanel, BorderLayout.CENTER);

        add(page, BorderLayout.CENTER);
    }

    void onShow() {
        bookIdField.setText("");
        borrowerIdField.setText("");
        UiStyles.updateStatus(statusLabel, "Ready to record a transaction.", false);
    }

    private void runTransaction(boolean borrow) {
        try {
            int bookId = InputValidator.positiveId(bookIdField.getText(), "Book ID");
            int borrowerId = InputValidator.positiveId(borrowerIdField.getText(), "Borrower ID");
            if (borrow) {
                BorrowedBookDAO.BorrowResult result = dao.borrowBook(bookId, borrowerId);
                switch (result) {
                    case BORROWED -> showOutcome("Book borrowed successfully.", true);
                    case BOOK_NOT_FOUND -> showOutcome("Book ID not found.", false);
                    case BORROWER_NOT_FOUND -> showOutcome("Borrower ID not found.", false);
                    case NO_COPIES -> showOutcome("No copies are available for this book.", false);
                }
            } else {
                BorrowedBookDAO.ReturnResult result = dao.returnBook(bookId, borrowerId);
                if (result == BorrowedBookDAO.ReturnResult.RETURNED) {
                    showOutcome("Book returned successfully.", true);
                } else {
                    showOutcome("No active loan matches these IDs.", false);
                }
            }
        } catch (IllegalArgumentException exception) {
            UiStyles.updateStatus(statusLabel, exception.getMessage(), false);
            UiFeedback.showValidationError(this, exception.getMessage());
        } catch (SQLException exception) {
            UiStyles.updateStatus(statusLabel, "The database operation failed.", false);
            UiFeedback.showDatabaseError(this, exception);
        }
    }

    private void showOutcome(String message, boolean success) {
        UiStyles.updateStatus(statusLabel, message, success);
        if (success) {
            UiFeedback.showSuccess(this, message);
        } else {
            UiFeedback.showValidationError(this, message);
        }
    }
}
