# Gym Appointment and Facility Management System

A Java Swing-based desktop application for managing gym appointments and facilities.

## Features

- **User Authentication**: Login system with role-based access (Student, Teacher, Admin)
- **Dashboard**: Overview with statistics cards showing total gyms, appointments, equipment, and repairs
- **Facility Booking**: View and book different gym facilities (Fitness Area, Dance Room, Basketball Room, Swimming Pool, Badminton Room, PingPong Room)
- **Appointment Management**: View, add, and cancel appointments
- **Equipment Management**: View equipment status and request repairs
- **Repair Management**: Admin-only feature to manage repair records

## Technology Stack

- Java 8 or higher
- Java Swing for GUI
- No database connection (uses mock data)

## How to Run

1. Compile all Java files:
   ```bash
   javac *.java
   ```

2. Run the application:
   ```bash
   java Main
   ```

## Mock Login Credentials

The system uses mock authentication. You can enter any User ID and select a role:
- User ID: Any number (e.g., 1, 2, 3, etc.)
- Role: Student, Teacher, or Admin

## File Structure

- `Main.java` - Entry point of the application
- `LoginFrame.java` - Login interface
- `DashboardFrame.java` - Main dashboard with navigation
- `FacilityPanel.java` - Gym facility booking interface
- `AppointmentPanel.java` - Appointment management interface
- `EquipmentPanel.java` - Equipment management interface
- `RepairPanel.java` - Repair records interface (Admin only)
- `UserSession.java` - Session management class
- `MockData.java` - Contains all sample data

## UI Design

- Modern, clean interface with gradient backgrounds
- Card-based layout for facilities
- Color-coded status indicators
- Responsive design for 1100x700 window size

## Notes

- This is a frontend-only application with mock data
- No database connection is implemented
- All data is stored in memory using the MockData class
- The application is designed for demonstration purposes in a university project