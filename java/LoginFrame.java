import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Login Frame for Gym Appointment System
 * Handles user authentication (mock)
 */
public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Gym Appointment System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(59, 89, 152),
                        getWidth(), getHeight(), new Color(109, 139, 202));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Title label
        JLabel titleLabel = new JLabel("Gym Appointment System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // User ID panel
        JPanel userIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userIdPanel.setOpaque(false);
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setForeground(Color.WHITE);
        userIdLabel.setPreferredSize(new Dimension(80, 25));
        userIdField = new JTextField(15);
        userIdPanel.add(userIdLabel);
        userIdPanel.add(userIdField);

        // Role selection panel
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rolePanel.setOpaque(false);
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setPreferredSize(new Dimension(80, 25));
        roleComboBox = new JComboBox<>(new String[]{"Student", "Teacher", "Admin"});
        roleComboBox.setPreferredSize(new Dimension(165, 25));
        rolePanel.add(roleLabel);
        rolePanel.add(roleComboBox);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Add action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Add components to main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        mainPanel.add(userIdPanel, gbc);

        gbc.gridy = 2;
        mainPanel.add(rolePanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 20, 0);
        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        // Simple validation
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate user ID exists in the system
        try {
            String id = userId;
            MockData mockData = new MockData();
            List<User> users = mockData.getUsers();

            boolean userExists = false;
            for (User user : users) {
                if (user.getUserId().equals(id)) {
                    userExists = true;
                    break;
                }
            }

            if (!userExists) {
                JOptionPane.showMessageDialog(this, "User ID not found in system. Please check your ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if role matches
            boolean roleMatches = false;
            if ("Student".equals(role)) {
                for (Student student : mockData.getStudents()) {
                    if (student.getUserId().equals(id)) {
                        roleMatches = true;
                        break;
                    }
                }
            } else if ("Teacher".equals(role)) {
                for (Teacher teacher : mockData.getTeachers()) {
                    if (teacher.getUserId().equals(id)) {
                        roleMatches = true;
                        break;
                    }
                }
            } else if ("Admin".equals(role)) {
                for (Admin admin : mockData.getAdmins()) {
                    if (admin.getUserId().equals(id)) {
                        roleMatches = true;
                        break;
                    }
                }
            }

            if (!roleMatches) {
                JOptionPane.showMessageDialog(this, "User ID does not match the selected role.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Valid user - set session
            UserSession.setCurrentUser(id, role);

            // Open dashboard (same MockData instance as validation — single source of truth)
            DashboardFrame dashboard = new DashboardFrame(mockData);
            dashboard.setVisible(true);
            this.dispose(); // Close login frame

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

/**
 * User session manager
 * Stores current logged-in user information
 */
class UserSession {
    private static String currentUserId;
    private static String currentRole;

    public static void setCurrentUser(String userId, String role) {
        currentUserId = userId;
        currentRole = role;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentRole() {
        return currentRole;
    }
}