-- Group 2: Gym Appointment and Facility Management System
-- UPDATED MySQL Database Script
-- Appointment table WITHOUT status, according to ER diagram

CREATE DATABASE IF NOT EXISTS group2_gym_system;
USE group2_gym_system;

-- Drop old view first
DROP VIEW IF EXISTS AppointmentInfo;

-- Drop old tables
DROP TABLE IF EXISTS Repair;
DROP TABLE IF EXISTS Appointment;
DROP TABLE IF EXISTS PingPong_Room;
DROP TABLE IF EXISTS Badminton_Room;
DROP TABLE IF EXISTS Swimming_Pool;
DROP TABLE IF EXISTS Basketball_Room;
DROP TABLE IF EXISTS Dance_Room;
DROP TABLE IF EXISTS Fitness_Area;
DROP TABLE IF EXISTS Equipment;
DROP TABLE IF EXISTS Admin;
DROP TABLE IF EXISTS Teacher;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Gym;
DROP TABLE IF EXISTS User;

-- User table
CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    role ENUM('Student', 'Teacher', 'Admin') NOT NULL
);

-- Gym table
CREATE TABLE Gym (
    gym_id INT PRIMARY KEY AUTO_INCREMENT,
    gym_type ENUM(
        'Fitness Area',
        'Dance Room',
        'Basketball Room',
        'Swimming Pool',
        'Badminton Room',
        'PingPong Room'
    ) NOT NULL,
    location VARCHAR(200) NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    CHECK (close_time > open_time)
);

-- User subclasses
CREATE TABLE Student (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Teacher (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Admin (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

-- Equipment table with VARCHAR equipment_id and NO equipment_name
CREATE TABLE Equipment (
    equipment_id VARCHAR(100) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    gym_id INT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

-- Appointment table WITHOUT status
-- IMPORTANT: no new attribute is added for unit-level booking.
-- For Badminton/PingPong/Dance schedule-grid booking, the specific booked unit is stored in the existing record field,
-- using the format: 'Booked Unit: <unit_id>'.
CREATE TABLE Appointment (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    gym_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    payment DECIMAL(10,2) DEFAULT 0,
    record TEXT,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE,
    CHECK (end_time > start_time),
    CHECK (payment >= 0)
);

-- Repair table with VARCHAR equipment_id
CREATE TABLE Repair (
    repair_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    equipment_id VARCHAR(100) NOT NULL,
    time_point DATETIME NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES Admin(user_id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id) ON DELETE CASCADE
);

-- Gym-specific tables with price information
CREATE TABLE Fitness_Area (
    gym_id INT PRIMARY KEY,
    equipment_count INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

CREATE TABLE Dance_Room (
    gym_id INT PRIMARY KEY,
    time VARCHAR(300),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

CREATE TABLE Basketball_Room (
    gym_id INT PRIMARY KEY,
    court_count INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

CREATE TABLE Swimming_Pool (
    gym_id INT PRIMARY KEY,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

CREATE TABLE Badminton_Room (
    gym_id INT PRIMARY KEY,
    court_count INT NOT NULL,
    time VARCHAR(300),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

CREATE TABLE PingPong_Room (
    gym_id INT PRIMARY KEY,
    table_count INT NOT NULL,
    time VARCHAR(300),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);

-- Insert test data for User
INSERT INTO User (name, phone_number, role) VALUES
('John Smith', '1234567890', 'Student'),
('Emma Johnson', '1234567891', 'Student'),
('Michael Brown', '1234567892', 'Student'),
('Sarah Davis', '1234567893', 'Teacher'),
('David Wilson', '1234567894', 'Teacher'),
('Lisa Anderson', '1234567895', 'Admin'),
('James Taylor', '1234567896', 'Student'),
('Maria Garcia', '1234567897', 'Teacher');

-- Insert subclass data
INSERT INTO Student (user_id) VALUES 
(1), 
(2), 
(3), 
(7);

INSERT INTO Teacher (user_id) VALUES 
(4), 
(5), 
(8);

INSERT INTO Admin (user_id) VALUES 
(6);

-- Insert Gym data
INSERT INTO Gym (gym_type, location, open_time, close_time) VALUES
('Fitness Area', 'Fitness Gym', '08:00:00', '21:00:00'),
('Dance Room', 'Dance Room', '12:00:00', '21:00:00'),
('Basketball Room', 'Basketball Court', '08:00:00', '21:00:00'),
('Swimming Pool', 'Swimming Pool', '12:00:00', '20:30:00'),
('Badminton Room', 'Badminton Court', '08:00:00', '21:00:00'),
('PingPong Room', 'PingPong Room', '08:00:00', '21:00:00');

-- Insert Gym-specific data
INSERT INTO Fitness_Area (gym_id, equipment_count, price) VALUES
(1, 2, 10.00);

INSERT INTO Dance_Room (gym_id, time, price) VALUES
(2, 'Weekdays and weekends: available during general open hours. Teaching/training time follows school arrangement.', 100.00);

INSERT INTO Basketball_Room (gym_id, court_count, price) VALUES
(3, 4, 10.00);

INSERT INTO Swimming_Pool (gym_id, price) VALUES
(4, 20.00);

INSERT INTO Badminton_Room (gym_id, court_count, time, price) VALUES
(5, 6, 'Mon-Fri 08:00-10:00 free; booking should be made one day in advance; Mon-Fri 10:00-16:00; weekend 11:30-16:00.', 15.00);

INSERT INTO PingPong_Room (gym_id, table_count, time, price) VALUES
(6, 10, 'Mon-Fri 08:00-10:00 free; available during general open hours.', 10.00);

-- Insert Equipment data with VARCHAR equipment_id and NO equipment_name
INSERT INTO Equipment (equipment_id, status, gym_id) VALUES
('treadmill_1_equipment', 'Working', 1),
('dumbbell_set_1_equipment', 'Working', 1),
('basketball_court1_equipment', 'Working', 3),
('basketball_court2_equipment', 'Working', 3),
('basketball_court3_equipment', 'Broken', 3),
('basketball_court4_equipment', 'Working', 3),
('badminton_court1_equipment', 'Working', 5),
('badminton_court2_equipment', 'Working', 5),
('badminton_court3_equipment', 'Under Maintenance', 5),
('badminton_court4_equipment', 'Working', 5),
('badminton_court5_equipment', 'Working', 5),
('badminton_court6_equipment', 'Under Maintenance', 5),
('pingpong_table1_equipment', 'Working', 6),
('pingpong_table2_equipment', 'Working', 6),
('pingpong_table3_equipment', 'Working', 6),
('pingpong_table4_equipment', 'Working', 6),
('pingpong_table5_equipment', 'Working', 6),
('pingpong_table6_equipment', 'Working', 6),
('pingpong_table7_equipment', 'Working', 6),
('pingpong_table8_equipment', 'Working', 6),
('pingpong_table9_equipment', 'Broken', 6),
('pingpong_table10_equipment', 'Working', 6);

-- Insert Appointment data WITHOUT status
-- For schedule-grid booking, specific units are stored in the existing record field.
-- No new attribute is added to Appointment or any other entity set.
INSERT INTO Appointment (user_id, gym_id, start_time, end_time, payment, record) VALUES
(1, 1, '2024-01-15 10:00:00', '2024-01-15 12:00:00', 20.00, 'Fitness booking by student'),
(7, 1, '2024-01-21 09:00:00', '2024-01-21 11:00:00', 20.00, 'Fitness booking by student'),
(2, 2, '2024-01-16 14:00:00', '2024-01-16 16:00:00', 200.00, 'Booked Unit: dance_room1'),
(3, 3, '2024-01-17 16:00:00', '2024-01-17 18:00:00', 20.00, 'Basketball booking'),
(8, 3, '2024-01-22 19:00:00', '2024-01-22 21:00:00', 20.00, 'Basketball booking by teacher'),
(4, 4, '2024-01-18 18:00:00', '2024-01-18 20:00:00', 40.00, 'Swimming booking by teacher'),
(5, 5, '2024-01-19 18:00:00', '2024-01-19 20:00:00', 30.00, 'Booked Unit: badminton_court1_equipment'),
(1, 6, '2024-01-20 15:00:00', '2024-01-20 17:00:00', 20.00, 'Booked Unit: pingpong_table1_equipment');

-- Insert Repair data with VARCHAR equipment_id
-- Insert Repair data with VARCHAR equipment_id
INSERT INTO Repair (admin_id, equipment_id, time_point) VALUES
(6, 'badminton_court3_equipment', '2024-01-10 09:00:00'),
(6, 'pingpong_table9_equipment', '2024-01-11 14:00:00'),
(6, 'badminton_court3_equipment', '2024-01-12 16:00:00');

-- Create triggers
DELIMITER //

CREATE TRIGGER check_user_role_student
BEFORE INSERT ON Student
FOR EACH ROW
BEGIN
    IF (SELECT role FROM User WHERE user_id = NEW.user_id) != 'Student' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This user is not a Student';
    END IF;
END//

CREATE TRIGGER check_user_role_teacher
BEFORE INSERT ON Teacher
FOR EACH ROW
BEGIN
    IF (SELECT role FROM User WHERE user_id = NEW.user_id) != 'Teacher' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This user is not a Teacher';
    END IF;
END//

CREATE TRIGGER check_user_role_admin
BEFORE INSERT ON Admin
FOR EACH ROW
BEGIN
    IF (SELECT role FROM User WHERE user_id = NEW.user_id) != 'Admin' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This user is not an Admin';
    END IF;
END//

CREATE TRIGGER prevent_admin_appointment
BEFORE INSERT ON Appointment
FOR EACH ROW
BEGIN
    IF (SELECT role FROM User WHERE user_id = NEW.user_id) = 'Admin' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Admin cannot make appointments';
    END IF;
END//

CREATE TRIGGER limit_single_appointment_time
BEFORE INSERT ON Appointment
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(MINUTE, NEW.start_time, NEW.end_time) > 120 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Each appointment can only be up to 2 hours';
    END IF;
END//

CREATE TRIGGER limit_single_appointment_time_update
BEFORE UPDATE ON Appointment
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(MINUTE, NEW.start_time, NEW.end_time) > 120 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Each appointment can only be up to 2 hours';
    END IF;
END//

CREATE TRIGGER check_fitness_area_type
BEFORE INSERT ON Fitness_Area
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'Fitness Area' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a Fitness Area';
    END IF;
END//

CREATE TRIGGER check_dance_room_type
BEFORE INSERT ON Dance_Room
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'Dance Room' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a Dance Room';
    END IF;
END//

CREATE TRIGGER check_basketball_room_type
BEFORE INSERT ON Basketball_Room
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'Basketball Room' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a Basketball Room';
    END IF;
END//

CREATE TRIGGER check_swimming_pool_type
BEFORE INSERT ON Swimming_Pool
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'Swimming Pool' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a Swimming Pool';
    END IF;
END//

CREATE TRIGGER check_badminton_room_type
BEFORE INSERT ON Badminton_Room
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'Badminton Room' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a Badminton Room';
    END IF;
END//

CREATE TRIGGER check_pingpong_room_type
BEFORE INSERT ON PingPong_Room
FOR EACH ROW
BEGIN
    IF (SELECT gym_type FROM Gym WHERE gym_id = NEW.gym_id) != 'PingPong Room' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This gym is not a PingPong Room';
    END IF;
END//

DELIMITER ;

-- Create AppointmentInfo View
-- appointment_count_for_gym is a derived attribute corresponding to the dotted count in ER diagram
CREATE VIEW AppointmentInfo AS
WITH EquipmentSummary AS (
    SELECT
        gym_id,
        COUNT(*) AS total_equipment,
        SUM(CASE WHEN status = 'Working' THEN 1 ELSE 0 END) AS working_equipment,
        SUM(CASE WHEN status = 'Broken' THEN 1 ELSE 0 END) AS broken_equipment,
        SUM(CASE WHEN status = 'Under Maintenance' THEN 1 ELSE 0 END) AS maintenance_equipment
    FROM Equipment
    GROUP BY gym_id
),
AppointmentCount AS (
    SELECT
        gym_id,
        COUNT(*) AS appointment_count_for_gym
    FROM Appointment
    GROUP BY gym_id
)
SELECT
    a.appointment_id,
    u.name AS user_name,
    u.role,
    u.phone_number,
    g.gym_type,
    g.location,
    a.start_time,
    a.end_time,
    TIMESTAMPDIFF(HOUR, a.start_time, a.end_time) AS duration_hours,
    a.payment,
    a.record,
    COALESCE(ac.appointment_count_for_gym, 0) AS appointment_count_for_gym,
    COALESCE(es.total_equipment, 0) AS total_equipment,
    COALESCE(es.working_equipment, 0) AS working_equipment,
    COALESCE(es.broken_equipment, 0) AS broken_equipment,
    COALESCE(es.maintenance_equipment, 0) AS maintenance_equipment
FROM Appointment a
JOIN User u ON a.user_id = u.user_id
JOIN Gym g ON a.gym_id = g.gym_id
LEFT JOIN EquipmentSummary es ON g.gym_id = es.gym_id
LEFT JOIN AppointmentCount ac ON g.gym_id = ac.gym_id;

-- Create indexes for better performance
CREATE INDEX idx_appointment_user_id ON Appointment(user_id);
CREATE INDEX idx_appointment_gym_id ON Appointment(gym_id);
CREATE INDEX idx_appointment_start_time ON Appointment(start_time);
-- Composite index for schedule-grid booking checks by gym and time range
CREATE INDEX idx_appointment_gym_time ON Appointment(gym_id, start_time, end_time);
-- Prefix index for checking unit bookings stored in the existing record field
CREATE INDEX idx_appointment_record_prefix ON Appointment(record(100));
CREATE INDEX idx_equipment_gym_id ON Equipment(gym_id);
CREATE INDEX idx_equipment_status ON Equipment(status);

-- Final verification queries
SELECT 'Database setup complete!' AS message;
SELECT 'Appointment table now has no status column according to ER diagram' AS appointment_change_summary;
SELECT 'Unit-level bookings use existing Appointment.record field; no entity attribute was added' AS unit_booking_summary;
SELECT 'Equipment table uses VARCHAR equipment_id and has no equipment_name' AS equipment_change_summary;
SELECT 'Repair table uses VARCHAR equipment_id' AS repair_summary;
SELECT 'AppointmentInfo view updated without appointment status' AS view_summary;

-- Display table structures
SHOW TABLES;

-- Display view structure
DESCRIBE AppointmentInfo;

-- Display sample data
SELECT 'Sample Appointment data:' AS info;
SELECT * FROM Appointment LIMIT 3;

SELECT 'Sample Equipment data:' AS info;
SELECT equipment_id, status, gym_id FROM Equipment LIMIT 5;

SELECT 'Sample Equipment summary:' AS info;
SELECT * FROM AppointmentInfo LIMIT 3;