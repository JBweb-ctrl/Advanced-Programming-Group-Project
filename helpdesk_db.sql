-- Help Desk & Ticketing System database (run this whole file in phpMyAdmin's SQL tab or MySQL Workbench)
-- it builds the 4 tables from scratch, so any old helpdesk_db tables and their rows are removed first

CREATE DATABASE IF NOT EXISTS helpdesk_db;

USE helpdesk_db;

-- remove old tables (comment first, because it points at ticket, and ticket points at user and technician)
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `ticket`;
DROP TABLE IF EXISTS `technician`;
DROP TABLE IF EXISTS `user`;

-- students and staff (and the administrator)
CREATE TABLE `user` (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'Student/Staff',
  name VARCHAR(50) NULL,
  email VARCHAR(50) NULL,
  phone_number VARCHAR(20) NULL
) ENGINE=InnoDB;

-- technicians
CREATE TABLE `technician` (
  technician_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  name VARCHAR(50) NULL,
  position VARCHAR(50) NULL,
  phone_number VARCHAR(20) NULL
) ENGINE=InnoDB;

-- tickets: one user has many tickets, one technician has many tickets
CREATE TABLE `ticket` (
  ticket_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  subject VARCHAR(150) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  resolved_date DATETIME NULL,
  priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
  status VARCHAR(15) NOT NULL DEFAULT 'NEW',
  resolution_notes VARCHAR(1000) NULL,
  user_id BIGINT NOT NULL,
  technician_id BIGINT NULL,
  CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES `user` (user_id),
  CONSTRAINT fk_ticket_technician FOREIGN KEY (technician_id) REFERENCES `technician` (technician_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- comments: one ticket has many comments (deleting a ticket deletes its comments)
CREATE TABLE `comment` (
  comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  author VARCHAR(50) NOT NULL,
  text VARCHAR(1000) NOT NULL,
  comment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_comment_ticket FOREIGN KEY (ticket_id) REFERENCES `ticket` (ticket_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- starting data
INSERT INTO `user` (username, password, role) VALUES
  ('student1', 'password123', 'Student/Staff'),
  ('abc', 'password123', 'Student/Staff'),
  ('admin', 'password123', 'Administrator');

INSERT INTO `technician` (username, password) VALUES
  ('tech', 'password123'),
  ('tech2', 'password123');

INSERT INTO `ticket` (subject, description, creation_date, user_id, technician_id, priority, status) VALUES
  ('Bad Wi-Fi across campus', 'The campus Wi-Fi keeps dropping and is too slow to use, all across campus.', '2026-11-17 09:15:00', 1, 1, 'HIGH', 'ASSIGNED'),
  ('Moodle is down', 'Moodle will not load, so course pages and assignment submissions cannot be reached.', '2026-11-18 13:40:00', 1, 1, 'HIGH', 'IN_PROGRESS'),
  ('Major project ethics form will not submit', 'The ethics form for the major project will not submit while on campus.', '2026-11-19 10:05:00', 1, NULL, 'MEDIUM', 'NEW');
  
-- check: user 3 rows, technician 2, ticket 3, comment 0
SELECT (SELECT COUNT(*) FROM `user`) AS users, (SELECT COUNT(*) FROM `technician`) AS technicians,
       (SELECT COUNT(*) FROM `ticket`) AS tickets, (SELECT COUNT(*) FROM `comment`) AS comments;
