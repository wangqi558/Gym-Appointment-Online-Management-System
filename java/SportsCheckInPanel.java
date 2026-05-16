import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SportsCheckInPanel extends JPanel {

    private static final double TARGET_HOURS = 20.0;

    private final MockData mockData;

    private JLabel hourLabel;
    private JLabel statusLabel;
    private JLabel detailLabel;
    private JProgressBar progressBar;

    public SportsCheckInPanel() {
        this(null);
    }

    public SportsCheckInPanel(MockData mockData) {
        this.mockData = mockData;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initComponents();
        loadSportsHours();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Sports Check-in Hours", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(33, 37, 41));

        hourLabel = new JLabel("Loading...", SwingConstants.CENTER);
        hourLabel.setFont(new Font("Arial", Font.BOLD, 22));
        hourLabel.setForeground(new Color(52, 73, 94));

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(650, 38));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 16));
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(230, 230, 230));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));

        detailLabel = new JLabel("", SwingConstants.CENTER);
        detailLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        detailLabel.setForeground(new Color(108, 117, 125));

        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(hourLabel, gbc);

        gbc.gridy = 2;
        mainPanel.add(progressBar, gbc);

        gbc.gridy = 3;
        mainPanel.add(statusLabel, gbc);

        gbc.gridy = 4;
        mainPanel.add(detailLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadSportsHours() {
        String currentUserId = UserSession.getCurrentUserId();

        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            updateProgress(0.0);
            detailLabel.setText("No user is currently logged in.");
            return;
        }

        double totalHours = loadSportsHoursFromDatabase(currentUserId);

        if (totalHours < 0) {
            totalHours = loadSportsHoursFromMock(currentUserId);
            detailLabel.setText("Calculated from local mock data. Book a new venue to update the progress.");
        } else {
            detailLabel.setText("Calculated from your appointment records in the database.");
        }

        updateProgress(totalHours);
    }

    private double loadSportsHoursFromDatabase(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            DBConnection dbConnection = DBConnection.getInstance();
            if (dbConnection.isUsingMockDatabase()) {
                return -1;
            }

            conn = dbConnection.getConnection();
            if (conn == null) {
                return -1;
            }

            String sql =
                    "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) AS total_minutes " +
                    "FROM Appointment " +
                    "WHERE user_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int totalMinutes = rs.getInt("total_minutes");
                return totalMinutes / 60.0;
            }

            return 0.0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading sports check-in hours from database:\n" + e.getMessage() +
                            "\n\nThe page will try to use local mock data instead.",
                    "Database Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return -1;

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
        }
    }

    private double loadSportsHoursFromMock(String userId) {
        if (mockData == null) {
            return 0.0;
        }

        double totalHours = 0.0;
        List<Appointment> appointments = mockData.getAppointments();

        for (Appointment appointment : appointments) {
            if (String.valueOf(appointment.getUserId()).equals(userId)) {
                totalHours += calculateDurationHours(appointment.getStartTime(), appointment.getEndTime());
            }
        }

        return totalHours;
    }

    private double calculateDurationHours(String startText, String endText) {
        LocalDateTime start = parseDateTime(startText);
        LocalDateTime end = parseDateTime(endText);

        if (start == null || end == null || !end.isAfter(start)) {
            return 0.0;
        }

        return Duration.between(start, end).toMinutes() / 60.0;
    }

    private LocalDateTime parseDateTime(String text) {
        if (text == null) {
            return null;
        }

        String value = text.trim();
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private void updateProgress(double totalHours) {
        double displayedHours = Math.min(totalHours, TARGET_HOURS);
        int percent = (int) Math.round(Math.min(totalHours / TARGET_HOURS * 100.0, 100.0));

        progressBar.setValue(percent);
        progressBar.setString(String.format("%.1f / 20 hours", displayedHours));

        hourLabel.setText(String.format("Current Sports Hours: %.1f hours", totalHours));

        if (totalHours >= TARGET_HOURS) {
            statusLabel.setForeground(new Color(39, 174, 96));
            statusLabel.setText("20-hour sports check-in completed!");
        } else {
            double remaining = TARGET_HOURS - totalHours;
            statusLabel.setForeground(new Color(220, 53, 69));
            statusLabel.setText(String.format("%.1f hours remaining to complete the sports check-in", remaining));
        }
    }

    public void refreshData() {
        loadSportsHours();
    }
}
