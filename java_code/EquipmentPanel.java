import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

/**
 * Equipment Panel - Shows equipment information and allows repair requests
 */
public class EquipmentPanel extends JPanel {
    private MockData mockData;
    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    public EquipmentPanel(MockData mockData) {
        this.mockData = mockData;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Equipment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton repairButton = new JButton("Request Repair");
        repairButton.setBackground(new Color(220, 53, 69));
        repairButton.setForeground(Color.WHITE);
        repairButton.setFocusPainted(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        buttonPanel.add(repairButton);
        buttonPanel.add(refreshButton);

        // Combine title and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(titlePanel, BorderLayout.WEST);
        northPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"Equipment ID", "Gym Location", "Status", "Last Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        equipmentTable = new JTable(tableModel);
        equipmentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        equipmentTable.setRowHeight(25);
        equipmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        equipmentTable.getTableHeader().setBackground(new Color(59, 89, 152));
        equipmentTable.getTableHeader().setForeground(Color.WHITE);

        // Status column (index 2): Working = green; Broken / Under Maintenance = yellow
        final Color statusYellow = new Color(234, 179, 8);
        final Color statusGreen = new Color(22, 163, 74);
        final Color statusRed = new Color(220, 53, 69);
        equipmentTable.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value == null) {
                    c.setForeground(table.getForeground());
                    return c;
                }
                String status = value.toString();
                if ("Working".equals(status)) {
                    c.setForeground(statusGreen);
                    } else if ("Under Maintenance".equals(status)) {
                        c.setForeground(statusYellow);
                    } else if ("Broken".equals(status)) {
                        c.setForeground(statusRed);
                    } else {
                        c.setForeground(table.getForeground());
                    }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Add components
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadEquipment();

        // Button actions
        repairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestRepair();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadEquipment();
            }
        });
    }

    private void loadEquipment() {
        // Clear table
        tableModel.setRowCount(0);

        // Check if we should use mock data
        DBConnection dbConn = DBConnection.getInstance();
        if (dbConn.isUsingMockDatabase()) {
            // Use mock data directly
            loadEquipmentFromMock();
            return;
        }

        // Otherwise try to use database
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            // Query to get equipment with gym location
            String sql = "SELECT e.equipment_id, e.status, e.last_updated, g.location " +
                        "FROM Equipment e " +
                        "JOIN Gym g ON e.gym_id = g.gym_id " +
                        "ORDER BY e.equipment_id";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("equipment_id");
                String location = rs.getString("location");
                String status = rs.getString("status");
                Timestamp lastUpdated = rs.getTimestamp("last_updated");

                Object[] row = {
                        equipmentId,
                        location,
                        status,
                        lastUpdated != null ? lastUpdated.toString().substring(0, 10) : "N/A"
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading equipment from database:\n" + e.getMessage(),
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

    private void requestRepair() {
        int selectedRow = equipmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String equipmentId = (String) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);

            if (currentStatus.equals("Broken")) {
                JOptionPane.showMessageDialog(this,
                        "This equipment is already marked as broken and repair has been requested.",
                        "Repair Request",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to request repair for Equipment ID: " + equipmentId + "?",
                        "Request Repair",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    // Check if we should use mock data
                    DBConnection dbConn = DBConnection.getInstance();
                    if (dbConn.isUsingMockDatabase()) {
                        // Update mock data directly
                        MockData mockData = new MockData();
                        mockData.updateEquipmentStatus(equipmentId, "Under Maintenance");
                        loadEquipment();
                        JOptionPane.showMessageDialog(this,
                                "Repair request submitted successfully!\nEquipment ID: " + equipmentId,
                                "Repair Request",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Update equipment status in database
                        Connection conn = null;
                        PreparedStatement pstmt = null;

                        try {
                            conn = DBConnection.getInstance().getConnection();
                            String sql = "UPDATE Equipment SET status = 'Under Maintenance' WHERE equipment_id = ?";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, equipmentId);

                            int rowsAffected = pstmt.executeUpdate();
                            if (rowsAffected > 0) {
                                loadEquipment();
                                JOptionPane.showMessageDialog(this,
                                        "Repair request submitted successfully!\nEquipment ID: " + equipmentId,
                                        "Repair Request",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Failed to update equipment status.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(this,
                                    "Error updating equipment status:\n" + e.getMessage(),
                                    "Database Error",
                                    JOptionPane.ERROR_MESSAGE);
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
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an equipment to request repair!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load equipment from mock data when database is not available
     */
    private void loadEquipmentFromMock() {
        MockData mockData = new MockData();
        List<Equipment> equipmentList = mockData.getEquipment();

        for (Equipment equipment : equipmentList) {
            Gym gym = mockData.getGymById(equipment.getGymId());
            String location = gym != null ? gym.getLocation() : "Unknown";

            Object[] row = {
                equipment.getEquipmentId(),
                location,
                equipment.getStatus(),
                "2024-01-15" // Mock last updated date
            };
            tableModel.addRow(row);
        }
    }
}