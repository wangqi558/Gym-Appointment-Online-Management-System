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
    user_id VARCHAR(100) PRIMARY KEY,
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
    user_id VARCHAR(100) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Teacher (
    user_id VARCHAR(100) PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);

CREATE TABLE Admin (
    user_id VARCHAR(100) PRIMARY KEY,
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
    user_id VARCHAR(100) NOT NULL,
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
    admin_id VARCHAR(100) NOT NULL,
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
-- Insert test data for User
INSERT INTO User (user_id, name, phone_number, role) VALUES
('2530034001', 'Student 2530034001', '2530034001', 'Student'),
('2530034002', 'Student 2530034002', '2530034002', 'Student'),
('2530034003', 'Student 2530034003', '2530034003', 'Student'),
('2530034004', 'Student 2530034004', '2530034004', 'Student'),
('2530034007', 'Student 2530034007', '2530034007', 'Student'),
('2530034008', 'Student 2530034008', '2530034008', 'Student'),
('2530034009', 'Student 2530034009', '2530034009', 'Student'),
('2530034010', 'Student 2530034010', '2530034010', 'Student'),
('2530034011', 'Student 2530034011', '2530034011', 'Student'),
('2530034012', 'Student 2530034012', '2530034012', 'Student'),
('2530034013', 'Student 2530034013', '2530034013', 'Student'),
('2530034014', 'Student 2530034014', '2530034014', 'Student'),
('2530034016', 'Student 2530034016', '2530034016', 'Student'),
('2530034017', 'Student 2530034017', '2530034017', 'Student'),
('2530034018', 'Student 2530034018', '2530034018', 'Student'),
('2530034019', 'Student 2530034019', '2530034019', 'Student'),
('2530034020', 'Student 2530034020', '2530034020', 'Student'),
('2530034021', 'Student 2530034021', '2530034021', 'Student'),
('2530034022', 'Student 2530034022', '2530034022', 'Student'),
('2530034023', 'Student 2530034023', '2530034023', 'Student'),
('2530034024', 'Student 2530034024', '2530034024', 'Student'),
('2530034025', 'Student 2530034025', '2530034025', 'Student'),
('2530034026', 'Student 2530034026', '2530034026', 'Student'),
('2530034027', 'Student 2530034027', '2530034027', 'Student'),
('2530034028', 'Student 2530034028', '2530034028', 'Student'),
('2530034029', 'Student 2530034029', '2530034029', 'Student'),
('2530034030', 'Student 2530034030', '2530034030', 'Student'),

('teacher1', 'Teacher 1', '1234567893', 'Teacher'),
('teacher2', 'Teacher 2', '1234567894', 'Teacher'),
('teacher3', 'Teacher 3', '1234567897', 'Teacher'),

('admin1', 'Admin 1', '1234567895', 'Admin');

-- Insert subclass data
INSERT INTO Student (user_id) VALUES
('2530034001'),
('2530034002'),
('2530034003'),
('2530034004'),
('2530034007'),
('2530034008'),
('2530034009'),
('2530034010'),
('2530034011'),
('2530034012'),
('2530034013'),
('2530034014'),
('2530034016'),
('2530034017'),
('2530034018'),
('2530034019'),
('2530034020'),
('2530034021'),
('2530034022'),
('2530034023'),
('2530034024'),
('2530034025'),
('2530034026'),
('2530034027'),
('2530034028'),
('2530034029'),
('2530034030');

INSERT INTO Teacher (user_id) VALUES 
('teacher1'), 
('teacher2'), 
('teacher3');

INSERT INTO Admin (user_id) VALUES 
('admin1');


-- Insert Gym data
INSERT INTO Gym (gym_type, location, open_time, close_time) VALUES
('Fitness Area', 'Fitness Gym', '08:00:00', '21:00:00'),
('Dance Room', 'Dance Room', '12:00:00', '21:00:00'),
('Basketball Room', 'Basketball Court', '08:00:00', '21:00:00'),
('Swimming Pool', 'Swimming Pool', '12:00:00', '20:30:00'),
('Badminton Room', 'Badminton Court', '08:00:00', '21:00:00'),
('PingPong Room', 'PingPong Room', '08:00:00', '21:00:00');

-- Insert Gym-specific data
-- Package / full-venue rental prices are not considered.
-- Basketball only considers individual booking.
-- Since the current schema has only one price column, the price field stores the basic individual/off-peak price.
-- Busy/off-peak differences are reflected in Appointment.payment and Appointment.record.

INSERT INTO Fitness_Area (gym_id, equipment_count, price) VALUES
(1, 2, 10.00);

INSERT INTO Dance_Room (gym_id, time, price) VALUES
(2, 'Open when not reserved for teaching or school training. Off-peak price: 100 RMB/hour. Busy-period price: 150 RMB/hour. Normal open periods include 12:00-14:00, 15:00-17:00, and evening open hours.', 100.00);

INSERT INTO Basketball_Room (gym_id, court_count, price) VALUES
(3, 4, 10.00);

INSERT INTO Swimming_Pool (gym_id, price) VALUES
(4, 20.00);

INSERT INTO Badminton_Room (gym_id, court_count, time, price) VALUES
(5, 6, 'Mon-Fri 08:00-10:00 free period. Booking should be made one day in advance. Off-peak price: 10 RMB/hour/court. Busy-period price: 30 RMB/hour/court. Normal booking periods include Mon-Fri 10:00-16:00 and weekend 11:30-16:00.', 10.00);

INSERT INTO PingPong_Room (gym_id, table_count, time, price) VALUES
(6, 10, 'Mon-Fri 08:00-10:00 free period. Available during general open hours when not reserved for teaching or training. Price: 10 RMB/hour/table.', 10.00);

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
-- For unit-level booking, the specific booked unit is stored in the existing record field.
-- No new attribute is added to Appointment or any other entity set.
-- Package / full-court rental is not considered.
-- Basketball only considers individual booking.

-- Insert Appointment data WITHOUT status
-- Busy/off-peak price differences are reflected in payment and record.
-- Package / full-venue rental is not considered.
-- Basketball only considers individual booking.
-- Specific booked units are stored in the existing record field.

INSERT INTO Appointment (user_id, gym_id, start_time, end_time, payment, record) VALUES
('2530034001', 1, '2024-01-15 08:00:00', '2024-01-15 10:00:00', 10.00, 'Fitness Area individual booking; free morning open period; individual price 10 RMB per visit'),
('2530034007', 1, '2024-01-21 12:00:00', '2024-01-21 14:00:00', 10.00, 'Fitness Area individual weekend booking; price 10 RMB per visit'),

('2530034002', 2, '2024-01-16 14:00:00', '2024-01-16 16:00:00', 200.00, 'Booked Unit: dance_room1; Off-peak booking; 100 RMB/hour x 2 hours'),
('teacher3', 2, '2024-01-22 19:00:00', '2024-01-22 21:00:00', 300.00, 'Booked Unit: dance_room1; Busy-period booking; 150 RMB/hour x 2 hours'),

('2530034003', 3, '2024-01-17 08:00:00', '2024-01-17 10:00:00', 10.00, 'Basketball individual booking during free morning open period; individual price 10 RMB per visit'),
('teacher3', 3, '2024-01-22 19:00:00', '2024-01-22 21:00:00', 10.00, 'Basketball individual booking by teacher; only individual price considered; 10 RMB per visit'),

('teacher1', 4, '2024-01-18 18:00:00', '2024-01-18 20:00:00', 20.00, 'Swimming Pool individual booking by teacher; price 20 RMB per visit'),

('teacher2', 5, '2024-01-19 10:00:00', '2024-01-19 12:00:00', 20.00, 'Booked Unit: badminton_court1_equipment; Off-peak booking; 10 RMB/hour/court x 2 hours'),
('teacher1', 5, '2024-01-19 18:00:00', '2024-01-19 20:00:00', 60.00, 'Booked Unit: badminton_court2_equipment; Busy-period booking; 30 RMB/hour/court x 2 hours'),

('2530034001', 6, '2024-01-20 15:00:00', '2024-01-20 17:00:00', 20.00, 'Booked Unit: pingpong_table1_equipment; 10 RMB/hour/table x 2 hours');

-- Insert Repair data with VARCHAR equipment_id
-- Insert Repair data with VARCHAR equipment_id
INSERT INTO Repair (admin_id, equipment_id, time_point) VALUES
('admin1', 'badminton_court3_equipment', '2024-01-10 09:00:00'),
('admin1', 'pingpong_table9_equipment', '2024-01-11 14:00:00'),
('admin1', 'badminton_court3_equipment', '2024-01-12 16:00:00');

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


