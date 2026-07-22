package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BorrowedBookDAO {
    public void borrowBook(int bookId, int borrowerId) {
        String checkCopies = "SELECT available_copies FROM books WHERE id = ?";
        String insertBorrow = "INSERT INTO borrowed_books(book_id, borrower_id) VALUES (?, ?)";
        String decrementCopies = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkCopies)) {
                checkStmt.setInt(1, bookId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("available_copies") <= 0) {
                        conn.rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertBorrow);
                 PreparedStatement updateStmt = conn.prepareStatement(decrementCopies)) {
                insertStmt.setInt(1, bookId);
                insertStmt.setInt(2, borrowerId);
                insertStmt.executeUpdate();

                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(int bookId, int borrowerId) {
        String closeBorrow = "UPDATE borrowed_books SET return_date = CURRENT_TIMESTAMP "
                + "WHERE book_id = ? AND borrower_id = ? AND return_date IS NULL";
        String incrementCopies = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            int updatedRows;
            try (PreparedStatement closeStmt = conn.prepareStatement(closeBorrow)) {
                closeStmt.setInt(1, bookId);
                closeStmt.setInt(2, borrowerId);
                updatedRows = closeStmt.executeUpdate();
            }

            if (updatedRows == 0) {
                conn.rollback();
                return;
            }

            try (PreparedStatement incStmt = conn.prepareStatement(incrementCopies)) {
                incStmt.setInt(1, bookId);
                incStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
