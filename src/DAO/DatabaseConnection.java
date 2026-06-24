package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:library.db";
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();

    private DatabaseConnection() {
        initializeSchema();
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void initializeSchema() {
        String createBooks = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "author TEXT NOT NULL, "
                + "available_copies INTEGER NOT NULL DEFAULT 0"
                + ")";

        String createBorrowers = "CREATE TABLE IF NOT EXISTS borrowers ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "email TEXT NOT NULL"
                + ")";

        String createBorrowedBooks = "CREATE TABLE IF NOT EXISTS borrowed_books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "book_id INTEGER NOT NULL, "
                + "borrower_id INTEGER NOT NULL, "
                + "borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "return_date TIMESTAMP, "
                + "FOREIGN KEY (book_id) REFERENCES books(id), "
                + "FOREIGN KEY (borrower_id) REFERENCES borrowers(id)"
                + ")";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createBooks);
            stmt.execute(createBorrowers);
            stmt.execute(createBorrowedBooks);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }
}
