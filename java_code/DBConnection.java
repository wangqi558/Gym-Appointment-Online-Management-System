import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Database Connection Manager
 * Singleton class to manage MySQL database connections
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/group2_gym_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Chy070204"; // Change this to your MySQL password
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Private constructor for singleton pattern
     */
    private DBConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName(DB_DRIVER);

            // Try to connect to real database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Real database connection established successfully.");

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver not found.\nFalling back to mock database.",
                "Driver Error",
                JOptionPane.WARNING_MESSAGE);
            // Use mock database
            useMockDatabase();
        } catch (SQLException e) {
            System.out.println("Real database connection failed: " + e.getMessage());
            System.out.println("Falling back to mock database...");
            // Use mock database
            useMockDatabase();
        }
    }

    private void useMockDatabase() {
        // For demonstration purposes, we'll use the existing MockData
        // In a real scenario, you would implement a proper mock connection
        System.out.println("Database not available. Using existing MockData class.");
        System.out.println("Note: Some database-specific features may not work in this demo mode.");
        // Set connection to null to indicate mock mode
        connection = null;
    }

    /**
     * Get the singleton instance of DBConnection
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Get the database connection
     * @return Connection object
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or null
            if (connection == null || connection.isClosed()) {
                // Reconnect if needed
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Failed to reconnect to database.\nError: " + e.getMessage(),
                "Reconnection Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Error closing database connection.\nError: " + e.getMessage(),
                "Close Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        Connection testConn = null;
        try {
            testConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection test failed.\nError: " + e.getMessage(),
                "Test Failed",
                JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            if (testConn != null) {
                try {
                    testConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if using mock database
     * @return true if connection is null (using mock)
     */
    public boolean isUsingMockDatabase() {
        return connection == null;
    }
}