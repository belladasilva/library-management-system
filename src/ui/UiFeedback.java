package ui;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UiFeedback {
    private static final Logger LOGGER = Logger.getLogger(UiFeedback.class.getName());

    private UiFeedback() {
    }

    public static void showValidationError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showDatabaseError(Component parent, SQLException exception) {
        showDatabaseError(parent, exception,
                "The database operation failed. Please try again or check the application log.");
    }

    public static void showDeleteError(Component parent, SQLException exception) {
        String message = "The database operation failed. Please try again or check the application log.";
        if (exception.getErrorCode() == 19 && String.valueOf(exception.getMessage()).contains("FOREIGN KEY")) {
            message = "This record has loan history and cannot be deleted.";
        }
        showDatabaseError(parent, exception, message);
    }

    private static void showDatabaseError(Component parent, SQLException exception, String message) {
        LOGGER.log(Level.SEVERE, "Database operation failed", exception);
        JOptionPane.showMessageDialog(parent,
                message,
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
