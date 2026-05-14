import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

/**
 * Repair Panel - Shows repair records (Admin only)
 */
public class RepairPanel extends JPanel {
    private MockData mockData;
    private JTable repairTable;
    private DefaultTableModel tableModel;

    public RepairPanel(MockData mockData) {
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

        JLabel titleLabel = new JLabel("Repair Records");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addRepairButton = new JButton("Add Repair Record");
        addRepairButton.setBackground(new Color(76, 175, 80));
        addRepairButton.setForeground(Color.WHITE);
        addRepairButton.setFocusPainted(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        buttonPanel.add(addRepairButton);
        buttonPanel.add(refreshButton);

        // Combine title and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(titlePanel, BorderLayout.WEST);
        northPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"Repair ID", "Admin Name", "Equipment ID", "Equipment Status", "Repair Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        repairTable = new JTable(tableModel);
        repairTable.setFont(new Font("Arial", Font.PLAIN, 14));
        repairTable.setRowHeight(25);
        repairTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        repairTable.getTableHeader().setBackground(new Color(59, 89, 152));
        repairTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(repairTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Add components
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadRepairs();

        // Button actions
        addRepairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddRepairDialog();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRepairs();
            }
        });
    }

    private void loadRepairs() {
        // Clear table
        tableModel.setRowCount(0);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            // Query to get repair records with admin and equipment information
            String sql = "SELECT r.repair_id, r.admin_id, r.equipment_id, r.time_point, " +
                        "u.name as admin_name, e.status as equipment_status " +
                        "FROM Repair r " +
                        "JOIN User u ON r.admin_id = u.user_id " +
                        "JOIN Equipment e ON r.equipment_id = e.equipment_id " +
                        "ORDER BY r.repair_id DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int repairId = rs.getInt("repair_id");
                int adminId = rs.getInt("admin_id");
                String equipmentId = rs.getString("equipment_id");
                String timePoint = rs.getString("time_point");
                String adminName = rs.getString("admin_name");
                String equipmentStatus = rs.getString("equipment_status");

                Object[] row = {
                        repairId,
                        adminName,
                        equipmentId,
                        equipmentStatus,
                        timePoint
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading repair records from database:\n" + e.getMessage(),
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

    private void showAddRepairDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Repair Record", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form fields
        JTextField adminIdField = new JTextField(UserSession.getCurrentUserId());
        adminIdField.setEditable(false);

        // Get list of equipment IDs from database
        List<String> equipmentIds = getEquipmentIdsFromDB();
        JComboBox<String> equipmentCombo = new JComboBox<>(equipmentIds.toArray(new String[0]));

        JTextField timeField = new JTextField("2024-01-25 14:00");
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setText("Equipment maintenance performed");

        // Add components
        panel.add(new JLabel("Admin ID:"));
        panel.add(adminIdField);
        panel.add(new JLabel("Equipment ID:"));
        panel.add(equipmentCombo);
        panel.add(new JLabel("Repair Time:"));
        panel.add(timeField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int adminId = Integer.parseInt(adminIdField.getText());
                    String equipmentId = (String) equipmentCombo.getSelectedItem();
                    String timePoint = timeField.getText();
                    String notes = notesArea.getText();

                    Connection conn = null;
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;

                    try {
                        conn = DBConnection.getInstance().getConnection();
                        conn.setAutoCommit(false); // Start transaction

                        // Get the next repair ID
                        String getIdSql = "SELECT MAX(repair_id) as max_id FROM Repair";
                        pstmt = conn.prepareStatement(getIdSql);
                        rs = pstmt.executeQuery();
                        int newId = 1;
                        if (rs.next() && rs.getInt("max_id") > 0) {
                            newId = rs.getInt("max_id") + 1;
                        }
                        rs.close();
                        pstmt.close();

                        // Insert repair record
                        String insertSql = "INSERT INTO Repair (repair_id, admin_id, equipment_id, time_point, notes) " +
                                         "VALUES (?, ?, ?, ?, ?)";
                        pstmt = conn.prepareStatement(insertSql);
                        pstmt.setInt(1, newId);
                        pstmt.setInt(2, adminId);
                        pstmt.setString(3, equipmentId);
                        pstmt.setString(4, timePoint);
                        pstmt.setString(5, notes);

                        int rowsAffected = pstmt.executeUpdate();
                        pstmt.close();

                        if (rowsAffected > 0) {
                            // Update equipment status to Working
                            String updateSql = "UPDATE Equipment SET status = 'Working' WHERE equipment_id = ?";
                            pstmt = conn.prepareStatement(updateSql);
                            pstmt.setString(1, equipmentId);
                            pstmt.executeUpdate();

                            conn.commit(); // Commit transaction

                            loadRepairs();
                            dialog.dispose();
                            JOptionPane.showMessageDialog(RepairPanel.this, "Repair record added successfully!");
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(RepairPanel.this, "Failed to add repair record!", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        if (conn != null) {
                            try {
                                conn.rollback();
                            } catch (SQLException rollbackEx) {
                                rollbackEx.printStackTrace();
                            }
                        }
                        JOptionPane.showMessageDialog(RepairPanel.this,
                            "Error saving repair record:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (rs != null) rs.close();
                            if (pstmt != null) pstmt.close();
                            if (conn != null) conn.setAutoCommit(true);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(RepairPanel.this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Get equipment IDs from database
     */
    private List<String> getEquipmentIdsFromDB() {
        List<String> equipmentIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT equipment_id FROM Equipment ORDER BY equipment_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                equipmentIds.add(rs.getString("equipment_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading equipment IDs:\n" + e.getMessage(),
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
        return equipmentIds;
    }
}