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
## Project Structure

```
├── helpdesk-client/                  # JavaFX client (GUI): login, dashboards, reports, popups
├── helpdesk-rest-service/            # Spring Boot REST service + MySQL database (JPA/Hibernate), port 8080
├── helpdesk-notification-server/     # TCP notification server, port 9090
├── helpdesk_db.sql                   # Creates the helpdesk_db database and sample data
├── documentation.zip                 # Problem definition, requirements and the 4 diagrams
├── .gitignore                        # Leaves out target/ and logs/
└── README.md                         # This file
```
---
## How to Run

### Requirements
- **JDK 17** (tested with Eclipse Temurin 17.0.19)
- **Eclipse IDE** with Maven
- **USBWebserver** (MySQL on port 3307, user `root`, password `usbw`)

### Eclipse setup (once)
1. **File > Import > Maven > Existing Maven Projects**, then select all 3 projects
2. Make sure Eclipse uses the JDK, not its built-in JRE:
   - **Window > Preferences > Java > Installed JREs**: tick your JDK 17 (e.g. `jdk-17.0.19.10-hotspot`)
   - **Installed JREs > Execution Environments > JavaSE-17**: tick the same JDK
   - Apply, then **Project > Clean** on all 3 projects
3. If a project still shows errors, right-click it > **Maven > Update Project** (tick Force Update)

If you see `java.lang.Enum cannot be resolved`, step 2 hasn't been done yet.

### Database setup (once)
1. Start **MySQL** in USBWebserver
2. Open phpMyAdmin, open the **SQL** tab and run `helpdesk_db.sql`
3. Check that `helpdesk_db` has 4 tables: `user`, `technician`, `ticket`, `comment`
   (if `comment` is missing, select `helpdesk_db` and run just the `CREATE TABLE comment` block from the file)

### Start the system (in this order)
1. **MySQL** (USBWebserver)
2. `NotificationServerLauncher` (helpdesk-notification-server), port 9090
3. `HelpDeskRestServiceApplication` (helpdesk-rest-service), port 8080
   - wait for `Database connection successful (helpdesk_db)`
4. `Launcher` (helpdesk-client)

Run each one with right-click > **Run As > Java Application**.

### Logins
All accounts use the password `password123`.

| Username | Role |
|---|---|
| `student1` | Student/Staff |
| `admin` | Administrator |
| `tech` | Technician |
