/**
 * Simple Main class - Starts the Gym Appointment System with simplified login
 */
public class SimpleMain {
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("GYM APPOINTMENT SYSTEM - STARTING WITH SIMPLE LOGIN");
        System.out.println("=".repeat(60));
        System.out.println("Click the quick login buttons to access the system!");
        System.out.println("=".repeat(60));

        // Set system look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show login frame
        javax.swing.SwingUtilities.invokeLater(() -> {
            SimpleLoginFrame loginFrame = new SimpleLoginFrame();
            loginFrame.setVisible(true);
        });
    }
}