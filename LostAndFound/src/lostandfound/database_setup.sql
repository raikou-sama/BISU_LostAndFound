-- ============================================================
--  BISU Candijay Campus — Lost & Found System
--  Database Setup Script
--  Run this in phpMyAdmin or MySQL CLI before launching the app
-- ============================================================

CREATE DATABASE IF NOT EXISTS bisu_lost_found_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bisu_lost_found_db;

-- Drop in reverse FK order so re-runs are safe
DROP TABLE IF EXISTS claims;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

-- ── USERS ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(120) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('admin','student') DEFAULT 'student',
    student_id  VARCHAR(40),
    college     VARCHAR(120),
    program     VARCHAR(80),
    year_level  TINYINT DEFAULT 0,
    section     VARCHAR(20),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── ITEMS ──────────────────────────────────────────────────────────────────────
CREATE TABLE items (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    item_name     VARCHAR(150) NOT NULL,
    description   TEXT,
    category      VARCHAR(80),
    location      VARCHAR(150),
    status        ENUM('active','claimed','completed') DEFAULT 'active',
    type          ENUM('lost','found') NOT NULL,
    contact_info  VARCHAR(150),
    reported_by   INT,
    date_reported TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ── CLAIMS ─────────────────────────────────────────────────────────────────────
CREATE TABLE claims (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    item_id       INT NOT NULL,
    claimed_by    INT NOT NULL,
    proof_details TEXT,
    status        ENUM('pending','approved','rejected','completed') DEFAULT 'pending',
    admin_notes   TEXT,
    reviewed_by   INT,
    date_claimed  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_reviewed TIMESTAMP NULL,
    FOREIGN KEY (item_id)     REFERENCES items(id)  ON DELETE CASCADE,
    FOREIGN KEY (claimed_by)  REFERENCES users(id)  ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id)  ON DELETE SET NULL
);

-- ── SEED DATA ──────────────────────────────────────────────────────────────────
-- Default admin  →  email: admin@bisu.edu.ph  |  password: admin123
INSERT INTO users (full_name, email, password, role, student_id) VALUES
('System Administrator', 'admin@bisu.edu.ph', 'admin123', 'admin', 'ADMIN-001');

-- Sample students
INSERT INTO users (full_name, email, password, role, student_id, college, program, year_level, section) VALUES
('Juan dela Cruz',  'juan@bisu.edu.ph',  'pass123', 'student', '2024-0001', 'CoS (College of Sciences)',                      'BSCS',             3, 'A'),
('Maria Santos',    'maria@bisu.edu.ph', 'pass123', 'student', '2024-0002', 'CTE (College of Teacher Education)',             'BSED Mathematics', 2, 'B'),
('Pedro Reyes',     'pedro@bisu.edu.ph', 'pass123', 'student', '2024-0003', 'CBM (College of Business & Management)',         'BSHM',             4, 'A');

-- Sample items
INSERT INTO items (item_name, description, category, location, status, type, contact_info, reported_by) VALUES
('Black Umbrella',  'Medium size, wooden handle',  'Clothing',    'Library 2nd Floor',  'active', 'lost',  '09171234567', 2),
('Blue Notebook',   'BSCS notes inside',            'Books/Notes', 'Room 204',           'active', 'found', '09351234567', 2),
('Student ID Card', 'Name: Rica Flores',            'ID/Cards',    'Near Gymnasium',     'active', 'found', '09401234567', 3),
('Car Keys',        'Toyota keychain',              'Keys',        'Parking Lot B',      'active', 'lost',  '09121234567', 4);
