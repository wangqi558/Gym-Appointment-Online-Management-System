import java.util.*;

/**
 * MockData class - stores all sample data for the application
 * Simulates database data without actual database connection
 */
public class MockData {
    // User data
    private List<User> users;
    private List<Student> students;
    private List<Teacher> teachers;
    private List<Admin> admins;

    // Gym data
    private List<Gym> gyms;
    private List<FitnessArea> fitnessAreas;
    private List<DanceRoom> danceRooms;
    private List<BasketballRoom> basketballRooms;
    private List<SwimmingPool> swimmingPools;
    private List<BadmintonRoom> badmintonRooms;
    private List<PingPongRoom> pingPongRooms;

    // Other data
    private List<Equipment> equipment;
    private List<Appointment> appointments;
    private List<Repair> repairs;

    public MockData() {
        initializeData();
    }

    private void initializeData() {
        // Initialize users
        users = new ArrayList<>();
        users.add(new User(1, "John Smith", "1234567890"));
        users.add(new User(2, "Emma Johnson", "1234567891"));
        users.add(new User(3, "Michael Brown", "1234567892"));
        users.add(new User(4, "Sarah Davis", "1234567893"));
        users.add(new User(5, "David Wilson", "1234567894"));
        users.add(new User(6, "Lisa Anderson", "1234567895"));
        users.add(new User(7, "James Taylor", "1234567896"));
        users.add(new User(8, "Maria Garcia", "1234567897"));

        // Initialize students
        students = new ArrayList<>();
        students.add(new Student(1));
        students.add(new Student(2));
        students.add(new Student(3));

        // Initialize teachers
        teachers = new ArrayList<>();
        teachers.add(new Teacher(4));
        teachers.add(new Teacher(5));

        // Initialize admins
        admins = new ArrayList<>();
        admins.add(new Admin(6));

        // Initialize gyms
        gyms = new ArrayList<>();
        gyms.add(new Gym(1, "Building A, Floor 1", "06:00", "22:00"));
        gyms.add(new Gym(2, "Building A, Floor 2", "08:00", "20:00"));
        gyms.add(new Gym(3, "Building B, Floor 1", "06:00", "22:00"));
        gyms.add(new Gym(4, "Building B, Floor 2", "07:00", "21:00"));
        gyms.add(new Gym(5, "Building C, Floor 1", "08:00", "20:00"));
        gyms.add(new Gym(6, "Building C, Floor 2", "09:00", "18:00"));

        // Initialize gym-specific details
        fitnessAreas = new ArrayList<>();
        fitnessAreas.add(new FitnessArea(1, 50, 15.00));

        danceRooms = new ArrayList<>();
        danceRooms.add(new DanceRoom(2, "Mon-Fri 10AM-6PM", 20.00));

        basketballRooms = new ArrayList<>();
        basketballRooms.add(new BasketballRoom(3, 2, 25.00));

        swimmingPools = new ArrayList<>();
        swimmingPools.add(new SwimmingPool(4, 30.00));

        badmintonRooms = new ArrayList<>();
        badmintonRooms.add(new BadmintonRoom(5, "Daily 8AM-8PM", 4, 18.00));

        pingPongRooms = new ArrayList<>();
        pingPongRooms.add(new PingPongRoom(6, "Daily 9AM-5PM", 10, 12.00));

        // Initialize equipment
        equipment = new ArrayList<>();

        // Gym 1 - Fitness Area (Reference type) - equipment status is for reference only
        equipment.add(new Equipment("treadmill_1_equipment", "Working", 1));
        equipment.add(new Equipment("dumbbell_set_1_equipment", "Working", 1));
        equipment.add(new Equipment("exercise_bike_1_equipment", "Under Maintenance", 1));
        equipment.add(new Equipment("weight_machine_1_equipment", "Working", 1));
        equipment.add(new Equipment("exercise_bike_2_equipment", "Working", 1));

        // Gym 2 - Dance Room (Ignore check type) - no equipment

        // Gym 3 - Basketball Room (Strict match type) - 2 courts = 2 court equipment
        equipment.add(new Equipment("basketball_court1_equipment", "Working", 3));
        equipment.add(new Equipment("basketball_court2_equipment", "Working", 3));

        // Gym 4 - Swimming Pool (Ignore check type) - no equipment

        // Gym 5 - Badminton Room (Strict match type) - 4 courts = 4 court equipment
        equipment.add(new Equipment("badminton_court1_equipment", "Working", 5));
        equipment.add(new Equipment("badminton_court2_equipment", "Working", 5));
        equipment.add(new Equipment("badminton_court3_equipment", "Under Maintenance", 5));
        equipment.add(new Equipment("badminton_court4_equipment", "Working", 5));

        // Gym 6 - PingPong Room (Strict match type) - 10 tables = 10 table equipment
        equipment.add(new Equipment("pingpong_table1_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table2_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table3_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table4_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table5_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table6_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table7_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table8_equipment", "Working", 6));
        equipment.add(new Equipment("pingpong_table9_equipment", "Broken", 6));
        equipment.add(new Equipment("pingpong_table10_equipment", "Working", 6));

        // Initialize appointments
        appointments = new ArrayList<>();
        appointments.add(new Appointment(1, 1, 1, "2024-01-15 10:00", "2024-01-15 12:00", 30.00, "Fitness booking by student with treadmill available"));
        appointments.add(new Appointment(2, 2, 2, "2024-01-16 14:00", "2024-01-16 16:00", 40.00, "Dance room booking - no equipment required"));
        appointments.add(new Appointment(3, 3, 3, "2024-01-17 16:00", "2024-01-17 18:00", 50.00, "Basketball booking - courts available"));
        appointments.add(new Appointment(4, 4, 4, "2024-01-18 18:00", "2024-01-18 20:00", 60.00, "Swimming booking - no equipment required"));
        appointments.add(new Appointment(5, 5, 5, "2024-01-19 18:00", "2024-01-19 20:00", 36.00, "Badminton booking - courts available"));
        appointments.add(new Appointment(6, 1, 6, "2024-01-20 15:00", "2024-01-20 17:00", 24.00, "PingPong booking - tables available"));
        appointments.add(new Appointment(7, 7, 1, "2024-01-21 09:00", "2024-01-21 11:00", 30.00, "Fitness booking by student with dumbbells available"));
        appointments.add(new Appointment(8, 8, 3, "2024-01-22 19:00", "2024-01-22 21:00", 50.00, "Basketball booking by teacher - courts available"));

        // Initialize repairs
        repairs = new ArrayList<>();
        repairs.add(new Repair(1, 6, "badminton_court3_equipment", "2024-01-10 09:00"));
        repairs.add(new Repair(2, 6, "pingpong_table9_equipment", "2024-01-11 14:00"));
        repairs.add(new Repair(3, 6, "badminton_court3_equipment", "2024-01-12 16:00"));
    }

    // Getters for all data
    public List<User> getUsers() { return users; }
    public List<Student> getStudents() { return students; }
    public List<Teacher> getTeachers() { return teachers; }
    public List<Admin> getAdmins() { return admins; }
    public List<Gym> getGyms() { return gyms; }
    public List<FitnessArea> getFitnessAreas() { return fitnessAreas; }
    public List<DanceRoom> getDanceRooms() { return danceRooms; }
    public List<BasketballRoom> getBasketballRooms() { return basketballRooms; }
    public List<SwimmingPool> getSwimmingPools() { return swimmingPools; }
    public List<BadmintonRoom> getBadmintonRooms() { return badmintonRooms; }
    public List<PingPongRoom> getPingPongRooms() { return pingPongRooms; }
    public List<Equipment> getEquipment() { return equipment; }
    public List<Equipment> getEquipmentByStatus(String status) {
        List<Equipment> result = new ArrayList<>();
        for (Equipment e : equipment) {
            if (e.getStatus().equals(status)) {
                result.add(e);
            }
        }
        return result;
    }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Repair> getRepairs() { return repairs; }

    // Get user by ID
    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    // Get gym by ID
    public Gym getGymById(int gymId) {
        for (Gym gym : gyms) {
            if (gym.getGymId() == gymId) {
                return gym;
            }
        }
        return null;
    }

    // Get equipment by ID
    public Equipment getEquipmentById(String equipmentId) {
        for (Equipment eq : equipment) {
            if (eq.getEquipmentId().equals(equipmentId)) {
                return eq;
            }
        }
        return null;
    }

    // Add appointment
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    // Cancel appointment
    public void cancelAppointment(int appointmentId) {
        appointments.removeIf(a -> a.getAppointmentId() == appointmentId);
    }

    // Add repair
    public void addRepair(Repair repair) {
        repairs.add(repair);
    }

    // Update equipment status
    public void updateEquipmentStatus(String equipmentId, String status) {
        for (Equipment eq : equipment) {
            if (eq.getEquipmentId().equals(equipmentId)) {
                eq.setStatus(status);
                break;
            }
        }
    }

    // Get equipment by gym ID
    public List<Equipment> getEquipmentByGymId(int gymId) {
        List<Equipment> gymEquipment = new ArrayList<>();
        for (Equipment eq : equipment) {
            if (eq.getGymId() == gymId) {
                gymEquipment.add(eq);
            }
        }
        return gymEquipment;
    }

    // Check if gym has broken equipment
    public boolean hasBrokenEquipment(int gymId) {
        for (Equipment eq : equipment) {
            if (eq.getGymId() == gymId && eq.getStatus().equals("Broken")) {
                return true;
            }
        }
        return false;
    }

    // Get working equipment count for a gym
    public int getWorkingEquipmentCount(int gymId) {
        int count = 0;
        for (Equipment eq : equipment) {
            if (eq.getGymId() == gymId && eq.getStatus().equals("Working")) {
                count++;
            }
        }
        return count;
    }

    // Get total equipment count for a gym
    public int getTotalEquipmentCount(int gymId) {
        int count = 0;
        for (Equipment eq : equipment) {
            if (eq.getGymId() == gymId) {
                count++;
            }
        }
        return count;
    }

    // Get venue type classification
    public VenueType getVenueType(int gymId) {
        // Strict match type: Basketball, Badminton, PingPong
        for (BasketballRoom br : basketballRooms) {
            if (br.getGymId() == gymId) return VenueType.STRICT_MATCH;
        }
        for (BadmintonRoom br : badmintonRooms) {
            if (br.getGymId() == gymId) return VenueType.STRICT_MATCH;
        }
        for (PingPongRoom ppr : pingPongRooms) {
            if (ppr.getGymId() == gymId) return VenueType.STRICT_MATCH;
        }

        // Ignore check type: Swimming Pool, Dance Room
        for (SwimmingPool sp : swimmingPools) {
            if (sp.getGymId() == gymId) return VenueType.IGNORE_CHECK;
        }
        for (DanceRoom dr : danceRooms) {
            if (dr.getGymId() == gymId) return VenueType.IGNORE_CHECK;
        }

        // Reference type: Fitness Area
        for (FitnessArea fa : fitnessAreas) {
            if (fa.getGymId() == gymId) return VenueType.REFERENCE;
        }

        return VenueType.IGNORE_CHECK; // Default
    }

    // Get expected equipment count for venue
    public int getExpectedEquipmentCount(int gymId) {
        for (BasketballRoom br : basketballRooms) {
            if (br.getGymId() == gymId) return br.getCourtCount();
        }
        for (BadmintonRoom br : badmintonRooms) {
            if (br.getGymId() == gymId) return br.getCourtCount();
        }
        for (PingPongRoom ppr : pingPongRooms) {
            if (ppr.getGymId() == gymId) return ppr.getTableCount();
        }
        return 0; // No equipment expected for other types
    }

    // Check if venue has equipment consistency issues
    public boolean hasEquipmentConsistencyIssue(int gymId) {
        VenueType type = getVenueType(gymId);
        if (type != VenueType.STRICT_MATCH) {
            return false; // Only strict match venues need consistency check
        }

        int expected = getExpectedEquipmentCount(gymId);
        int actual = getTotalEquipmentCount(gymId);
        return expected != actual;
    }

    // Get venue type description
    public String getVenueTypeDescription(int gymId) {
        VenueType type = getVenueType(gymId);
        switch (type) {
            case STRICT_MATCH: return "Strict Match - Equipment must match venue capacity";
            case IGNORE_CHECK: return "Ignore Check - No equipment validation";
            case REFERENCE: return "Reference - Equipment status for reference only";
            default: return "Unknown";
        }
    }
}

// Model classes
class User {
    private int userId;
    private String name;
    private String phoneNumber;

    public User(int userId, String name, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
}

class Student extends User {
    public Student(int userId) {
        super(userId, "", "");
    }
}

class Teacher extends User {
    public Teacher(int userId) {
        super(userId, "", "");
    }
}

class Admin extends User {
    public Admin(int userId) {
        super(userId, "", "");
    }
}

class Gym {
    private int gymId;
    private String location;
    private String openTime;
    private String closeTime;

    public Gym(int gymId, String location, String openTime, String closeTime) {
        this.gymId = gymId;
        this.location = location;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getGymId() { return gymId; }
    public String getLocation() { return location; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }
}

class FitnessArea extends Gym {
    private int equipmentCount;
    private double price;

    public FitnessArea(int gymId, int equipmentCount, double price) {
        super(gymId, "", "", "");
        this.equipmentCount = equipmentCount;
        this.price = price;
    }

    public int getEquipmentCount() { return equipmentCount; }
    public double getPrice() { return price; }
}

class DanceRoom extends Gym {
    private String time;
    private double price;

    public DanceRoom(int gymId, String time, double price) {
        super(gymId, "", "", "");
        this.time = time;
        this.price = price;
    }

    public String getTime() { return time; }
    public double getPrice() { return price; }
}

class BasketballRoom extends Gym {
    private int courtCount;
    private double price;

    public BasketballRoom(int gymId, int courtCount, double price) {
        super(gymId, "", "", "");
        this.courtCount = courtCount;
        this.price = price;
    }

    public int getCourtCount() { return courtCount; }
    public double getPrice() { return price; }
}

class SwimmingPool extends Gym {
    private double price;

    public SwimmingPool(int gymId, double price) {
        super(gymId, "", "", "");
        this.price = price;
    }

    public double getPrice() { return price; }
}

class BadmintonRoom extends Gym {
    private String time;
    private int courtCount;
    private double price;

    public BadmintonRoom(int gymId, String time, int courtCount, double price) {
        super(gymId, "", "", "");
        this.time = time;
        this.courtCount = courtCount;
        this.price = price;
    }

    public String getTime() { return time; }
    public int getCourtCount() { return courtCount; }
    public double getPrice() { return price; }
}

class PingPongRoom extends Gym {
    private String time;
    private int tableCount;
    private double price;

    public PingPongRoom(int gymId, String time, int tableCount, double price) {
        super(gymId, "", "", "");
        this.time = time;
        this.tableCount = tableCount;
        this.price = price;
    }

    public String getTime() { return time; }
    public int getTableCount() { return tableCount; }
    public double getPrice() { return price; }
}

class Equipment {
    private String equipmentId;
    private String status;
    private int gymId;

    public Equipment(String equipmentId, String status, int gymId) {
        this.equipmentId = equipmentId;
        this.status = status;
        this.gymId = gymId;
    }

    public String getEquipmentId() { return equipmentId; }
    public String getStatus() { return status; }
    public int getGymId() { return gymId; }
    public void setStatus(String status) { this.status = status; }
}

class Appointment {
    private int appointmentId;
    private int userId;
    private int gymId;
    private String startTime;
    private String endTime;
    private double payment;
    private String record;

    public Appointment(int appointmentId, int userId, int gymId, String startTime, String endTime, double payment, String record) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.gymId = gymId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.payment = payment;
        this.record = record;
    }

    public int getAppointmentId() { return appointmentId; }
    public int getUserId() { return userId; }
    public int getGymId() { return gymId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public double getPayment() { return payment; }
    public String getRecord() { return record; }
}

class Repair {
    private int repairId;
    private int adminId;
    private String equipmentId;
    private String timePoint;

    public Repair(int repairId, int adminId, String equipmentId, String timePoint) {
        this.repairId = repairId;
        this.adminId = adminId;
        this.equipmentId = equipmentId;
        this.timePoint = timePoint;
    }

    public int getRepairId() { return repairId; }
    public int getAdminId() { return adminId; }
    public String getEquipmentId() { return equipmentId; }
    public String getTimePoint() { return timePoint; }
}

// Venue type classification enum
enum VenueType {
    STRICT_MATCH,    // Must have equipment matching venue capacity (Basketball, Badminton, PingPong)
    IGNORE_CHECK,    // No equipment validation needed (Swimming Pool, Dance Room)
    REFERENCE        // Equipment status for reference only (Fitness Area)
}