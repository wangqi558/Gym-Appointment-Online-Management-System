import javax.swing.JOptionPane;

/**
 * Database Adapter - Provides a unified interface for database operations
 * Uses real database when available, falls back to MockData when not
 */
public class DatabaseAdapter {
    private static DatabaseAdapter instance;
    private boolean useRealDatabase = true;
    private MockData mockData;

    private DatabaseAdapter() {
        // Test if real database is available
        DBConnection dbConn = DBConnection.getInstance();
        if (dbConn.isUsingMockDatabase()) {
            useRealDatabase = false;
            mockData = new MockData();
            JOptionPane.showMessageDialog(null,
                "Using mock database for demonstration.\n" +
                "Features will work but data is simulated.",
                "Database Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static synchronized DatabaseAdapter getInstance() {
        if (instance == null) {
            instance = new DatabaseAdapter();
        }
        return instance;
    }

    public boolean isUsingRealDatabase() {
        return useRealDatabase;
    }

    public MockData getMockData() {
        return mockData;
    }

    /**
     * Show a demo message explaining the current state
     */
    public void showDemoMessage() {
        String message = useRealDatabase ?
            "Connected to real MySQL database." :
            "Running in demo mode with mock database.\n" +
            "All features are functional but data is simulated.";

        JOptionPane.showMessageDialog(null,
            message,
            "System Status",
            JOptionPane.INFORMATION_MESSAGE);
    }
}