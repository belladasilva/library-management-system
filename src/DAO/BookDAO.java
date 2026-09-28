package DAO;

import Model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books(title, author, available_copies) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getAvailableCopies());
            stmt.executeUpdate();

        }
    }

    public int updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, available_copies = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getAvailableCopies());
            stmt.setInt(4, book.getId());
            return stmt.executeUpdate();

        }
    }

    public int deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();

        }
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("available_copies")));
            }

        }

        return books;
    }
}
