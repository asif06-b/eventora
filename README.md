Eventora — Event Management System (Java Swing)

A desktop Event Management System built with Java Swing and MySQL.
The app provides a simple GUI for adding, viewing, updating, searching and exporting events — plus basic user management and event registrations.

🔎 Project Highlights

Pure Java Swing desktop UI (no web front-end)

CRUD for events (Add / View / Update / Search / Filter)

User management (add users, login)

Registration system (register user to an event)

Export events to CSV (events_export.csv)

Simple dashboard with sidebar and hero image

⚙️ Tech / Requirements

Java 11+ (works with Java 8+, but Java 11+ recommended)

MySQL Server (or compatible, running locally)

MySQL JDBC Driver (Connector/J) on classpath — e.g. mysql-connector-java-8.x.x.jar

IDE or command-line environment to compile & run Java

🔌 JDBC Configuration (edit in Swing class)

By default the code uses:
static final String URL = "jdbc:mysql://localhost:3306/eventdb";
static final String USER = "root";
static final String PASS = "asif06";

Change these constants to suit your local DB credentials. Consider moving credentials to an external config file or environment variables for production.

Important: The IMAGE_PATH constant in your code currently contains extra quotes. Use a plain path string like:
static final String IMAGE_PATH = "C:\\Users\\Asif\\Downloads\\cover.jpg";

🗄️ Database Schema (SQL)

Run these statements in your MySQL console to create the database and required tables:
-- Create database
CREATE DATABASE IF NOT EXISTS eventdb;
USE eventdb;

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- Events table
CREATE TABLE IF NOT EXISTS events (
  id INT AUTO_INCREMENT PRIMARY KEY,
  event_name VARCHAR(255) NOT NULL,
  event_date DATE NOT NULL,
  location VARCHAR(255),
  organizer VARCHAR(255),
  capacity INT DEFAULT 0
);

-- Registrations table
CREATE TABLE IF NOT EXISTS registrations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  event_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
  FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

🧭 How to run
Using the command line (without build tools)

Compile (make sure mysql-connector-java.jar is on the classpath):
javac -cp .;path\to\mysql-connector-java-8.x.x.jar Swing.java

Run:
java -cp .;path\to\mysql-connector-java-8.x.x.jar Swing

📁 Output files

events_export.csv will be created in the working directory when you use Export CSV.
