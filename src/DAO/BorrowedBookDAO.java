package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BorrowedBookDAO {
    public enum BorrowResult {
        BORROWED, BOOK_NOT_FOUND, BORROWER_NOT_FOUND, NO_COPIES
    }

    public enum ReturnResult {
        RETURNED, NO_ACTIVE_LOAN
    }

    public BorrowResult borrowBook(int bookId, int borrowerId) throws SQLException {
        String checkCopies = "SELECT available_copies FROM books WHERE id = ?";
        String checkBorrower = "SELECT id FROM borrowers WHERE id = ?";
        String insertBorrow = "INSERT INTO borrowed_books(book_id, borrower_id) VALUES (?, ?)";
        String decrementCopies = "UPDATE books SET available_copies = available_copies - 1 "
                + "WHERE id = ? AND available_copies > 0";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkCopies)) {
                    checkStmt.setInt(1, bookId);
                    try (ResultSet result = checkStmt.executeQuery()) {
                        if (!result.next()) {
                            conn.rollback();
                            return BorrowResult.BOOK_NOT_FOUND;
                        }
                        if (result.getInt("available_copies") <= 0) {
                            conn.rollback();
                            return BorrowResult.NO_COPIES;
                        }
                    }
                }

                try (PreparedStatement checkStmt = conn.prepareStatement(checkBorrower)) {
                    checkStmt.setInt(1, borrowerId);
                    try (ResultSet result = checkStmt.executeQuery()) {
                        if (!result.next()) {
                            conn.rollback();
                            return BorrowResult.BORROWER_NOT_FOUND;
                        }
                    }
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(decrementCopies)) {
                    updateStmt.setInt(1, bookId);
                    if (updateStmt.executeUpdate() != 1) {
                        conn.rollback();
                        return BorrowResult.NO_COPIES;
                    }
                }

                try (PreparedStatement insertStmt = conn.prepareStatement(insertBorrow)) {
                    insertStmt.setInt(1, bookId);
                    insertStmt.setInt(2, borrowerId);
                    insertStmt.executeUpdate();
                }

                conn.commit();
                return BorrowResult.BORROWED;
            } catch (SQLException exception) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }

    public ReturnResult returnBook(int bookId, int borrowerId) throws SQLException {
        String closeBorrow = "UPDATE borrowed_books SET return_date = CURRENT_TIMESTAMP "
                + "WHERE id = (SELECT id FROM borrowed_books "
                + "WHERE book_id = ? AND borrower_id = ? AND return_date IS NULL ORDER BY id LIMIT 1)";
        String incrementCopies = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int updatedRows;
                try (PreparedStatement closeStmt = conn.prepareStatement(closeBorrow)) {
                    closeStmt.setInt(1, bookId);
                    closeStmt.setInt(2, borrowerId);
                    updatedRows = closeStmt.executeUpdate();
                }

                if (updatedRows == 0) {
                    conn.rollback();
                    return ReturnResult.NO_ACTIVE_LOAN;
                }

                try (PreparedStatement incStmt = conn.prepareStatement(incrementCopies)) {
                    incStmt.setInt(1, bookId);
                    if (incStmt.executeUpdate() != 1) {
                        throw new SQLException("The book for this loan no longer exists.");
                    }
                }

                conn.commit();
                return ReturnResult.RETURNED;
            } catch (SQLException exception) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
                throw exception;
            }
        }
    }
}
