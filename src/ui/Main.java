package ui;

import DAO.DatabaseConnection;

import javax.swing.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        setLookAndFeel();
        try {
            DatabaseConnection.getInstance().initializeSchema();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Could not initialize the library database", exception);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "The library database could not be opened. Check that this folder is writable and try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE));
            return;
        }
        SwingUtilities.invokeLater(LibraryAppFrame::new);
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception crossPlatformException) {
            LOGGER.log(Level.WARNING, "Cross-platform look and feel is unavailable", crossPlatformException);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception systemException) {
                LOGGER.log(Level.WARNING, "System look and feel is unavailable", systemException);
            }
        }
        UiStyles.installThemeDefaults();
    }
}
