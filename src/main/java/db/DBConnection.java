package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = DBConnection.class
                .getResourceAsStream("/config/database.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                URL = prop.getProperty("db.url");
                USERNAME = prop.getProperty("db.username");
                PASSWORD = prop.getProperty("db.password");
                DRIVER = prop.getProperty("db.driver");
            } else {
                // Default values for your setup
                URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";
                USERNAME = "root";
                PASSWORD = "root";
                DRIVER = "com.mysql.cj.jdbc.Driver";
            }
        } catch (Exception e) {
            // Fallback to your database credentials
            URL = "jdbc:mysql://localhost:3306/banking_system?useSSL=false&serverTimezone=UTC";
            USERNAME = "root";
            PASSWORD = "root";
            DRIVER = "com.mysql.cj.jdbc.Driver";
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}

