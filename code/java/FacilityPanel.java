import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.time.LocalTime;

/**
 * Facility Panel - Shows all gym facilities and allows booking
 */
public class FacilityPanel extends JPanel {
    private MockData mockData;
    private JPanel facilitiesContainer;

    public FacilityPanel(MockData mockData) {
        this.mockData = mockData;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("Book Gym Facilities", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Facilities container with scroll pane - Changed to vertical BoxLayout
        facilitiesContainer = new JPanel();
        facilitiesContainer.setLayout(new BoxLayout(facilitiesContainer, BoxLayout.Y_AXIS));
        facilitiesContainer.setBackground(Color.WHITE);
        facilitiesContainer.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50)); // Increased horizontal padding for wider cards

        JScrollPane scrollPane = new JScrollPane(facilitiesContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Add facility cards
        loadFacilities();

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadFacilities() {
        // Check if we should use mock data
        DBConnection dbConn = DBConnection.getInstance();
        if (dbConn.isUsingMockDatabase()) {
            // Use mock data directly
            loadFacilitiesFromMock();
            return;
        }

        // Otherwise try to use database
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            // Query to get all facilities with their gym information
            String sql = "SELECT g.gym_id, g.gym_type, g.location, g.open_time, g.close_time, " +
                        "CASE " +
                        "WHEN g.gym_type = 'Fitness Area' THEN fa.price " +
                        "WHEN g.gym_type = 'Dance Room' THEN dr.price " +
                        "WHEN g.gym_type = 'Basketball Room' THEN br.price " +
                        "WHEN g.gym_type = 'Swimming Pool' THEN sp.price " +
                        "WHEN g.gym_type = 'Badminton Room' THEN bm.price " +
                        "WHEN g.gym_type = 'PingPong Room' THEN pp.price " +
                        "END as price, " +
                        "CASE " +
                        "WHEN g.gym_type = 'Fitness Area' THEN CAST(fa.equipment_count AS CHAR) " +
                        "WHEN g.gym_type = 'Dance Room' THEN dr.time " +
                        "WHEN g.gym_type = 'Basketball Room' THEN CAST(br.court_count AS CHAR) " +
                        "WHEN g.gym_type = 'Swimming Pool' THEN 'Pool available' " +
                        "WHEN g.gym_type = 'Badminton Room' THEN CAST(bm.court_count AS CHAR) " +
                        "WHEN g.gym_type = 'PingPong Room' THEN CAST(pp.table_count AS CHAR) " +
                        "END as availability_info " +
                        "FROM Gym g " +
                        "LEFT JOIN Fitness_Area fa ON g.gym_id = fa.gym_id " +
                        "LEFT JOIN Dance_Room dr ON g.gym_id = dr.gym_id " +
                        "LEFT JOIN Basketball_Room br ON g.gym_id = br.gym_id " +
                        "LEFT JOIN Swimming_Pool sp ON g.gym_id = sp.gym_id " +
                        "LEFT JOIN Badminton_Room bm ON g.gym_id = bm.gym_id " +
                        "LEFT JOIN PingPong_Room pp ON g.gym_id = pp.gym_id " +
                        "WHERE g.gym_type IN ('Fitness Area', 'Dance Room', 'Basketball Room', 'Swimming Pool', 'Badminton Room', 'PingPong Room') " +
                        "ORDER BY g.gym_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int gymId = rs.getInt("gym_id");
                String gymType = rs.getString("gym_type");
                String location = rs.getString("location");
                String openTime = rs.getString("open_time");
                String closeTime = rs.getString("close_time");
                double price = rs.getDouble("price");
                String availabilityInfo = rs.getString("availability_info");

                // Set facility name based on type
                String facilityName = "";
                String availabilityText = "";
                Color color = Color.GRAY;
                String priceDisplay = String.format("$%.2f/hour", price); // Default to hourly

                switch (gymType) {
                    case "Fitness Area":
                        facilityName = "Fitness Area";
                        availabilityText = availabilityInfo + " equipment available";
                        color = new Color(52, 152, 219);
                        priceDisplay = "$10.00 (Fixed)"; // Fixed price 10
                        break;
                    case "Dance Room":
                        facilityName = "Dance Room";
                        availabilityText = "Schedule: " + availabilityInfo;
                        color = new Color(155, 89, 182);
                        break;
                    case "Basketball Room":
                        facilityName = "Basketball Room";
                        availabilityText = availabilityInfo + " courts available";
                        color = new Color(230, 126, 34);
                        priceDisplay = "$10.00 (Fixed)"; // Fixed price 10
                        break;
                    case "Swimming Pool":
                        facilityName = "Swimming Pool";
                        availabilityText = availabilityInfo;
                        color = new Color(46, 204, 113);
                        priceDisplay = "$50.00 (Fixed)"; // Fixed price 50
                        break;
                    case "Badminton Room":
                        facilityName = "Badminton Room";
                        availabilityText = availabilityInfo + " courts available";
                        color = new Color(26, 188, 156);
                        break;
                    case "PingPong Room":
                        facilityName = "PingPong Room";
                        availabilityText = availabilityInfo + " tables available";
                        color = new Color(241, 196, 15);
                        priceDisplay = String.format("$%.2f/hour", price);
                        break;
                    default:
                        priceDisplay = String.format("$%.2f/hour", price);
                        break;
                }

                facilitiesContainer.add(createFacilityCard(
                    facilityName,
                    location,
                    openTime + " - " + closeTime,
                    priceDisplay,
                    availabilityText,
                    color,
                    gymId
                ));

                // Add spacing between facility cards
                facilitiesContainer.add(Box.createVerticalStrut(20));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading facilities from database:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createFacilityCard(String name, String location, String hours, String price, String availability, Color color, int gymId) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header with facility name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(color);

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel locationLabel = new JLabel("Location: " + location);
        JLabel hoursLabel = new JLabel("Hours: " + hours);
        JLabel priceLabel = new JLabel("Price: " + price);
        JLabel availabilityLabel = new JLabel(availability);

        // Style labels
        Font detailFont = new Font("Arial", Font.PLAIN, 14);
        locationLabel.setFont(detailFont);
        hoursLabel.setFont(detailFont);
        priceLabel.setFont(detailFont);
        availabilityLabel.setFont(detailFont);
        availabilityLabel.setForeground(new Color(46, 204, 113));

        detailsPanel.add(locationLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(hoursLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(availabilityLabel);

        // Add equipment status information based on venue type
        VenueType venueType = mockData.getVenueType(gymId);
        int workingCount = getWorkingEquipmentCountFromDB(gymId);
        int totalCount = getTotalEquipmentCountFromDB(gymId);

        // Add venue type label
        JLabel venueTypeLabel = new JLabel(mockData.getVenueTypeDescription(gymId));
        venueTypeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        venueTypeLabel.setForeground(Color.DARK_GRAY);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(venueTypeLabel);

        // Show equipment info based on venue type
        if (venueType == VenueType.IGNORE_CHECK) {
            // No equipment check needed
            JLabel equipmentLabel = new JLabel("No equipment validation required");
            equipmentLabel.setFont(detailFont);
            equipmentLabel.setForeground(new Color(46, 204, 113));
            detailsPanel.add(Box.createVerticalStrut(5));
            detailsPanel.add(equipmentLabel);
        } else if (venueType == VenueType.REFERENCE) {
            // Show equipment status for reference
            JLabel equipmentLabel = new JLabel(String.format("Equipment: %d/%d working (for reference)", workingCount, totalCount));
            equipmentLabel.setFont(detailFont);
            detailsPanel.add(Box.createVerticalStrut(5));
            detailsPanel.add(equipmentLabel);

            // Show warning if equipment issues but don't block
            if (hasBrokenEquipmentFromDB(gymId)) {
                JLabel warningLabel = new JLabel("⚠ Equipment issues detected (booking still allowed)");
                warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
                warningLabel.setForeground(Color.ORANGE);
                detailsPanel.add(Box.createVerticalStrut(5));
                detailsPanel.add(warningLabel);
            }
        } else if (venueType == VenueType.STRICT_MATCH) {
            // Show equipment status and consistency check
            JLabel equipmentLabel = new JLabel(String.format("Equipment: %d/%d working", workingCount, totalCount));
            equipmentLabel.setFont(detailFont);
            detailsPanel.add(Box.createVerticalStrut(5));
            detailsPanel.add(equipmentLabel);

            // Check equipment consistency
            if (mockData.hasEquipmentConsistencyIssue(gymId)) {
                int expected = mockData.getExpectedEquipmentCount(gymId);
                JLabel consistencyLabel = new JLabel(String.format("⚠ Config issue: Expected %d equipment but found %d",
                    expected, totalCount));
                consistencyLabel.setFont(new Font("Arial", Font.BOLD, 12));
                consistencyLabel.setForeground(Color.RED);
                detailsPanel.add(Box.createVerticalStrut(5));
                detailsPanel.add(consistencyLabel);
            }

            // Show warning if equipment is broken
            if (hasBrokenEquipmentFromDB(gymId)) {
                JLabel warningLabel = new JLabel("⚠ Equipment issues detected - booking may be restricted");
                warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
                warningLabel.setForeground(Color.RED);
                detailsPanel.add(Box.createVerticalStrut(5));
                detailsPanel.add(warningLabel);
            }
        }

        // Add gym ID
        JLabel gymIdLabel = new JLabel("Facility ID: " + gymId);
        gymIdLabel.setFont(detailFont);
        gymIdLabel.setForeground(Color.GRAY);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(gymIdLabel);

        // Book button
        JButton bookButton = new JButton("Book Now");
        bookButton.setBackground(color);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setFocusPainted(false);
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usesScheduleGridBooking(gymId)) {
                    showScheduleGridBookingDialog(name, location, gymId);
                } else {
                    showBookingDialog(name, location, gymId);
                }
            }
        });

        card.add(nameLabel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(bookButton, BorderLayout.SOUTH);

        return card;
    }

    private void showBookingDialog(String facilityName, String location, int gymId) {
        VenueType venueType = mockData.getVenueType(gymId);

        // Handle different venue types
        if (venueType == VenueType.IGNORE_CHECK) {
            // For ignore check venues (swimming pool, dance room), allow direct booking
            showDirectBookingDialog(facilityName, location, gymId, 0);
            return;
        }

        if (venueType == VenueType.REFERENCE) {
            // For reference venues (fitness area), show equipment status but allow any equipment
            showReferenceBookingDialog(facilityName, location, gymId);
            return;
        }

        // For strict match venues, use existing equipment selection logic
        showStrictMatchBookingDialog(facilityName, location, gymId);
    }

    private void showDirectBookingDialog(String facilityName, String location, int gymId, int equipmentId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book " + facilityName, true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Get base price and open/close times for this gym
        double basePrice = getBasePriceForGym(gymId);
        String[] gymHours = getGymHoursFromDB(gymId);
        String openTime = gymHours[0];
        String closeTime = gymHours[1];

        // Form fields
        JTextField userIdField = new JTextField(String.valueOf(UserSession.getCurrentUserId()));
        userIdField.setEditable(false);

        JTextField facilityField = new JTextField(facilityName);
        facilityField.setEditable(false);

        JTextField locationField = new JTextField(location);
        locationField.setEditable(false);

        JTextField gymIdField = new JTextField(String.valueOf(gymId));
        gymIdField.setEditable(false);

        // Date field (default to today)
        JTextField dateField = new JTextField(TimeSlotUtils.getCurrentDate());
        dateField.setEditable(false);

        // Time selection with combo boxes based on gym hours
        JComboBox<String> startTimeCombo = TimeSlotUtils.createStartTimeComboBox(openTime, closeTime);
        JComboBox<String> endTimeCombo = new JComboBox<String>(); // Start with empty combo

        // Initialize end time options based on the default start time. Close time can be included.
        String selectedStartForInit = (String) startTimeCombo.getSelectedItem();
        String[] allTimes = TimeSlotUtils.getEndTimeOptions(selectedStartForInit, closeTime);
        for (String time : allTimes) {
            endTimeCombo.addItem(time);
        }
        JTextField paymentField = new JTextField();
        paymentField.setEditable(false); // Price is calculated automatically

        // Update end times when start time changes - now smarter
        startTimeCombo.addActionListener(e -> {
            String selectedStart = (String) startTimeCombo.getSelectedItem();
            if (selectedStart != null) {
                endTimeCombo.removeAllItems();
                String[] availableTimes = TimeSlotUtils.getEndTimeOptions(selectedStart, closeTime);

                for (String time : availableTimes) {
                    endTimeCombo.addItem(time);
                }

                if (endTimeCombo.getItemCount() > 0) {
                    endTimeCombo.setSelectedIndex(0);
                }
            }
            // Update price when time changes
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        endTimeCombo.addActionListener(e -> {
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        JTextArea recordArea = new JTextArea(3, 20);
        recordArea.setText("Booking for " + facilityName);

        // Add components
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Facility:"));
        panel.add(facilityField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Facility ID:"));
        panel.add(gymIdField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeCombo);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeCombo);
        panel.add(new JLabel("Payment:"));
        panel.add(paymentField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(recordArea));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    String date = dateField.getText();
                    String startTime = (String) startTimeCombo.getSelectedItem();
                    String endTime = (String) endTimeCombo.getSelectedItem();

                    // Format full date time
                    String startDateTime = TimeSlotUtils.formatDateTime(date, startTime);
                    String endDateTime = TimeSlotUtils.formatDateTime(date, endTime);
                    double payment = Double.parseDouble(paymentField.getText());
                    String record = recordArea.getText();

                    // Validate time selection against gym hours
                    String validationError = TimeSlotUtils.validateTimeSelection(startTime, endTime, openTime, closeTime);
                    if (validationError != null) {
                        JOptionPane.showMessageDialog(dialog, validationError, "时间选择错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Create appointment without equipment_id
                    boolean success = createAppointmentInDB(userId, gymId, startDateTime, endDateTime, payment, record);
                    if (!success) {
                        throw new Exception("Failed to create appointment in database");
                    }

                    JOptionPane.showMessageDialog(dialog,
                            "Booking confirmed!\n\nFacility: " + facilityName +
                            "\nFacility ID: " + gymId +
                            "\nLocation: " + location +
                            "\nTime: " + startDateTime + " - " + endDateTime +
                            "\nPayment: $" + String.format("%.2f", payment),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();

                    // Refresh appointment panel if it's open
                    Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(FacilityPanel.this);
                    if (mainFrame instanceof DashboardFrame) {
                        DashboardFrame dashboard = (DashboardFrame) mainFrame;
                        dashboard.refreshAppointmentPanel();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid payment amount", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error creating appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Update price based on selected time duration - Modified to show duration and fetch from database
     * Fixed pricing for Swimming Pool (50), Fitness Area (10), and Basketball Room (10)
     */
    private void updatePriceBasedOnTime(JComboBox<String> startTimeCombo,
                                       JComboBox<String> endTimeCombo,
                                       JTextField paymentField,
                                       int gymId) {
        String startTime = (String) startTimeCombo.getSelectedItem();
        String endTime = (String) endTimeCombo.getSelectedItem();

        if (startTime != null && endTime != null) {
            double duration = TimeSlotUtils.calculateDurationHours(startTime, endTime);

            // Validate time selection
            if (duration <= 0) {
                paymentField.setText("Invalid Time Selection");
                return;
            }

            // Get gym type for fixed pricing check
            String gymType = getGymTypeById(gymId);
            double totalPrice;

            // Fixed pricing for specific gym types
            if ("Swimming Pool".equals(gymType)) {
                totalPrice = 50.00; // Fixed price 50
            } else if ("Fitness Area".equals(gymType)) {
                totalPrice = 10.00; // Fixed price 10
            } else if ("Basketball Room".equals(gymType)) {
                totalPrice = 10.00; // Fixed price 10
            } else {
                // Hourly pricing for other gym types
                double pricePerHour = getPriceFromDatabase(gymId);
                totalPrice = pricePerHour * duration;
            }

            // Show duration and total price
            paymentField.setText(String.format("%.2f", totalPrice));

            // Update the record field if exists
            JTextArea recordArea = findRecordArea(paymentField);
            if (recordArea != null) {
                if ("Swimming Pool".equals(gymType) || "Fitness Area".equals(gymType) || "Basketball Room".equals(gymType)) {
                    recordArea.setText(String.format("Duration: %.1f Hours\nFixed Price: $%.2f",
                        duration, totalPrice));
                } else {
                    double pricePerHour = getPriceFromDatabase(gymId);
                    recordArea.setText(String.format("Duration: %.1f Hours\nPrice per hour: $%.2f\nTotal Price: $%.2f",
                        duration, pricePerHour, totalPrice));
                }
            }
        }
    }

    /**
     * Get price from database based on gym ID
     * Queries the appropriate table based on gym type
     */
    private double getPriceFromDatabase(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double price = 0.0;

        try {
            conn = DBConnection.getInstance().getConnection();

            // First get the gym type
            String getTypeSql = "SELECT gym_type FROM Gym WHERE gym_id = ?";
            pstmt = conn.prepareStatement(getTypeSql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            String gymType = null;
            if (rs.next()) {
                gymType = rs.getString("gym_type");
            }
            rs.close();
            pstmt.close();

            if (gymType == null) {
                return 30.00; // Default price
            }

            // Query the appropriate table based on gym type
            String sql = "";
            switch (gymType) {
                case "Fitness Area":
                    sql = "SELECT price FROM Fitness_Area WHERE gym_id = ?";
                    break;
                case "Dance Room":
                    sql = "SELECT price FROM Dance_Room WHERE gym_id = ?";
                    break;
                case "Basketball Room":
                    sql = "SELECT price FROM Basketball_Room WHERE gym_id = ?";
                    break;
                case "Swimming Pool":
                    sql = "SELECT price FROM Swimming_Pool WHERE gym_id = ?";
                    break;
                case "Badminton Room":
                    sql = "SELECT price FROM Badminton_Room WHERE gym_id = ?";
                    break;
                case "PingPong Room":
                    sql = "SELECT price FROM PingPong_Room WHERE gym_id = ?";
                    break;
                default:
                    return 30.00; // Default price
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback to default prices if database query fails
            price = getDefaultPriceById(gymId);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
        }

        return price;
    }

    /**
     * Find the record area text component in the dialog
     */
    private JTextArea findRecordArea(Component parent) {
        if (parent instanceof JTextArea) {
            return (JTextArea) parent;
        }
        if (parent instanceof Container) {
            for (Component comp : ((Container) parent).getComponents()) {
                JTextArea result = findRecordArea(comp);
                if (result != null) {
                    return result;
                }
            }
            if (parent instanceof JScrollPane) {
                JViewport viewport = ((JScrollPane) parent).getViewport();
                if (viewport.getView() instanceof JTextArea) {
                    return (JTextArea) viewport.getView();
                }
            }
        }
        return null;
    }
    private double getDefaultPriceById(int gymId) {
        switch (gymId) {
            case 1: return 10.00; // Fitness Area
            case 2: return 100.00; // Dance Room
            case 3: return 10.00; // Basketball Room
            case 4: return 20.00; // Swimming Pool
            case 5: return 15.00; // Badminton Room
            case 6: return 10.00; // PingPong Room
            default: return 30.00;
        }
    }
    private double getBasePriceForGym(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double price = 30.0; // Default price

        try {
            conn = DBConnection.getInstance().getConnection();

            // Query to get price from the appropriate table based on gym type
            String sql = "SELECT CASE " +
                        "WHEN g.gym_type = 'Fitness Area' THEN fa.price " +
                        "WHEN g.gym_type = 'Dance Room' THEN dr.price " +
                        "WHEN g.gym_type = 'Basketball Room' THEN br.price " +
                        "WHEN g.gym_type = 'Swimming Pool' THEN sp.price " +
                        "WHEN g.gym_type = 'Badminton Room' THEN bm.price " +
                        "WHEN g.gym_type = 'PingPong Room' THEN pp.price " +
                        "END as price " +
                        "FROM Gym g " +
                        "LEFT JOIN Fitness_Area fa ON g.gym_id = fa.gym_id " +
                        "LEFT JOIN Dance_Room dr ON g.gym_id = dr.gym_id " +
                        "LEFT JOIN Basketball_Room br ON g.gym_id = br.gym_id " +
                        "LEFT JOIN Swimming_Pool sp ON g.gym_id = sp.gym_id " +
                        "LEFT JOIN Badminton_Room bm ON g.gym_id = bm.gym_id " +
                        "LEFT JOIN PingPong_Room pp ON g.gym_id = pp.gym_id " +
                        "WHERE g.gym_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return price;
    }

    /**
     * Get working equipment count from database
     */
    private int getWorkingEquipmentCountFromDB(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) as count FROM Equipment WHERE gym_id = ? AND status = 'Working'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * Get total equipment count from database
     */
    private int getTotalEquipmentCountFromDB(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) as count FROM Equipment WHERE gym_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * Get equipment by gym ID from database
     */
    private List<Equipment> getEquipmentByGymIdFromDB(int gymId) {
        List<Equipment> equipment = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT equipment_id, status FROM Equipment WHERE gym_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("equipment_id");
                String status = rs.getString("status");
                equipment.add(new Equipment(equipmentId, status, gymId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return equipment;
    }

    /**
     * Get gym type by gym ID
     */
    private String getGymTypeById(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String gymType = "Unknown";

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT gym_type FROM Gym WHERE gym_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                gymType = rs.getString("gym_type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return gymType;
    }

    /**
     * Get gym hours from database
     */
    private String[] getGymHoursFromDB(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] hours = {"06:00", "22:00"}; // Default hours

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT open_time, close_time FROM Gym WHERE gym_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Convert HH:mm:ss to HH:mm format
                String openTimeFull = rs.getString("open_time");
                String closeTimeFull = rs.getString("close_time");

                // Extract only HH:mm part
                hours[0] = openTimeFull.substring(0, 5);
                hours[1] = closeTimeFull.substring(0, 5);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hours;
    }
    private boolean createAppointmentInDB(int userId, int gymId,
                                         String startDateTime, String endDateTime,
                                         double payment, String record) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            // Get the next appointment ID
            String getIdSql = "SELECT MAX(appointment_id) as max_id FROM Appointment";
            pstmt = conn.prepareStatement(getIdSql);
            rs = pstmt.executeQuery();
            int newId = 1;
            if (rs.next() && rs.getInt("max_id") > 0) {
                newId = rs.getInt("max_id") + 1;
            }
            rs.close();
            pstmt.close();

            // Insert the appointment (without status field)
            String insertSql = "INSERT INTO Appointment (appointment_id, user_id, gym_id, " +
                             "start_time, end_time, payment, record) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, newId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, gymId);
            pstmt.setString(4, startDateTime);
            pstmt.setString(5, endDateTime);
            pstmt.setDouble(6, payment);
            pstmt.setString(7, record);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Check for trigger errors
            if (e.getMessage().contains("Admin cannot make appointments")) {
                JOptionPane.showMessageDialog(this,
                    "Admin users cannot make appointments!\nPlease log in as a student or teacher.",
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error creating appointment:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if gym has broken equipment from database
     */
    private boolean hasBrokenEquipmentFromDB(int gymId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean hasBroken = false;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) as count FROM Equipment WHERE gym_id = ? AND status IN ('Broken', 'Maintenance')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                hasBroken = rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return hasBroken;
    }



    private void showReferenceBookingDialog(String facilityName, String location, int gymId) {
        // Get all equipment for this gym (not just working)
        List<Equipment> gymEquipment = getEquipmentByGymIdFromDB(gymId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book " + facilityName, true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Get base price and open/close times for this gym
        double basePrice = getBasePriceForGym(gymId);
        String[] gymHours = getGymHoursFromDB(gymId);
        String openTime = gymHours[0];
        String closeTime = gymHours[1];

        // Form fields
        JTextField userIdField = new JTextField(String.valueOf(UserSession.getCurrentUserId()));
        userIdField.setEditable(false);

        JTextField facilityField = new JTextField(facilityName);
        facilityField.setEditable(false);

        JTextField locationField = new JTextField(location);
        locationField.setEditable(false);

        JTextField gymIdField = new JTextField(String.valueOf(gymId));
        gymIdField.setEditable(false);

        // Date field (default to today)
        JTextField dateField = new JTextField(TimeSlotUtils.getCurrentDate());
        dateField.setEditable(false);

        // Time selection with combo boxes based on gym hours
        JComboBox<String> startTimeCombo = TimeSlotUtils.createStartTimeComboBox(openTime, closeTime);
        JComboBox<String> endTimeCombo = new JComboBox<String>(); // Start with empty combo

        // Initialize end time options based on the default start time. Close time can be included.
        String selectedStartForInit = (String) startTimeCombo.getSelectedItem();
        String[] allTimes = TimeSlotUtils.getEndTimeOptions(selectedStartForInit, closeTime);
        for (String time : allTimes) {
            endTimeCombo.addItem(time);
        }
        JTextField paymentField = new JTextField();
        paymentField.setEditable(false); // Price is calculated automatically

        // Update end times when start time changes - now smarter
        startTimeCombo.addActionListener(e -> {
            String selectedStart = (String) startTimeCombo.getSelectedItem();
            if (selectedStart != null) {
                endTimeCombo.removeAllItems();
                String[] availableTimes = TimeSlotUtils.getEndTimeOptions(selectedStart, closeTime);

                for (String time : availableTimes) {
                    endTimeCombo.addItem(time);
                }

                if (endTimeCombo.getItemCount() > 0) {
                    endTimeCombo.setSelectedIndex(0);
                }
            }
            // Update price when time changes
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        endTimeCombo.addActionListener(e -> {
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        // Equipment selection (show all equipment, not just working)
        JComboBox<String> equipmentCombo = new JComboBox<>();
        for (Equipment eq : gymEquipment) {
            equipmentCombo.addItem(eq.getEquipmentId() + " - " + eq.getStatus());
        }

        JTextArea recordArea = new JTextArea(3, 20);
        recordArea.setText("Booking for " + facilityName);

        // Add components
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Facility:"));
        panel.add(facilityField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Facility ID:"));
        panel.add(gymIdField);
        panel.add(new JLabel("Equipment:"));
        panel.add(equipmentCombo);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeCombo);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeCombo);
        panel.add(new JLabel("Payment:"));
        panel.add(paymentField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(recordArea));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    String date = dateField.getText();
                    String startTime = (String) startTimeCombo.getSelectedItem();
                    String endTime = (String) endTimeCombo.getSelectedItem();

                    // Format full date time
                    String startDateTime = TimeSlotUtils.formatDateTime(date, startTime);
                    String endDateTime = TimeSlotUtils.formatDateTime(date, endTime);
                    double payment = Double.parseDouble(paymentField.getText());
                    String record = recordArea.getText();

                    // Validate time selection against gym hours
                    String validationError = TimeSlotUtils.validateTimeSelection(startTime, endTime, openTime, closeTime);
                    if (validationError != null) {
                        JOptionPane.showMessageDialog(dialog, validationError, "时间选择错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Get selected equipment
                    int selectedIndex = equipmentCombo.getSelectedIndex();
                    Equipment selectedEquipment = gymEquipment.get(selectedIndex);

                    // Create appointment - record equipment info in text field instead of foreign key
                    record = String.format("Booked Unit: %s", selectedEquipment.getEquipmentId());
                    boolean success = createAppointmentInDB(userId, gymId, startDateTime, endDateTime, payment, record);
                    if (!success) {
                        throw new Exception("Failed to create appointment in database");
                    }

                    JOptionPane.showMessageDialog(dialog,
                            "Booking confirmed!\n\nFacility: " + facilityName +
                            "\nFacility ID: " + gymId +
                            "\nEquipment ID: " + selectedEquipment.getEquipmentId() +
                            "\nEquipment Status: " + selectedEquipment.getStatus() +
                            "\nLocation: " + location +
                            "\nTime: " + startDateTime + " - " + endDateTime +
                            "\nPayment: $" + String.format("%.2f", payment),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();

                    // Refresh appointment panel if it's open
                    Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(FacilityPanel.this);
                    if (mainFrame instanceof DashboardFrame) {
                        DashboardFrame dashboard = (DashboardFrame) mainFrame;
                        dashboard.refreshAppointmentPanel();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid payment amount", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error creating appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showStrictMatchBookingDialog(String facilityName, String location, int gymId) {
        // Get available equipment for this gym
        List<Equipment> gymEquipment = getEquipmentByGymIdFromDB(gymId);
        List<Equipment> workingEquipment = new ArrayList<>();

        for (Equipment eq : gymEquipment) {
            if (eq.getStatus().equals("Working")) {
                workingEquipment.add(eq);
            }
        }

        // If no working equipment, show message
        if (workingEquipment.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No working equipment available in this facility!\n\nAll equipment is either broken or under maintenance.",
                    "Booking Not Available",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book " + facilityName, true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form fields
        JTextField userIdField = new JTextField(String.valueOf(UserSession.getCurrentUserId()));
        userIdField.setEditable(false);

        JTextField facilityField = new JTextField(facilityName);
        facilityField.setEditable(false);

        JTextField locationField = new JTextField(location);
        locationField.setEditable(false);

        JTextField gymIdField = new JTextField(String.valueOf(gymId));
        gymIdField.setEditable(false);

        // Get base price and open/close times for this gym
        double basePrice = getBasePriceForGym(gymId);
        String[] gymHours = getGymHoursFromDB(gymId);
        String openTime = gymHours[0];
        String closeTime = gymHours[1];

        // Equipment selection
        JComboBox<String> equipmentCombo = new JComboBox<>();
        for (Equipment eq : workingEquipment) {
            equipmentCombo.addItem(eq.getEquipmentId());
        }

        // Date field (default to today)
        JTextField dateField = new JTextField(TimeSlotUtils.getCurrentDate());
        dateField.setEditable(false);

        // Time selection with combo boxes based on gym hours
        JComboBox<String> startTimeCombo = TimeSlotUtils.createStartTimeComboBox(openTime, closeTime);
        JComboBox<String> endTimeCombo = new JComboBox<String>(); // Start with empty combo

        // Initialize end time options based on the default start time. Close time can be included.
        String selectedStartForInit = (String) startTimeCombo.getSelectedItem();
        String[] allTimes = TimeSlotUtils.getEndTimeOptions(selectedStartForInit, closeTime);
        for (String time : allTimes) {
            endTimeCombo.addItem(time);
        }
        JTextField paymentField = new JTextField();
        paymentField.setEditable(false); // Price is calculated automatically
        JTextArea recordArea = new JTextArea(3, 20);
        recordArea.setText("Booking for " + facilityName);

        // Update end times when start time changes - now smarter
        startTimeCombo.addActionListener(e -> {
            String selectedStart = (String) startTimeCombo.getSelectedItem();
            if (selectedStart != null) {
                endTimeCombo.removeAllItems();
                String[] availableTimes = TimeSlotUtils.getEndTimeOptions(selectedStart, closeTime);

                for (String time : availableTimes) {
                    endTimeCombo.addItem(time);
                }

                if (endTimeCombo.getItemCount() > 0) {
                    endTimeCombo.setSelectedIndex(0);
                }
            }
            // Update price when time changes
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        endTimeCombo.addActionListener(e -> {
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymId);
        });

        // Add components
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Facility:"));
        panel.add(facilityField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Facility ID:"));
        panel.add(gymIdField);
        panel.add(new JLabel("Equipment:"));
        panel.add(equipmentCombo);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeCombo);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeCombo);
        panel.add(new JLabel("Payment:"));
        panel.add(paymentField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(recordArea));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Confirm Booking");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    String date = dateField.getText();
                    String startTime = (String) startTimeCombo.getSelectedItem();
                    String endTime = (String) endTimeCombo.getSelectedItem();

                    // Format full date time
                    String startDateTime = TimeSlotUtils.formatDateTime(date, startTime);
                    String endDateTime = TimeSlotUtils.formatDateTime(date, endTime);
                    double payment = Double.parseDouble(paymentField.getText());
                    String record = recordArea.getText();

                    // Validate time selection against gym hours
                    String validationError = TimeSlotUtils.validateTimeSelection(startTime, endTime, openTime, closeTime);
                    if (validationError != null) {
                        JOptionPane.showMessageDialog(dialog, validationError, "时间选择错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Get selected equipment
                    int selectedIndex = equipmentCombo.getSelectedIndex();
                    Equipment selectedEquipment = workingEquipment.get(selectedIndex);

                    // Create appointment - record equipment info in text field instead of foreign key
                    record = String.format("Booked Unit: %s", selectedEquipment.getEquipmentId());
                    boolean success = createAppointmentInDB(userId, gymId, startDateTime, endDateTime, payment, record);
                    if (!success) {
                        throw new Exception("Failed to create appointment in database");
                    }

                    JOptionPane.showMessageDialog(dialog,
                            "Booking confirmed!\n\nFacility: " + facilityName +
                            "\nFacility ID: " + gymId +
                            "\nEquipment ID: " + selectedEquipment.getEquipmentId() +
                            "\nLocation: " + location +
                            "\nTime: " + startDateTime + " - " + endDateTime +
                            "\nPayment: $" + String.format("%.2f", payment),
                            "Booking Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();

                    // Refresh appointment panel if it's open
                    Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(FacilityPanel.this);
                    if (mainFrame instanceof DashboardFrame) {
                        DashboardFrame dashboard = (DashboardFrame) mainFrame;
                        dashboard.refreshAppointmentPanel();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid payment amount", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error creating appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    /**
     * These facilities use the visual schedule-grid booking UI.
     * We do not add any new database attribute. The selected unit is stored in Appointment.record.
     */
    private boolean usesScheduleGridBooking(int gymId) {
        String gymType = getGymTypeById(gymId);
        return "Badminton Room".equals(gymType)
                || "PingPong Room".equals(gymType)
                || "Dance Room".equals(gymType);
    }

    /**
     * Show a schedule grid like: time slots x courts/tables/rooms.
     * For Badminton and PingPong, units come from Equipment.
     * For Dance Room, we use a logical unit name stored in record: dance_room1.
     */
    private void showScheduleGridBookingDialog(String facilityName, String location, int gymId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book " + facilityName, true);
        dialog.setSize(950, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] gymHours = getGymHoursFromDB(gymId);
        String openTime = gymHours[0];
        String closeTime = gymHours[1];
        double pricePerHour = getPriceFromDatabase(gymId);

        JLabel titleLabel = new JLabel(facilityName + " Schedule Booking", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        JTextField dateField = new JTextField(TimeSlotUtils.getCurrentDate(), 10);
        dateField.setEditable(false);

        JButton refreshButton = new JButton("Refresh");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Date:"));
        topPanel.add(dateField);
        topPanel.add(new JLabel("Hours: " + openTime + " - " + closeTime));
        topPanel.add(new JLabel("Price/hour: $" + String.format("%.2f", pricePerHour)));
        topPanel.add(refreshButton);

        JPanel gridPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JLabel selectionInfoLabel = new JLabel("Select any available slots in this facility, then click Confirm Booking. Daily limit: 2 hours per user per facility.");
        selectionInfoLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton clearSelectionButton = new JButton("Clear Selection");
        clearSelectionButton.setBackground(new Color(46, 204, 113));
        clearSelectionButton.setForeground(Color.WHITE);
        clearSelectionButton.setOpaque(true);
        clearSelectionButton.setBorderPainted(false);
        clearSelectionButton.setFocusPainted(false);

        JButton confirmBookingButton = new JButton("Confirm Booking");
        confirmBookingButton.setBackground(new Color(52, 152, 219));
        confirmBookingButton.setForeground(Color.WHITE);
        confirmBookingButton.setOpaque(true);
        confirmBookingButton.setBorderPainted(false);
        confirmBookingButton.setFocusPainted(false);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.add(clearSelectionButton);
        bottomButtonPanel.add(confirmBookingButton);
        bottomPanel.add(selectionInfoLabel, BorderLayout.WEST);
        bottomPanel.add(bottomButtonPanel, BorderLayout.EAST);

        Color availableColor = new Color(46, 204, 113);
        Color selectedColor = new Color(52, 152, 219);
        Color bookedColor = Color.LIGHT_GRAY;

        class SelectedSlot {
            String unit;
            String start;
            String end;
            String startDateTime;
            String endDateTime;
            JButton button;

            SelectedSlot(String unit, String start, String end, String startDateTime, String endDateTime, JButton button) {
                this.unit = unit;
                this.start = start;
                this.end = end;
                this.startDateTime = startDateTime;
                this.endDateTime = endDateTime;
                this.button = button;
            }
        }

        List<SelectedSlot> selectedSlots = new ArrayList<>();
        final Runnable[] buildGrid = new Runnable[1];

        Runnable updateSelectionInfo = () -> {
            if (selectedSlots.isEmpty()) {
                double existingHours = getUserBookedHoursForDate(UserSession.getCurrentUserId(), gymId, dateField.getText());
                selectionInfoLabel.setText(String.format(
                        "Select any available slots, then click Confirm Booking. Already booked today in this facility: %.1f / 2.0 hour(s).",
                        existingHours
                ));
                return;
            }

            selectedSlots.sort((a, b) -> {
                int byTime = a.startDateTime.compareTo(b.startDateTime);
                if (byTime != 0) return byTime;
                return a.unit.compareTo(b.unit);
            });

            double existingHours = getUserBookedHoursForDate(UserSession.getCurrentUserId(), gymId, dateField.getText());
            double newHours = selectedSlots.size();
            double payment = pricePerHour * newHours;

            selectionInfoLabel.setText(String.format(
                    "Selected: %d slot(s) | Current: %.1f hour(s) | Already booked today: %.1f hour(s) | Total after confirm: %.1f / 2.0 hour(s) | Payment: $%.2f",
                    selectedSlots.size(),
                    newHours,
                    existingHours,
                    existingHours + newHours,
                    payment
            ));
        };

        clearSelectionButton.addActionListener(e -> {
            for (SelectedSlot slot : selectedSlots) {
                if (slot.button != null) {
                    slot.button.setBackground(availableColor);
                    slot.button.setForeground(Color.WHITE);
                }
            }
            selectedSlots.clear();
            updateSelectionInfo.run();
        });

        confirmBookingButton.addActionListener(e -> {
            if (selectedSlots.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select at least one time slot first.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            selectedSlots.sort((a, b) -> {
                int byTime = a.startDateTime.compareTo(b.startDateTime);
                if (byTime != 0) return byTime;
                return a.unit.compareTo(b.unit);
            });

            double existingHours = getUserBookedHoursForDate(
                    UserSession.getCurrentUserId(),
                    gymId,
                    dateField.getText()
            );
            double newHours = selectedSlots.size();

            if (existingHours + newHours > 2.0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        String.format(
                                "Daily booking limit exceeded.\n\nAlready booked today in this facility: %.1f hour(s)\nCurrent selection: %.1f hour(s)\nMaximum allowed per day per facility: 2.0 hours",
                                existingHours,
                                newHours
                        ),
                        "Booking Limit",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            for (SelectedSlot slot : selectedSlots) {
                if (isUnitBooked(gymId, slot.unit, slot.startDateTime, slot.endDateTime)) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "One of the selected slots has just been booked. Please refresh and choose again.",
                            "Slot No Longer Available",
                            JOptionPane.WARNING_MESSAGE
                    );
                    buildGrid[0].run();
                    return;
                }
            }

            StringBuilder bookingDetails = new StringBuilder();
            for (SelectedSlot slot : selectedSlots) {
                bookingDetails.append("- ")
                        .append(getUnitDisplayName(slot.unit))
                        .append(": ")
                        .append(slot.startDateTime)
                        .append(" - ")
                        .append(slot.endDateTime)
                        .append("\n");
            }

            double payment = pricePerHour * newHours;

            int confirm = JOptionPane.showConfirmDialog(
                    dialog,
                    "Confirm booking?\n\n" +
                            "Facility: " + facilityName + "\n" +
                            "Selected slots:\n" + bookingDetails +
                            "Current selected duration: " + String.format("%.1f", newHours) + " hour(s)\n" +
                            "Total duration today after confirm: " + String.format("%.1f", existingHours + newHours) + " / 2.0 hour(s)\n" +
                            "Payment: $" + String.format("%.2f", payment),
                    "Confirm Booking",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                for (SelectedSlot slot : selectedSlots) {
                    boolean success = createAppointmentInDB(
                            UserSession.getCurrentUserId(),
                            gymId,
                            slot.startDateTime,
                            slot.endDateTime,
                            pricePerHour,
                            "Booked Unit: " + slot.unit
                    );

                    if (!success) {
                        throw new Exception("Failed to create appointment for " + getUnitDisplayName(slot.unit) + " at " + slot.start + " - " + slot.end);
                    }
                }

                JOptionPane.showMessageDialog(dialog, "Booking successful!");
                selectedSlots.clear();
                buildGrid[0].run();

                Frame mainFrame = (Frame) SwingUtilities.getWindowAncestor(FacilityPanel.this);
                if (mainFrame instanceof DashboardFrame) {
                    DashboardFrame dashboard = (DashboardFrame) mainFrame;
                    dashboard.refreshAppointmentPanel();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Booking failed:\n" + ex.getMessage(),
                        "Booking Error",
                        JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });

        buildGrid[0] = () -> {
            selectedSlots.clear();
            updateSelectionInfo.run();
            gridPanel.removeAll();

            List<String> units = getBookableUnits(gymId);
            String[] startTimes = TimeSlotUtils.generateTimeSlots(openTime, closeTime);

            if (units.isEmpty()) {
                gridPanel.setLayout(new BorderLayout());
                JLabel emptyLabel = new JLabel("No available units for this facility.", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.BOLD, 16));
                gridPanel.add(emptyLabel, BorderLayout.CENTER);
                gridPanel.revalidate();
                gridPanel.repaint();
                return;
            }

            gridPanel.setLayout(new GridLayout(startTimes.length + 1, units.size() + 1, 6, 6));

            JLabel timeHeader = new JLabel("Time", SwingConstants.CENTER);
            timeHeader.setFont(new Font("Arial", Font.BOLD, 13));
            gridPanel.add(timeHeader);

            for (String unit : units) {
                JLabel unitLabel = new JLabel(getUnitDisplayName(unit), SwingConstants.CENTER);
                unitLabel.setFont(new Font("Arial", Font.BOLD, 12));
                unitLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                gridPanel.add(unitLabel);
            }

            for (String start : startTimes) {
                String[] endOptions = TimeSlotUtils.getEndTimeOptions(start, closeTime);
                if (endOptions.length == 0) {
                    continue;
                }

                String end = endOptions[0];
                String startDateTime = TimeSlotUtils.formatDateTime(dateField.getText(), start);
                String endDateTime = TimeSlotUtils.formatDateTime(dateField.getText(), end);

                JLabel timeLabel = new JLabel(start + " - " + end, SwingConstants.CENTER);
                timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                gridPanel.add(timeLabel);

                for (String unit : units) {
                    boolean booked = isUnitBooked(gymId, unit, startDateTime, endDateTime);
                    JButton slotButton = new JButton(booked ? "Booked" : "$" + String.format("%.2f", pricePerHour));
                    slotButton.setFocusPainted(false);
                    slotButton.setOpaque(true);

                    if (booked) {
                        slotButton.setEnabled(false);
                        slotButton.setBackground(bookedColor);
                        slotButton.setForeground(Color.DARK_GRAY);
                    } else {
                        slotButton.setBackground(availableColor);
                        slotButton.setForeground(Color.WHITE);
                    }

                    slotButton.addActionListener(e -> {
                        SelectedSlot existingSlot = null;
                        for (SelectedSlot slot : selectedSlots) {
                            if (slot.unit.equals(unit) && slot.startDateTime.equals(startDateTime) && slot.endDateTime.equals(endDateTime)) {
                                existingSlot = slot;
                                break;
                            }
                        }

                        if (existingSlot != null) {
                            selectedSlots.remove(existingSlot);
                            slotButton.setBackground(availableColor);
                            slotButton.setForeground(Color.WHITE);
                            updateSelectionInfo.run();
                            return;
                        }

                        selectedSlots.add(new SelectedSlot(unit, start, end, startDateTime, endDateTime, slotButton));
                        slotButton.setBackground(selectedColor);
                        slotButton.setForeground(Color.WHITE);
                        updateSelectionInfo.run();
                    });

                    gridPanel.add(slotButton);
                }
            }

            gridPanel.revalidate();
            gridPanel.repaint();
        };

        refreshButton.addActionListener(e -> buildGrid[0].run());
        buildGrid[0].run();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(topPanel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Count how many hours this user has already booked in this facility on the selected date.
     * This supports the rule: one user can book at most 2 hours per day for the same gym.
     */
    private double getUserBookedHoursForDate(int userId, int gymId, String date) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalHours = 0.0;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT COALESCE(SUM(TIMESTAMPDIFF(MINUTE, start_time, end_time)), 0) AS total_minutes " +
                    "FROM Appointment " +
                    "WHERE user_id = ? " +
                    "AND gym_id = ? " +
                    "AND DATE(start_time) = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, gymId);
            pstmt.setString(3, date);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                totalHours = rs.getDouble("total_minutes") / 60.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return totalHours;
    }

    /**
     * Get bookable units for the schedule-grid UI.
     * Badminton/PingPong use Working rows from Equipment.
     * Dance Room has no equipment row in your current dataset, so we use one logical unit.
     */
    private List<String> getBookableUnits(int gymId) {
        List<String> units = new ArrayList<>();
        String gymType = getGymTypeById(gymId);

        if ("Dance Room".equals(gymType)) {
            units.add("dance_room1");
            return units;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT equipment_id FROM Equipment WHERE gym_id = ? AND status = 'Working' ORDER BY equipment_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                units.add(rs.getString("equipment_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return units;
    }

    /**
     * Check whether a specific unit is already booked at the selected time.
     * The unit ID is stored in the existing Appointment.record field.
     */
    private boolean isUnitBooked(int gymId, String unitId, String startDateTime, String endDateTime) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            /*
             * Check time overlap instead of exact start/end equality.
             *
             * Example:
             * If an appointment is stored as 08:00-10:00,
             * then both 08:00-09:00 and 09:00-10:00 should be shown as booked in the grid.
             */
            String sql = "SELECT COUNT(*) AS count FROM Appointment " +
                    "WHERE gym_id = ? " +
                    "AND record LIKE ? " +
                    "AND start_time < ? " +
                    "AND end_time > ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            pstmt.setString(2, "%Booked Unit: " + unitId + "%");
            pstmt.setString(3, endDateTime);
            pstmt.setString(4, startDateTime);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return false;
    }

    /**
     * Make database IDs easier to read in the schedule grid.
     */
    private String getUnitDisplayName(String unitId) {
        if (unitId == null) {
            return "Unknown";
        }

        if (unitId.startsWith("badminton_court")) {
            String number = unitId.replace("badminton_court", "").replace("_equipment", "");
            return "Court " + number;
        }

        if (unitId.startsWith("pingpong_table")) {
            String number = unitId.replace("pingpong_table", "").replace("_equipment", "");
            return "Table " + number;
        }

        if (unitId.startsWith("dance_room")) {
            String number = unitId.replace("dance_room", "");
            return "Dance Room " + number;
        }

        return unitId;
    }

    /**
     * Load facilities from mock data when database is not available
     */
    private void loadFacilitiesFromMock() {
        // Get mock data directly
        MockData mockData = new MockData();
        List<Gym> gyms = mockData.getGyms();

        for (Gym gym : gyms) {
            String gymType = "";
            double price = 0.0;
            String availabilityText = "";
            Color color = Color.GRAY;

            // Determine gym type and get specific details
            if (gym.getGymId() == 1) {
                gymType = "Fitness Area";
                FitnessArea fa = mockData.getFitnessAreas().get(0);
                price = fa.getPrice();
                availabilityText = fa.getEquipmentCount() + " equipment available";
                color = new Color(52, 152, 219);
            } else if (gym.getGymId() == 2) {
                gymType = "Dance Room";
                DanceRoom dr = mockData.getDanceRooms().get(0);
                price = dr.getPrice();
                availabilityText = "Schedule: " + dr.getTime();
                color = new Color(155, 89, 182);
            } else if (gym.getGymId() == 3) {
                gymType = "Basketball Room";
                BasketballRoom br = mockData.getBasketballRooms().get(0);
                price = br.getPrice();
                availabilityText = br.getCourtCount() + " courts available";
                color = new Color(230, 126, 34);
            } else if (gym.getGymId() == 4) {
                gymType = "Swimming Pool";
                SwimmingPool sp = mockData.getSwimmingPools().get(0);
                price = sp.getPrice();
                availabilityText = "Olympic size pool";
                color = new Color(46, 204, 113);
            } else if (gym.getGymId() == 5) {
                gymType = "Badminton Room";
                BadmintonRoom br = mockData.getBadmintonRooms().get(0);
                price = br.getPrice();
                availabilityText = br.getCourtCount() + " courts available";
                color = new Color(26, 188, 156);
            } else if (gym.getGymId() == 6) {
                gymType = "PingPong Room";
                PingPongRoom ppr = mockData.getPingPongRooms().get(0);
                price = ppr.getPrice();
                availabilityText = ppr.getTableCount() + " tables available";
                color = new Color(241, 196, 15);
            }

            facilitiesContainer.add(createFacilityCard(
                gymType,
                gym.getLocation(),
                gym.getOpenTime() + " - " + gym.getCloseTime(),
                "$" + price + "/hour",
                availabilityText,
                color,
                gym.getGymId()
            ));

            // Add spacing between facility cards
            facilitiesContainer.add(Box.createVerticalStrut(20));
        }
    }
}