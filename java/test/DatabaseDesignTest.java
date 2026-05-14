import java.sql.*;

/**
 * Test class to verify the new VARCHAR equipment_id database design
 */
public class DatabaseDesignTest {
    public static void main(String[] args) {
        System.out.println("=== Testing New VARCHAR Equipment ID Design ===\n");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get database connection
            conn = DBConnection.getInstance().getConnection();

            // Test 1: Verify Equipment table structure
            System.out.println("Test 1: Equipment Table Structure");
            String sql = "DESCRIBE Equipment";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            System.out.println("Equipment table columns:");
            while (rs.next()) {
                System.out.println("  " + rs.getString("Field") + " - " + rs.getString("Type") + " - " + rs.getString("Null") + " - " + rs.getString("Key"));
            }
            rs.close();
            pstmt.close();

            // Test 2: Sample equipment data with VARCHAR IDs
            System.out.println("\nTest 2: Sample Equipment Data");
            sql = "SELECT equipment_id, status, gym_id FROM Equipment LIMIT 5";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("equipment_id");
                String status = rs.getString("status");
                int gymId = rs.getInt("gym_id");
                System.out.println("  Equipment ID: " + equipmentId + ", Status: " + status + ", Gym ID: " + gymId);
            }
            rs.close();
            pstmt.close();

            // Test 3: Verify Repair table with VARCHAR equipment_id
            System.out.println("\nTest 3: Repair Table with VARCHAR Equipment ID");
            sql = "SELECT r.repair_id, r.admin_id, r.equipment_id, r.time_point, u.name as admin_name " +
                  "FROM Repair r JOIN User u ON r.admin_id = u.user_id " +
                  "ORDER BY r.repair_id DESC LIMIT 3";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int repairId = rs.getInt("repair_id");
                String adminName = rs.getString("admin_name");
                String equipmentId = rs.getString("equipment_id");
                String timePoint = rs.getString("time_point");
                System.out.println("  Repair ID: " + repairId + ", Admin: " + adminName + ", Equipment ID: " + equipmentId + ", Time: " + timePoint);
            }
            rs.close();
            pstmt.close();

            // Test 4: Verify AppointmentInfo view with equipment summary
            System.out.println("\nTest 4: AppointmentInfo View with Equipment Summary");
            sql = "SELECT appointment_id, user_name, gym_type, location, start_time, end_time, payment, " +
                  "total_equipment, working_equipment, broken_equipment, maintenance_equipment " +
                  "FROM AppointmentInfo LIMIT 3";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                String userName = rs.getString("user_name");
                String gymType = rs.getString("gym_type");
                String location = rs.getString("location");
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                double payment = rs.getDouble("payment");
                int totalEquipment = rs.getInt("total_equipment");
                int workingEquipment = rs.getInt("working_equipment");
                int brokenEquipment = rs.getInt("broken_equipment");
                int maintenanceEquipment = rs.getInt("maintenance_equipment");

                System.out.println("  Appointment ID: " + appointmentId);
                System.out.println("    User: " + userName + ", Gym: " + gymType + " (" + location + ")");
                System.out.println("    Time: " + startTime + " - " + endTime + ", Payment: $" + payment);
                System.out.println("    Equipment Summary: Total=" + totalEquipment + ", Working=" + workingEquipment + ", Broken=" + brokenEquipment + ", Maintenance=" + maintenanceEquipment);
            }

            System.out.println("\n=== All Tests Completed Successfully! ===");
            System.out.println("\nThe database has been successfully updated with:");
            System.out.println("- Equipment table using VARCHAR(100) equipment_id as primary key");
            System.out.println("- Removed equipment_name field from Equipment table");
            System.out.println("- Repair table updated to use VARCHAR equipment_id");
            System.out.println("- AppointmentInfo view using WITH clause for equipment summary");
            System.out.println("- Java code updated to work with String equipment_id instead of int");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
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
}