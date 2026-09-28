package DAO;

import Model.Borrower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BorrowerDAO {
    public void addBorrower(Borrower borrower) throws SQLException {
        String sql = "INSERT INTO borrowers(name, email) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, borrower.getName());
            stmt.setString(2, borrower.getEmail());
            stmt.executeUpdate();

        }
    }

    public int updateBorrower(Borrower borrower) throws SQLException {
        String sql = "UPDATE borrowers SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, borrower.getName());
            stmt.setString(2, borrower.getEmail());
            stmt.setInt(3, borrower.getId());
            return stmt.executeUpdate();

        }
    }

    public int deleteBorrower(int id) throws SQLException {
        String sql = "DELETE FROM borrowers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate();

        }
    }

    public List<Borrower> getAllBorrowers() throws SQLException {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM borrowers ORDER BY id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                borrowers.add(new Borrower(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }

        }

        return borrowers;
    }
}
