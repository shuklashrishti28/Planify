# 🚀 Planify - Web-Based Project Management System

Planify is a full-stack, role-based project management web application built using Jakarta EE Servlets, JSP, MySQL, and Bootstrap 5.
Operating on an MVC architecture with a DAO design pattern, it features secure session-based authentication and supports three distinct user roles:
Admins who manage system users, 
Project Managers who create projects and allocate tasks,
and Team Members who monitor deliverables and update workflow statuses in real time.
---

## 🛠️ Tech Stack

- **Backend**: Java Servlets (Jakarta EE), JDBC
- **Frontend**: JSP (JavaServer Pages), HTML5, CSS3, Bootstrap 5
- **Database**: MySQL Server
- **Server**: Apache Tomcat 10.1+
- **Build Tool**: Apache Maven

---

## 📌 Features

- **Role-Based Access Control (RBAC)**:
  - **Admin**: Manages system users and assigns roles.
  - **Project Manager**: Creates projects, monitors timelines, and assigns tasks.
  - **Team Member**: Views assigned tasks and updates workflow status (`PENDING` -> `IN_PROGRESS` -> `COMPLETED`).
- **Secure Authentication**: Session-based login and logout mechanism.
- **Modern UI**: Fully responsive interface designed with Bootstrap 5 and dynamic color badges.
  
---

## 📁 Project Directory Structure

```text
Planify/
│
├── src/main/java/com/planify/
│   ├── controller/      # Servlets (Login, User, Project, Task, Logout)
│   ├── dao/             # Database Access Objects (UserDAO, ProjectDAO, TaskDAO)
│   ├── model/           # POJO classes (User, Project, Task)
│   └── util/            # Utility classes (DBConnection)
│
├── src/main/webapp/
│   ├── login.jsp        # Authentication Page
│   ├── admin_dashboard.jsp
│   ├── pm_dashboard.jsp
│   ├── member_dashboard.jsp
│   ├── manage_users.jsp
│   ├── manage_projects.jsp
│   ├── manage_tasks.jsp
│   └── logout.jsp       # Session Invalidation Page
│
└── pom.xml              # Maven Configuration & Dependencies
🗄️ Database Setup (planify_db)
SQL
CREATE DATABASE planify_db;
USE planify_db;

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PROJECT_MANAGER', 'TEAM_MEMBER') NOT NULL
);

CREATE TABLE Projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT,
    assigned_to INT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'PENDING',
    due_date DATE,
    FOREIGN KEY (project_id) REFERENCES Projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES Users(user_id) ON DELETE SET NULL
);

-- Default Test Users
INSERT INTO Users (name, email, password, role) VALUES 
('System Admin', 'admin@planify.com', 'admin123', 'ADMIN'),
('Project Lead', 'pm@planify.com', 'pm123', 'PROJECT_MANAGER'),
('Software Engineer', 'member@planify.com', 'member123', 'TEAM_MEMBER');
⚙️ How to Run Locally
Clone the Repository:

Bash
git clone https://github.com/shuklashrishti28/Planify.git
Open in IntelliJ IDEA: Import project as a Maven Project.

Configure Database: Set the following environment variables for the Tomcat process (defaults are localhost, root, and a blank password):

```text
PLANIFY_DB_URL=jdbc:mysql://localhost:3306/planify_db?useSSL=false&allowPublicKeyRetrieval=true
PLANIFY_DB_USER=root
PLANIFY_DB_PASSWORD=your_mysql_password
```

Configure Tomcat: Set up Apache Tomcat 10+ using Smart Tomcat or standard Tomcat Server configuration in IntelliJ.

Build the deployable WAR with `mvn package`, deploy `target/Planify-1.0-SNAPSHOT.war` to Tomcat 10.1+, then navigate to:

Plaintext
http://localhost:8080/login.jsp
🔑 Default Login Credentials
Admin: admin@planify.com / admin123

Project Manager: pm@planify.com / pm123

Team Member: member@planify.com / member123

For new users, the admin form requires passwords with at least 8 characters. The sample SQL credentials above are legacy demo values; if they are used, change them to passwords of at least 8 characters before inserting the sample users.

New passwords are stored as salted PBKDF2 hashes. If a legacy demo account is already in the database with a plain-text password, a successful login upgrades that account to the hashed format automatically. Use the role-specific dashboard to manage users, projects, tasks, and task progress.

## Run Tests

Run the unit test suite with:

```bash
mvn test
```

Tests cover project/task servlet authorization and invalid-form handling, dashboard routing, HTML escaping, password hashing and verification, and CSRF token behavior.
