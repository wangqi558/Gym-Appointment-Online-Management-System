import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Dashboard Frame
 * Central hub for all system functions
 */
public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel sidePanel;
    private JLabel welcomeLabel;
    private Map<String, JPanel> panels;
    private AppointmentPanel appointmentPanel;

    /** Dashboard statistic value labels (kept in sync with {@link #mockData}) */
    private JLabel statGymsValue;
    private JLabel statUsersValue;
    private JLabel statAppointmentsValue;
    private JLabel statEquipmentWorkingTotalValue;
    private JLabel statEquipmentIssuesValue;
    private JLabel statRepairsValue;

    private final MockData mockData;

    public DashboardFrame(MockData mockData) {
        this.mockData = mockData;
        setTitle("Gym Appointment System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        panels = new HashMap<>();

        initComponents();
    }

    private void initComponents() {
        // Main container
        setLayout(new BorderLayout());

        // Top panel with welcome message
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(59, 89, 152));
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));

        welcomeLabel = new JLabel("Welcome, " + UserSession.getCurrentUserId() + " (" + UserSession.getCurrentRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Side navigation panel
        sidePanel = createSidePanel();

        // Main content area with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add different panels
        JPanel dashboardPanel = createDashboardPanel();
        FacilityPanel facilityPanel = new FacilityPanel(mockData);
        appointmentPanel = new AppointmentPanel(mockData);
        EquipmentPanel equipmentPanel = new EquipmentPanel(mockData);
        RepairPanel repairPanel = new RepairPanel(mockData);

        panels.put("dashboard", dashboardPanel);
        panels.put("facilities", facilityPanel);
        panels.put("appointments", appointmentPanel);
        panels.put("equipment", equipmentPanel);
        panels.put("repairs", repairPanel);

        // Store reference to appointment panel for refresh
        this.appointmentPanel = appointmentPanel;

        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(facilityPanel, "facilities");
        mainPanel.add(appointmentPanel, "appointments");
        mainPanel.add(equipmentPanel, "equipment");
        mainPanel.add(repairPanel, "repairs");

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    public void refreshAppointmentPanel() {
        if (appointmentPanel != null) {
            appointmentPanel.loadAppointments();
        }
        refreshDashboardStats();
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(43, 53, 63));
        panel.setPreferredSize(new Dimension(200, getHeight()));

        // Navigation buttons
        String[] buttons = {"Dashboard", "Book Facilities", "My Appointments", "Equipment", "Repair Records"};
        String[] cardNames = {"dashboard", "facilities", "appointments", "equipment", "repairs"};

        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton(buttons[i]);
            button.setMaximumSize(new Dimension(180, 40));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setBackground(new Color(59, 89, 152));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            String cardName = cardNames[i];
            button.addActionListener(e -> {
                cardLayout.show(mainPanel, cardName);
                if ("dashboard".equals(cardName)) {
                    refreshDashboardStats();
                }
            });

            // Hide repair button for non-admin users
            if (i == 4 && !UserSession.getCurrentRole().equals("Admin")) {
                button.setVisible(false);
            }

            panel.add(Box.createVerticalStrut(10));
            panel.add(button);
        }

        panel.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);

        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                UserSession.setCurrentUser(null, null);
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(logoutButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Statistics cards — all values derived from shared MockData (same lists as Facility / Equipment / Appointments)
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardsPanel.setBackground(Color.WHITE);

        statGymsValue = new JLabel("0", SwingConstants.CENTER);
        statUsersValue = new JLabel("0", SwingConstants.CENTER);
        statAppointmentsValue = new JLabel("0", SwingConstants.CENTER);
        statEquipmentWorkingTotalValue = new JLabel("0 / 0", SwingConstants.CENTER);
        statEquipmentIssuesValue = new JLabel("0", SwingConstants.CENTER);
        statRepairsValue = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(createStatCard("Total Gyms", statGymsValue, new Color(41, 128, 185)));
        cardsPanel.add(createStatCard("Registered Users", statUsersValue, new Color(52, 73, 94)));
        cardsPanel.add(createStatCard("Total Appointments", statAppointmentsValue, new Color(39, 174, 96)));
        cardsPanel.add(createStatCard("Equipment (Working / Total)", statEquipmentWorkingTotalValue, new Color(142, 68, 173)));
        cardsPanel.add(createStatCard("Equipment Issues (Broken + Maint.)", statEquipmentIssuesValue, new Color(192, 57, 43)));
        cardsPanel.add(createStatCard("Repair Records", statRepairsValue, new Color(211, 84, 0)));

        refreshDashboardStats();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(cardsPanel, BorderLayout.CENTER);

        return panel;
    }

    /** Refresh dashboard numbers from current in-memory mock data (bookings, repairs, equipment status). */
    private void refreshDashboardStats() {
        if (statGymsValue == null) {
            return;
        }
        statGymsValue.setText(String.valueOf(mockData.getGyms().size()));
        statUsersValue.setText(String.valueOf(mockData.getUsers().size()));
        statAppointmentsValue.setText(String.valueOf(mockData.getAppointments().size()));
        int working = mockData.getEquipmentByStatus("Working").size();
        int totalEq = mockData.getEquipment().size();
        statEquipmentWorkingTotalValue.setText(working + " / " + totalEq);
        int issues = mockData.getEquipmentByStatus("Broken").size()
                + mockData.getEquipmentByStatus("Under Maintenance").size();
        statEquipmentIssuesValue.setText(String.valueOf(issues));
        statRepairsValue.setText(String.valueOf(mockData.getRepairs().size()));
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}