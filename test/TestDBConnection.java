/**
 * Test class to verify database connection
 */
public class TestDBConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        // Test connection
        DBConnection dbConn = DBConnection.getInstance();
        boolean connected = dbConn.testConnection();

        if (connected) {
            System.out.println("✓ Database connection successful!");
            System.out.println("The system is ready to use with MySQL database.");
        } else {
            System.out.println("✗ Database connection failed!");
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running on localhost:3306");
            System.out.println("2. Database 'group2_gym_system' exists");
            System.out.println("3. Username and password in DBConnection.java are correct");
            System.out.println("4. mysql-connector-j-9.7.0.jar is in the classpath");
        }

        // Close connection
        dbConn.closeConnection();
    }
}