import ui.LoginFrame;
import db.DBConnection;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Test database connection
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed!\n" +
                            "Please check your database configuration.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch application
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
