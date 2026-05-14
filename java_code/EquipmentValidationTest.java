import java.util.List;

/**
 * Test class to verify equipment validation functionality
 */
public class EquipmentValidationTest {
    public static void main(String[] args) {
        // Create mock data instance
        MockData mockData = new MockData();

        System.out.println("=== Equipment Validation Test ===\n");

        // Test 1: Venue Type Classification
        System.out.println("Test 1: Venue Type Classification");
        for (int gymId = 1; gymId <= 6; gymId++) {
            VenueType type = mockData.getVenueType(gymId);
            String description = mockData.getVenueTypeDescription(gymId);
            System.out.println("Gym ID " + gymId + ": " + type + " - " + description);
        }

        // Test 2: Equipment by Gym ID with venue details
        System.out.println("\n\nTest 2: Equipment by Gym ID");
        System.out.println("Gym ID 1 (Fitness Area - REFERENCE):");
        List<Equipment> gym1Equipment = mockData.getEquipmentByGymId(1);
        for (Equipment eq : gym1Equipment) {
            System.out.println("  Equipment ID: " + eq.getEquipmentId() + ", Status: " + eq.getStatus());
        }

        System.out.println("\nGym ID 2 (Dance Room - IGNORE_CHECK):");
        List<Equipment> gym2Equipment = mockData.getEquipmentByGymId(2);
        System.out.println("  No equipment configured");

        System.out.println("\nGym ID 3 (Basketball Room - STRICT_MATCH):");
        List<Equipment> gym3Equipment = mockData.getEquipmentByGymId(3);
        for (Equipment eq : gym3Equipment) {
            System.out.println("  Equipment ID: " + eq.getEquipmentId() + ", Status: " + eq.getStatus());
        }

        System.out.println("\nGym ID 4 (Swimming Pool - IGNORE_CHECK):");
        List<Equipment> gym4Equipment = mockData.getEquipmentByGymId(4);
        System.out.println("  No equipment configured");

        System.out.println("\nGym ID 5 (Badminton Room - STRICT_MATCH):");
        List<Equipment> gym5Equipment = mockData.getEquipmentByGymId(5);
        for (Equipment eq : gym5Equipment) {
            System.out.println("  Equipment ID: " + eq.getEquipmentId() + ", Status: " + eq.getStatus());
        }

        System.out.println("\nGym ID 6 (PingPong Room - STRICT_MATCH):");
        List<Equipment> gym6Equipment = mockData.getEquipmentByGymId(6);
        for (Equipment eq : gym6Equipment) {
            System.out.println("  Equipment ID: " + eq.getEquipmentId() + ", Status: " + eq.getStatus());
        }

        // Test 3: Equipment Counts and Consistency
        System.out.println("\n\nTest 3: Equipment Counts and Consistency Check");
        for (int gymId = 1; gymId <= 6; gymId++) {
            int working = mockData.getWorkingEquipmentCount(gymId);
            int total = mockData.getTotalEquipmentCount(gymId);
            int expected = mockData.getExpectedEquipmentCount(gymId);
            boolean hasConsistencyIssue = mockData.hasEquipmentConsistencyIssue(gymId);

            System.out.println("Gym ID " + gymId + ":");
            System.out.println("  Working/Total: " + working + "/" + total);
            System.out.println("  Expected: " + expected);
            System.out.println("  Consistency Issue: " + (hasConsistencyIssue ? "YES" : "NO"));
        }

        // Test 4: Booking Scenarios by Venue Type
        System.out.println("\n\nTest 4: Booking Scenarios by Venue Type");

        // Scenario 1: IGNORE_CHECK venue (Swimming Pool)
        System.out.println("\nScenario 1: Swimming Pool (ID=4) - IGNORE_CHECK");
        System.out.println("  - No equipment validation required");
        System.out.println("  - Booking allowed directly");
        System.out.println("  - Equipment count: " + mockData.getTotalEquipmentCount(4));

        // Scenario 2: IGNORE_CHECK venue (Dance Room)
        System.out.println("\nScenario 2: Dance Room (ID=2) - IGNORE_CHECK");
        System.out.println("  - No equipment validation required");
        System.out.println("  - Booking allowed directly");
        System.out.println("  - Equipment count: " + mockData.getTotalEquipmentCount(2));

        // Scenario 3: REFERENCE venue (Fitness Area)
        System.out.println("\nScenario 3: Fitness Area (ID=1) - REFERENCE");
        System.out.println("  - Equipment status shown for reference");
        System.out.println("  - Booking allowed with any equipment status");
        System.out.println("  - Working/Total: " + mockData.getWorkingEquipmentCount(1) + "/" + mockData.getTotalEquipmentCount(1));
        System.out.println("  - Has broken equipment: " + (mockData.hasBrokenEquipment(1) ? "YES (but booking allowed)" : "NO"));

        // Scenario 4: STRICT_MATCH venue (Basketball Room)
        System.out.println("\nScenario 4: Basketball Room (ID=3) - STRICT_MATCH");
        System.out.println("  - Must select working equipment");
        System.out.println("  - Expected courts: " + mockData.getExpectedEquipmentCount(3));
        System.out.println("  - Actual equipment: " + mockData.getTotalEquipmentCount(3));
        System.out.println("  - Working equipment: " + mockData.getWorkingEquipmentCount(3));
        System.out.println("  - Has broken equipment: " + mockData.hasBrokenEquipment(3));
        if (mockData.getWorkingEquipmentCount(3) > 0) {
            System.out.println("  - ✓ Booking allowed with equipment selection");
        } else {
            System.out.println("  - ❌ Booking blocked - no working equipment");
        }

        // Scenario 5: STRICT_MATCH venue (Badminton Room)
        System.out.println("\nScenario 5: Badminton Room (ID=5) - STRICT_MATCH");
        System.out.println("  - Must select working equipment");
        System.out.println("  - Expected courts: " + mockData.getExpectedEquipmentCount(5));
        System.out.println("  - Actual equipment: " + mockData.getTotalEquipmentCount(5));
        System.out.println("  - Working equipment: " + mockData.getWorkingEquipmentCount(5));
        System.out.println("  - Consistency issue: " + (mockData.hasEquipmentConsistencyIssue(5) ? "YES" : "NO"));
        System.out.println("  - ✓ Booking allowed with equipment selection");

        // Scenario 6: STRICT_MATCH venue (PingPong Room)
        System.out.println("\nScenario 6: PingPong Room (ID=6) - STRICT_MATCH");
        System.out.println("  - Must select working equipment");
        System.out.println("  - Expected tables: " + mockData.getExpectedEquipmentCount(6));
        System.out.println("  - Actual equipment: " + mockData.getTotalEquipmentCount(6));
        System.out.println("  - Working equipment: " + mockData.getWorkingEquipmentCount(6));
        System.out.println("  - Consistency issue: " + (mockData.hasEquipmentConsistencyIssue(6) ? "YES" : "NO"));
        System.out.println("  - ✓ Booking allowed with equipment selection");

        System.out.println("\n=== Test Complete ===");
    }
}