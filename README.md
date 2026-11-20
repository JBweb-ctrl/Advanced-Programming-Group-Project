<h1 align="center">Advanced-Programming-Group-Project</h1>

<h3 align="center">University of Technology, Jamaica</h3>
<p align="center">
  Faculty of Engineering and Computing<br/>
  School of Computing and Information Technology<br/>
  <strong>Advanced Programming (CIT3009)</strong><br/>
  Semester 1 -- Academic Year 26/27
</p>

---

<h2 align="center">Advanced Programming Group Project University Help Desk & Ticketing System</h2>

---

<h3 align="center"> Lecturer: Mr. Christopher Panther,
Date Given: October 2, 2026,
Due Date: November 20, 2026</h3>

---
<p align="center">
</strong>Group Members:</strong><br/>
  Jhamar Brown - 2209430<br/>
  Danielle Noble - 2205710<br/>
  Shemar Campbell - 2111864<br/>

  Occurrence: UE1
  Lab: Tuesdays 8 AM
</p>

---
<p align="center">Project Description</p>

Project Overview: 
The University Help Desk & Ticketing System is a distributed software solution designed
to support the reporting, tracking, assignment, and resolution of technical support
issues within a university environment. The system will provide a desktop-based JavaFX
client for end users and support personnel, a RESTful web service layer developed using
Spring Boot, a relational database for persistent storage, a TCP/IP notification service
for real-time communication, and robust logging and exception handling mechanisms.
The project is intended to serve as a final assessment for Advanced Programming and
requires students to collaboratively integrate all major concepts covered throughout the
course into a single coherent application.

---

Project Scenario:
The university's IT Services Department currently receives support requests via email,
phone calls, and walk-in reports. This process is inefficient and makes it difficult to:
• Track reported issues
• Assign technicians
• Monitor resolution progress
• Generate reports
• Notify users of status changes
• Maintain a historical record of incidents
The university has commissioned your development team to design and implement a
computerized Help Desk & Ticketing System that addresses these issues

---

Project Objectives:
Students will design and develop a software solution capable of:
• Recording support tickets.
• Managing users and technicians.
• Assigning tickets to technicians.
• Tracking ticket progress.
• Exchanging information through RESTful services.
• Storing data persistently within a relational database.
• Providing real-time notifications through network communication.
• Maintaining audit logs.
• Processing concurrent requests safely.

---
<h1>Project Structure </h1>
├── helpdesk-client/                          # JavaFX client (GUI)
│   ├── pom.xml
│   └── src/main/
│       ├── java/jm/edu/utech/helpdesk/
│       │   ├── Launcher.java                 # Starts the JavaFX app
│       │   ├── MainApp.java                  # JavaFX Application, opens the login screen
│       │   ├── controller/                   # MVC controllers (button and form handling)
│       │   │   ├── LoginController.java
│       │   │   ├── RegisterController.java
│       │   │   ├── ChangePasswordController.java
│       │   │   ├── UserDashboardController.java
│       │   │   ├── TechnicianDashboardController.java
│       │   │   ├── AdminDashboardController.java
│       │   │   ├── ManageUsersController.java
│       │   │   ├── ManageTechniciansController.java
│       │   │   └── ReportsController.java
│       │   ├── view/                         # MVC views (JavaFX screens)
│       │   │   ├── LoginView.java
│       │   │   ├── RegisterView.java
│       │   │   ├── ChangePasswordView.java
│       │   │   ├── UserDashboardView.java
│       │   │   ├── TechnicianDashboardView.java
│       │   │   ├── AdminDashboardView.java
│       │   │   ├── ManageUsersView.java
│       │   │   ├── ManageTechniciansView.java
│       │   │   └── ReportsView.java
│       │   ├── model/                        # MVC models (Ticket, User, Technician, Comment)
│       │   ├── dto/                          # JSON records received from the REST service
│       │   ├── service/
│       │   │   ├── ApiClient.java            # Sends HTTP requests to the REST service
│       │   │   ├── ApiResponse.java          # Status code + body of a REST reply
│       │   │   ├── TicketApi.java            # Ticket endpoints
│       │   │   ├── UserApi.java              # User and login endpoints
│       │   │   ├── TechnicianApi.java        # Technician endpoints
│       │   │   ├── ReportApi.java            # Report endpoints
│       │   │   ├── TicketMapper.java         # Converts server JSON into client models
│       │   │   ├── TicketRefreshService.java # Background thread that refreshes the technician dashboard
│       │   │   ├── TicketService.java        # Old in-memory version (kept, commented out)
│       │   │   └── TechnicianService.java    # Old in-memory version (kept, commented out)
│       │   ├── network/
│       │   │   └── NotificationListener.java # TCP connection that receives popups
│       │   ├── notification/model/           # Shared notification classes (same in all 3 projects)
│       │   ├── exception/                    # Client exceptions (invalid login, server connection, etc.)
│       │   └── util/
│       │       ├── SceneManager.java         # Switches between screens
│       │       ├── AlertUtil.java            # JavaFX Alerts
│       │       ├── ErrorHandler.java         # Turns REST errors into messages
│       │       └── NotificationPopup.java    # Shows notification popups
│       └── resources/log4j2.xml              # Logging (application.log, error.log)
│
├── helpdesk-rest-service/                    # Spring Boot REST service + database
│   ├── pom.xml
│   └── src/main/
│       ├── java/jm/edu/utech/helpdesk/
│       │   ├── rest/
│       │   │   ├── HelpDeskRestServiceApplication.java  # Starts the REST service (port 8080)
│       │   │   ├── controller/               # REST endpoints
│       │   │   │   ├── UserController.java       # /api/users
│       │   │   │   ├── TicketController.java     # /api/tickets
│       │   │   │   ├── TechnicianController.java # /api/technicians
│       │   │   │   └── ReportController.java     # /api/reports
│       │   │   ├── service/                  # Business logic
│       │   │   │   ├── AccountService.java       # Login, register, passwords
│       │   │   │   ├── UserService.java
│       │   │   │   ├── TechnicianService.java
│       │   │   │   ├── TicketService.java        # Ticket rules, per-ticket locks, notifications
│       │   │   │   └── ReportService.java        # The 5 reports
│       │   │   ├── persistence/              # Database access (JPA / Hibernate)
│       │   │   │   ├── JpaUtil.java              # EntityManagerFactory
│       │   │   │   ├── DatabaseCheck.java        # Checks the MySQL connection at startup
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── TechnicianRepository.java
│       │   │   │   ├── TicketRepository.java
│       │   │   │   └── CommentRepository.java
│       │   │   ├── entity/                   # JPA entities (User, Technician, Ticket, Comment)
│       │   │   ├── model/                    # Records sent as JSON + TicketStatus enum
│       │   │   ├── dto/                      # Request and response bodies
│       │   │   ├── exception/                # Custom exceptions + GlobalExceptionHandler
│       │   │   └── notification/
│       │   │       ├── NotificationClient.java   # Sends notifications to the server over TCP
│       │   │       └── NotificationService.java  # Thread pool that sends them in the background
│       │   └── notification/model/           # Shared notification classes
│       └── resources/
│           ├── META-INF/persistence.xml      # Database connection (helpdesk_db)
│           ├── application.properties
│           └── log4j2.xml                    # Logging (application.log, error.log)
│
├── helpdesk-notification-server/             # TCP notification server (port 9090)
│   ├── pom.xml
│   └── src/main/
│       ├── java/jm/edu/utech/helpdesk/notification/
│       │   ├── NotificationServerLauncher.java   # Starts the server
│       │   ├── server/
│       │   │   ├── NotificationServer.java       # Accepts client connections
│       │   │   ├── ClientHandler.java            # One thread per connected client
│       │   │   └── SessionManager.java           # Tracks who is connected, delivers notifications
│       │   ├── model/                            # Shared notification classes
│       │   └── tools/
│       │       └── NotificationTestClient.java   # Console tool for testing the server
│       └── resources/log4j2.xml                  # Logging (application.log, error.log)
│
├── helpdesk_db.sql                           # Creates the helpdesk_db database and sample data
├── .gitignore                                # Leaves out target/ and logs/
└── README.md                                 # This file
---
