# Gym Appointment and Facility Management System

A Java Swing desktop application for managing gym facility booking, appointment records, equipment status, and repair records. The project is designed for a database course assignment and uses a MySQL dataset for the gym management system.

## Project Overview

This system supports three user roles:

- **Student**: book gym facilities, view personal appointments, cancel appointments, and request equipment repairs.
- **Teacher**: same main booking and appointment functions as students.
- **Admin**: view all appointments and manage repair records.

The application contains a graphical interface built with Java Swing and a MySQL database script named `数据集_updated.sql`.

## Main Features

### 1. Login System

Users log in with a user ID and selected role.

The sample users are loaded from the project data. The role must match the selected user ID.

Example login accounts:

| User ID | Name | Role |
|---:|---|---|
| 1 | John Smith | Student |
| 2 | Emma Johnson | Student |
| 3 | Michael Brown | Student |
| 4 | Sarah Davis | Teacher |
| 5 | David Wilson | Teacher |
| 6 | Lisa Anderson | Admin |
| 7 | James Taylor | Student |
| 8 | Maria Garcia | Teacher |

### 2. Dashboard

The dashboard displays system statistics, including:

- total gyms
- registered users
- total appointments
- working equipment / total equipment
- equipment issues
- repair records

### 3. Facility Booking

Users can view and book different gym facilities:

- Fitness Area
- Dance Room
- Basketball Room
- Swimming Pool
- Badminton Room
- PingPong Room

The booking logic includes time selection, price calculation, equipment availability checks, and unit-level booking for facilities such as badminton courts and ping-pong tables.

### 4. Appointment Management

Users can:

- view their own appointments
- add appointments
- cancel appointments

Admins can view all appointment records.

### 5. Equipment Management

The equipment page shows equipment information, including:

- equipment ID
- status
- gym location
- last updated time

Equipment status is color-coded:

| Status | Meaning |
|---|---|
| Working | Equipment can be used |
| Under Maintenance | Equipment is being repaired |
| Broken | Equipment is not available |

### 6. Repair Management

Admins can view and add repair records. Repair records connect an admin, an equipment item, and a repair time.

## Technology Stack

| Part | Technology |
|---|---|
| Language | Java |
| GUI | Java Swing |
| Database | MySQL |
| JDBC Driver | MySQL Connector/J 9.7.0 |
| Main Entry File | `Main.java` |

## Project Structure

```text
code/
├── README.md
├── 数据集_updated.sql
├── gym_database_dataset_documentation.md
├── mysql-connector-j-9.7.0.jar
|——test/
|        ├── DatabaseDesignTest.java
|        ├── NoStatusTest.java
|        ├── SimpleMain.java
|        └── TestDBConnection.java
└── java/
    ├── Main.java
    ├── LoginFrame.java
    ├── DashboardFrame.java
    ├── FacilityPanel.java
    ├── AppointmentPanel.java
    ├── EquipmentPanel.java
    ├── RepairPanel.java
    ├── MockData.java
    ├── DBConnection.java
    ├── DatabaseAdapter.java
    ├── TimeSlotUtils.java
    ├── EquipmentValidationTest.java
 
```

## Important Java Files

| File | Purpose |
|---|---|
| `Main.java` | Program entry point. Starts the login window. |
| `LoginFrame.java` | Login interface and user-role validation. |
| `DashboardFrame.java` | Main application window and side navigation. |
| `FacilityPanel.java` | Facility display and booking logic. |
| `AppointmentPanel.java` | Appointment table, appointment creation, and cancellation. |
| `EquipmentPanel.java` | Equipment list and repair request logic. |
| `RepairPanel.java` | Admin repair record management. |
| `MockData.java` | In-memory sample data and model classes. |
| `DBConnection.java` | MySQL database connection manager. |
| `TimeSlotUtils.java` | Time slot generation, time validation, and price calculation. |

## Database Design

The database script is `数据集_updated.sql`.

It creates the database:

```sql
CREATE DATABASE IF NOT EXISTS group2_gym_system;
```

Main tables include:

| Table | Purpose |
|---|---|
| `User` | Stores user ID, name, phone number, and role. |
| `Student` | Student subclass table. |
| `Teacher` | Teacher subclass table. |
| `Admin` | Admin subclass table. |
| `Gym` | Stores gym type, location, and opening hours. |
| `Equipment` | Stores equipment status and related gym. |
| `Appointment` | Stores booking records. |
| `Repair` | Stores equipment repair records. |
| `Fitness_Area` | Stores fitness area details. |
| `Dance_Room` | Stores dance room details. |
| `Basketball_Room` | Stores basketball room details. |
| `Swimming_Pool` | Stores swimming pool details. |
| `Badminton_Room` | Stores badminton room details. |
| `PingPong_Room` | Stores ping-pong room details. |

The SQL file also creates:

- role-checking triggers for `Student`, `Teacher`, and `Admin`
- appointment validation triggers
- gym-type validation triggers
- the `AppointmentInfo` view
- indexes for appointment and equipment queries

## How to Set Up the Database
find DBConnection.java
and then find :
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/group2_gym_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "you mysql password here"; // Change this to your MySQL password
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
```

 

## How to Compile and Run

Go to the `java` folder:

```bash
cd Gym Appointment Online Management System/java_code
```

Compile the Java files:

```bash
javac -cp ".:../mysql-connector-j-9.7.0.jar" *.java
```

Run the application:

```bash
java -cp ".:../mysql-connector-j-9.7.0.jar" Main
```

On Windows, use `;` instead of `:`:

```bash
javac -cp ".;../mysql-connector-j-9.7.0.jar" *.java
java -cp ".;../mysql-connector-j-9.7.0.jar" Main
```

## Example Usage

1. Start the application.
2. Log in with a valid user ID and matching role.
3. Use the side menu to open:
   - Dashboard
   - Book Facilities
   - My Appointments
   - Equipment
   - Repair Records
4. Book a facility by selecting a gym, date, start time, and end time.
5. View the booking in the appointment table.
6. Cancel an appointment if needed.
7. Log in as Admin to view repair records.

## Booking Rules

The system follows these main booking rules:

- One appointment cannot be longer than 2 hours.
- A user cannot book unavailable or broken equipment.
- Unit-level booking uses the existing `Appointment.record` field.
- Admin users cannot make appointments.
- Some facilities use schedule-grid booking for different courts or tables.

## Notes for Running the Project

- The project includes both real database logic and mock data logic.
- `MockData.java` is used for login validation and in-memory sample objects.
- Several panels also query the MySQL database directly through `DBConnection`.
- For the best result, run the SQL script first and make sure MySQL is running.
- The MySQL Connector/J JAR file is already included in the project folder.

## Known Issue

In the current source code, `RepairPanel.java` inserts a `notes` field into the `Repair` table:

```sql
INSERT INTO Repair (repair_id, admin_id, equipment_id, time_point, notes)
```

However, the current SQL file defines `Repair` without a `notes` column. To avoid a database error when adding a repair record, either:

1. add a `notes` column to the `Repair` table, or
2. remove `notes` from the insert statement in `RepairPanel.java`.

## Suggested GitHub Repository Description

```text
A Java Swing and MySQL gym appointment management system with facility booking, appointment management, equipment tracking, and repair records.
```

## Author / Group

Group 2: Gym Appointment and Facility Management System
