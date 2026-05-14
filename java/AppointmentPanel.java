import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Date;

/**
 * Appointment Panel - Shows user's appointments
 */
public class AppointmentPanel extends JPanel {
    private MockData mockData;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public AppointmentPanel(MockData mockData) {
        this.mockData = mockData;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Appointment");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel Appointment");
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(titlePanel, BorderLayout.WEST);
        northPanel.add(buttonPanel, BorderLayout.EAST);

        String[] columnNames = {
                "Appointment ID", "User Name", "Role", "Phone", "Gym Type", "Location",
                "Start Time", "End Time", "Duration", "Payment", "Record",
                "Total Eq", "Working Eq", "Broken Eq", "Maint Eq"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        appointmentTable.getTableHeader().setBackground(new Color(59, 89, 152));
        appointmentTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadAppointments();

        addButton.addActionListener(e -> showAddAppointmentDialog());
        cancelButton.addActionListener(e -> cancelSelectedAppointment());
        refreshButton.addActionListener(e -> loadAppointments());
    }

    public void loadAppointments() {
        tableModel.setRowCount(0);

        DBConnection dbConn = DBConnection.getInstance();
        if (dbConn.isUsingMockDatabase()) {
            loadAppointmentsFromMock();
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            Integer currentUserId = UserSession.getCurrentUserId();
            String currentRole = UserSession.getCurrentRole();

            String sql = "SELECT appointment_id, user_name, role, phone_number, " +
                    "gym_type, location, start_time, end_time, duration_hours, " +
                    "payment, record, total_equipment, working_equipment, " +
                    "broken_equipment, maintenance_equipment " +
                    "FROM AppointmentInfo ";

            if (!currentRole.equals("Admin")) {
                sql += "WHERE user_name = (SELECT name FROM User WHERE user_id = ?) ";
            }

            sql += "ORDER BY appointment_id DESC";

            pstmt = conn.prepareStatement(sql);

            if (!currentRole.equals("Admin")) {
                pstmt.setInt(1, currentUserId);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getString("user_name"),
                        rs.getString("role"),
                        rs.getString("phone_number"),
                        convertGymTypeToDisplayName(rs.getString("gym_type")),
                        rs.getString("location"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        String.format("%.1f hrs", rs.getDouble("duration_hours")),
                        "$" + String.format("%.2f", rs.getDouble("payment")),
                        rs.getString("record"),
                        rs.getInt("total_equipment"),
                        rs.getInt("working_equipment"),
                        rs.getInt("broken_equipment"),
                        rs.getInt("maintenance_equipment")
                };

                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading appointments from database:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadAppointmentsFromMock() {
        tableModel.setRowCount(0);

        String[] mockColumnNames = {
                "Appointment ID", "User Name", "Gym Type", "Gym ID",
                "Start Time", "End Time", "Payment", "Status"
        };

        tableModel.setColumnIdentifiers(mockColumnNames);

        Integer currentUserId = UserSession.getCurrentUserId();
        String currentRole = UserSession.getCurrentRole();
        List<Appointment> appointments = mockData.getAppointments();

        for (Appointment appointment : appointments) {
            if (currentRole.equals("Admin") || appointment.getUserId() == currentUserId.intValue()) {
                User user = mockData.getUserById(appointment.getUserId());
                String gymType = getGymTypeDisplayName(appointment.getGymId());

                Object[] row = {
                        appointment.getAppointmentId(),
                        user != null ? user.getName() : "Unknown",
                        gymType,
                        appointment.getGymId(),
                        appointment.getStartTime(),
                        appointment.getEndTime(),
                        "$" + String.format("%.2f", appointment.getPayment()),
                        "Active"
                };

                tableModel.addRow(row);
            }
        }
    }

    private String convertGymTypeToDisplayName(String gymType) {
        switch (gymType) {
            case "Fitness Area": return "Fitness Area";
            case "Dance Room": return "Dance Room";
            case "Basketball Room": return "Basketball Room";
            case "Swimming Pool": return "Swimming Pool";
            case "Badminton Room": return "Badminton Room";
            case "PingPong Room": return "PingPong Room";
            default: return "Unknown";
        }
    }

    private String getGymTypeDisplayName(int gymId) {
        switch (gymId) {
            case 1: return "Fitness Area";
            case 2: return "Dance Room";
            case 3: return "Basketball Room";
            case 4: return "Swimming Pool";
            case 5: return "Badminton Room";
            case 6: return "PingPong Room";
            default: return "Unknown";
        }
    }

    private void showAddAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Appointment", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField userIdField = new JTextField(String.valueOf(UserSession.getCurrentUserId()));
        userIdField.setEditable(false);

        JTextField dateField = new JTextField(TimeSlotUtils.getCurrentDate());
        dateField.setEditable(false);

        JButton dateButton = new JButton("Select Date");
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(dateButton, BorderLayout.EAST);

        JComboBox<String> startTimeCombo = new JComboBox<>();
        JComboBox<String> endTimeCombo = new JComboBox<>();

        JComboBox<String> gymTypeCombo = new JComboBox<>(new String[]{
                "Fitness Area", "Dance Room", "Basketball Room",
                "Swimming Pool", "Badminton Room", "PingPong Room"
        });

        JTextField paymentField = new JTextField();
        paymentField.setEditable(false);

        JComboBox<String> equipmentCombo = new JComboBox<>();
        equipmentCombo.setEnabled(false);

        JTextField equipmentStatusField = new JTextField();
        equipmentStatusField.setEditable(false);
        equipmentStatusField.setText("Select a facility");

        JTextArea recordArea = new JTextArea(3, 20);
        recordArea.setText("New appointment");

        Runnable updateTimeCombos = () -> {
            String selectedType = (String) gymTypeCombo.getSelectedItem();
            String[] hours = getGymHoursByType(selectedType);
            String openTime = hours[0];
            String closeTime = hours[1];

            startTimeCombo.removeAllItems();

            String[] startTimes = TimeSlotUtils.generateTimeSlots(openTime, closeTime);
            for (String time : startTimes) {
                startTimeCombo.addItem(time);
            }

            if (startTimeCombo.getItemCount() > 0) {
                startTimeCombo.setSelectedIndex(0);
            }

            String selectedStart = (String) startTimeCombo.getSelectedItem();

            endTimeCombo.removeAllItems();

            String[] endTimes = TimeSlotUtils.getEndTimeOptions(selectedStart, closeTime);
            for (String time : endTimes) {
                endTimeCombo.addItem(time);
            }

            if (endTimeCombo.getItemCount() > 0) {
                endTimeCombo.setSelectedIndex(0);
            }

            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymTypeCombo);
        };

        startTimeCombo.addActionListener(e -> {
            String selectedType = (String) gymTypeCombo.getSelectedItem();
            String[] hours = getGymHoursByType(selectedType);
            String closeTime = hours[1];

            String selectedStart = (String) startTimeCombo.getSelectedItem();

            endTimeCombo.removeAllItems();

            String[] endTimes = TimeSlotUtils.getEndTimeOptions(selectedStart, closeTime);
            for (String time : endTimes) {
                endTimeCombo.addItem(time);
            }

            if (endTimeCombo.getItemCount() > 0) {
                endTimeCombo.setSelectedIndex(0);
            }

            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymTypeCombo);
        });

        endTimeCombo.addActionListener(e -> {
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymTypeCombo);
        });

        dateButton.addActionListener(e -> {
            JDialog dateDialog = new JDialog(dialog, "Select Date", true);
            dateDialog.setSize(300, 200);
            dateDialog.setLocationRelativeTo(dialog);

            JPanel datePanelDialog = new JPanel(new BorderLayout());

            SpinnerDateModel dateModel = new SpinnerDateModel();
            JSpinner dateSpinner = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);

            try {
                Date currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                dateSpinner.setValue(currentDate);
            } catch (Exception ex) {
                dateSpinner.setValue(new Date());
            }

            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");

            okButton.addActionListener(okEvent -> {
                Date selectedDate = (Date) dateSpinner.getValue();
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                dateField.setText(sdf.format(selectedDate));
                dateDialog.dispose();
            });

            cancelButton.addActionListener(cancelEvent -> dateDialog.dispose());

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            datePanelDialog.add(new JLabel("Select Date:"), BorderLayout.NORTH);
            datePanelDialog.add(dateSpinner, BorderLayout.CENTER);
            datePanelDialog.add(buttonPanel, BorderLayout.SOUTH);

            dateDialog.add(datePanelDialog);
            dateDialog.setVisible(true);
        });

        gymTypeCombo.addActionListener(e -> {
            updateTimeCombos.run();
            updatePriceBasedOnTime(startTimeCombo, endTimeCombo, paymentField, gymTypeCombo);

            String selectedType = (String) gymTypeCombo.getSelectedItem();
            int gymId = getGymIdByType(selectedType);

            VenueType venueType = mockData.getVenueType(gymId);

            List<Equipment> gymEquipment = mockData.getEquipmentByGymId(gymId);
            List<Equipment> workingEquipment = new ArrayList<>();

            equipmentCombo.removeAllItems();

            if (venueType == VenueType.IGNORE_CHECK) {
                equipmentStatusField.setText("No equipment validation required");
                equipmentStatusField.setForeground(new Color(46, 204, 113));
                equipmentCombo.setEnabled(false);
            } else if (venueType == VenueType.REFERENCE) {
                for (Equipment eq : gymEquipment) {
                    equipmentCombo.addItem(eq.getEquipmentId() + " - " + eq.getStatus());
                }

                int workingCount = mockData.getWorkingEquipmentCount(gymId);
                int totalCount = mockData.getTotalEquipmentCount(gymId);

                equipmentStatusField.setText(
                        String.format("Equipment: %d/%d working (reference only)", workingCount, totalCount)
                );
                equipmentStatusField.setForeground(Color.BLACK);
                equipmentCombo.setEnabled(true);
            } else if (venueType == VenueType.STRICT_MATCH) {
                for (Equipment eq : gymEquipment) {
                    if (eq.getStatus().equals("Working")) {
                        workingEquipment.add(eq);
                        equipmentCombo.addItem(eq.getEquipmentId());
                    }
                }

                int workingCount = workingEquipment.size();
                int totalCount = gymEquipment.size();

                if (workingCount == 0) {
                    equipmentStatusField.setText("No working equipment available");
                    equipmentStatusField.setForeground(Color.RED);
                    equipmentCombo.setEnabled(false);
                } else if (mockData.hasBrokenEquipment(gymId)) {
                    equipmentStatusField.setText(
                            String.format("⚠ %d/%d working - Issues detected", workingCount, totalCount)
                    );
                    equipmentStatusField.setForeground(Color.RED);
                    equipmentCombo.setEnabled(true);
                } else {
                    equipmentStatusField.setText(
                            String.format("✓ %d/%d working - All good", workingCount, totalCount)
                    );
                    equipmentStatusField.setForeground(new Color(46, 204, 113));
                    equipmentCombo.setEnabled(true);
                }
            }
        });

        updateTimeCombos.run();

        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel("Gym Type:"));
        panel.add(gymTypeCombo);
        panel.add(new JLabel("Equipment:"));
        panel.add(equipmentCombo);
        panel.add(new JLabel("Equipment Status:"));
        panel.add(equipmentStatusField);
        panel.add(new JLabel("Date:"));
        panel.add(datePanel);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeCombo);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeCombo);
        panel.add(new JLabel("Payment:"));
        panel.add(paymentField);
        panel.add(new JLabel("Record:"));
        panel.add(new JScrollPane(recordArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int userId = Integer.parseInt(userIdField.getText());
                    String gymType = (String) gymTypeCombo.getSelectedItem();
                    int gymId = getGymIdByType(gymType);

                    String date = dateField.getText();
                    String startTime = (String) startTimeCombo.getSelectedItem();
                    String endTime = (String) endTimeCombo.getSelectedItem();

                    String[] hours = getGymHoursByType(gymType);
                    String openTime = hours[0];
                    String closeTime = hours[1];

                    String validationError = TimeSlotUtils.validateTimeSelection(
                            startTime, endTime, openTime, closeTime
                    );

                    if (validationError != null) {
                        JOptionPane.showMessageDialog(
                                dialog,
                                validationError,
                                "时间选择错误",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    String startDateTime = TimeSlotUtils.formatDateTime(date, startTime);
                    String endDateTime = TimeSlotUtils.formatDateTime(date, endTime);

                    double payment = Double.parseDouble(paymentField.getText());
                    String record = recordArea.getText();

                    VenueType venueType = mockData.getVenueType(gymId);
                    String equipmentId = null;

                    if (venueType == VenueType.IGNORE_CHECK) {
                        equipmentId = null;
                    } else if (venueType == VenueType.REFERENCE || venueType == VenueType.STRICT_MATCH) {
                        if (equipmentCombo.getSelectedIndex() == -1 || !equipmentCombo.isEnabled()) {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "No equipment available for booking!",
                                    "Appointment Not Available",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            return;
                        }

                        List<Equipment> gymEquipment = mockData.getEquipmentByGymId(gymId);

                        if (venueType == VenueType.REFERENCE) {
                            equipmentId = gymEquipment.get(equipmentCombo.getSelectedIndex()).getEquipmentId();
                        } else {
                            List<Equipment> workingEquipment = new ArrayList<>();

                            for (Equipment eq : gymEquipment) {
                                if (eq.getStatus().equals("Working")) {
                                    workingEquipment.add(eq);
                                }
                            }

                            equipmentId = workingEquipment.get(equipmentCombo.getSelectedIndex()).getEquipmentId();
                        }
                    }

                    if (DBConnection.getInstance().isUsingMockDatabase()) {
                        int newId = mockData.getAppointments().size() + 1;
                        Appointment appointment = new Appointment(
                                newId,
                                userId,
                                gymId,
                                startDateTime,
                                endDateTime,
                                payment,
                                record
                        );
                        mockData.addAppointment(appointment);
                    } else {
                        Connection conn = null;
                        PreparedStatement pstmt = null;

                        try {
                            conn = DBConnection.getInstance().getConnection();

                            String sql = "INSERT INTO Appointment " +
                                    "(user_id, gym_id, start_time, end_time, payment, record) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)";

                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, userId);
                            pstmt.setInt(2, gymId);
                            pstmt.setString(3, startDateTime);
                            pstmt.setString(4, endDateTime);
                            pstmt.setDouble(5, payment);
                            pstmt.setString(6, record);

                            pstmt.executeUpdate();

                        } finally {
                            if (pstmt != null) {
                                pstmt.close();
                            }
                        }
                    }

                    loadAppointments();
                    dialog.dispose();

                    JOptionPane.showMessageDialog(
                            AppointmentPanel.this,
                            String.format(
                                    "Appointment added successfully!\n\nTime: %s - %s\nPayment: $%.2f",
                                    startDateTime,
                                    endDateTime,
                                    payment
                            )
                    );

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            AppointmentPanel.this,
                            "Invalid payment amount!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            AppointmentPanel.this,
                            "Database error while saving appointment:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String[] getGymHoursByType(String gymType) {
        int gymId = getGymIdByType(gymType);
        Gym gym = mockData.getGymById(gymId);

        if (gym != null) {
            return new String[]{gym.getOpenTime(), gym.getCloseTime()};
        }

        return new String[]{"06:00", "22:00"};
    }

    private void updatePriceBasedOnTime(JComboBox<String> startTimeCombo,
                                        JComboBox<String> endTimeCombo,
                                        JTextField paymentField,
                                        JComboBox<String> gymTypeCombo) {
        String startTime = (String) startTimeCombo.getSelectedItem();
        String endTime = (String) endTimeCombo.getSelectedItem();
        String gymType = (String) gymTypeCombo.getSelectedItem();

        if (startTime != null && endTime != null && gymType != null) {
            double duration = TimeSlotUtils.calculateDurationHours(startTime, endTime);
            double totalPrice;

            if ("Swimming Pool".equals(gymType)) {
                totalPrice = 50.00;
            } else if ("Fitness Area".equals(gymType)) {
                totalPrice = 10.00;
            } else if ("Basketball Room".equals(gymType)) {
                totalPrice = 10.00;
            } else {
                double pricePerHour = getPriceFromDatabase(gymType);
                totalPrice = pricePerHour * duration;
            }

            paymentField.setText(String.format("%.2f", totalPrice));
        }
    }

    private double getPriceFromDatabase(String gymType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double price = 0.0;

        try {
            conn = DBConnection.getInstance().getConnection();

            int gymId = getGymIdByType(gymType);

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
                    return 30.00;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, gymId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                price = rs.getDouble("price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            price = getDefaultPriceByType(gymType);
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception e) {}

            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {}
        }

        return price;
    }

    private double getDefaultPriceByType(String gymType) {
        switch (gymType) {
            case "Fitness Area": return 15.00;
            case "Dance Room": return 100.00;
            case "Basketball Room": return 10.00;
            case "Swimming Pool": return 20.00;
            case "Badminton Room": return 15.00;
            case "PingPong Room": return 10.00;
            default: return 30.00;
        }
    }

    private int getGymIdByType(String gymType) {
        switch (gymType) {
            case "Fitness Area": return 1;
            case "Dance Room": return 2;
            case "Basketball Room": return 3;
            case "Swimming Pool": return 4;
            case "Badminton Room": return 5;
            case "PingPong Room": return 6;
            default: return 1;
        }
    }

    private void cancelSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();

        if (selectedRow >= 0) {
            int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to cancel this appointment?",
                    "Cancel Appointment",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                DBConnection dbConn = DBConnection.getInstance();

                if (dbConn.isUsingMockDatabase()) {
                    mockData.cancelAppointment(appointmentId);
                    loadAppointments();
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                } else {
                    Connection conn = null;
                    PreparedStatement pstmt = null;

                    try {
                        conn = DBConnection.getInstance().getConnection();

                        String sql = "DELETE FROM Appointment WHERE appointment_id = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, appointmentId);

                        int rowsAffected = pstmt.executeUpdate();

                        if (rowsAffected > 0) {
                            loadAppointments();
                            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to cancel appointment.");
                        }

                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Error cancelling appointment:\n" + e.getMessage(),
                                "Database Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pstmt != null) pstmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an appointment to cancel!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}