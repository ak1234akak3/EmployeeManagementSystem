# 🏢 Employee Management System (EMS)

An enterprise-grade **Employee Management System** built with **Java** and **MySQL JDBC**, featuring a clean Layered DAO Architecture, comprehensive CRUD operations, flexible multi-criteria search, statistics/analytics, and dual interfaces (Interactive Terminal CLI and Modern Swing Desktop GUI).

---

## 🌟 Key Features

| Operation | Description |
| :--- | :--- |
| **➕ Add Employee** | Add new employee records with rigorous validation (Name, Email format, Department, Role, Salary, Phone, Hire Date). Prevents duplicate emails. |
| **📋 View Employees** | List all employees in dynamically formatted ASCII tables (CLI) or sortable desktop data grid (GUI). |
| **🔍 View by ID** | Detailed card view for an individual employee. |
| **✏️ Update Employee** | Modify any field selectively with prompt defaults (keep existing or update value). |
| **🗑️ Delete Employee** | Safely delete employee records with confirmation checks. |
| **🔎 Search Employee** | Search by **Name** (partial matching), **Department**, **Designation / Role**, or **Salary Range** (min–max). |
| **📊 Analytics & Stats** | Real-time statistics: Total employees, average salary, highest/lowest salary, and department distribution. |
| **💾 MySQL Persistence** | Auto-initializes `employee_db` and `employees` table on startup with sample data if empty. |
| **🖥️ Dual Interfaces** | Interactive terminal CLI with ANSI styling AND Modern Swing Desktop GUI. |

---

## 📁 Project Structure

```text
EmployeeManagementSystem/
├── lib/
│   └── mysql-connector-j-8.4.0.jar      # Bundled MySQL JDBC Driver
├── resources/
│   ├── db.properties                    # Database configuration (Host, Port, User, Pass)
│   └── schema.sql                       # Standalone SQL schema & seed script
├── src/
│   └── com/
│       └── ems/
│           ├── Main.java                # Application launcher (CLI or GUI selector)
│           ├── model/
│           │   └── Employee.java        # Employee POJO entity
│           ├── dao/
│           │   ├── EmployeeDAO.java     # DAO Interface for CRUD & Search operations
│           │   └── EmployeeDAOImpl.java # JDBC implementation with PreparedStatements
│           ├── service/
│           │   └── EmployeeService.java # Business logic, validations, & statistics
│           ├── ui/
│           │   ├── ConsoleMenu.java     # Interactive terminal CLI menu
│           │   └── EmployeeGUI.java     # Modern Swing desktop graphical interface
│           └── util/
│               ├── AnsiColors.java      # Terminal styling & ANSI color codes
│               ├── DBConnection.java    # JDBC Singleton connection manager
│               ├── DatabaseInitializer.java # Auto database & table creator
│               ├── InputValidator.java  # Regex & business rule validators
│               └── TableFormatter.java  # Dynamic ASCII table & card renderer
├── bin/                                 # Compiled .class files
├── compile.bat / compile.ps1            # 1-click build scripts
├── run-cli.bat / run-cli.ps1            # 1-click CLI launch scripts
├── run-gui.bat / run-gui.ps1            # 1-click GUI launch scripts
├── pom.xml                              # Maven project configuration (optional)
└── README.md                            # Documentation
```

---

## 🗄️ Database Setup (MySQL)

### Automatic Setup (Zero-Configuration)
When you start the application, `DatabaseInitializer` will automatically:
1. Connect to MySQL (`localhost:3306`).
2. Create the `employee_db` database if missing.
3. Create the `employees` table if missing.
4. Populate sample demo employees if the table is empty!

### Configuring Credentials
Edit [resources/db.properties](file:///C:/Users/Lenovo/.gemini/antigravity-ide/scratch/EmployeeManagementSystem/resources/db.properties):
```properties
db.host=localhost
db.port=3306
db.name=employee_db
db.username=root
db.password=
```
*(Leave password empty for standard XAMPP/WAMP setups, or specify your MySQL root password).*

### Manual SQL Setup (Optional)
You can also run the SQL script in MySQL Workbench or phpMyAdmin:
```sql
-- resources/schema.sql
CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

CREATE TABLE IF NOT EXISTS employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    salary DOUBLE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 🚀 How to Compile and Run

### Option 1: Using Batch / PowerShell Scripts (Recommended)

1. **Compile**:
   - Double-click `compile.bat` or run `./compile.ps1` in PowerShell.
2. **Run Interactive Terminal (CLI)**:
   - Double-click `run-cli.bat` or run `./run-cli.ps1`.
3. **Run Graphical Window (GUI)**:
   - Double-click `run-gui.bat` or run `./run-gui.ps1`.

---

### Option 2: Command Line (javac / java)

```bash
# Navigate to project root
cd C:\Users\Lenovo\.gemini\antigravity-ide\scratch\EmployeeManagementSystem

# 1. Compile
javac -cp ".;lib\*" -d bin src/com/ems/model/*.java src/com/ems/util/*.java src/com/ems/dao/*.java src/com/ems/service/*.java src/com/ems/ui/*.java src/com/ems/Main.java

# 2. Copy resources
copy resources\* bin\

# 3. Run CLI
java -cp "bin;lib\*" com.ems.Main --cli

# 4. Run GUI
java -cp "bin;lib\*" com.ems.Main --gui
```

---

## 📸 Sample CLI Interface

```text
╔══════════════════════════════════════════════════════════════════════╗
║                 EMPLOYEE MANAGEMENT SYSTEM (EMS)                     ║
║                 Java 25 + MySQL JDBC Enterprise Edition              ║
╚══════════════════════════════════════════════════════════════════════╝

┌─────────────────────── MAIN MENU ────────────────────────┐
│  1. Add New Employee                                  │
│  2. View All Employees                                │
│  3. View Employee Details by ID                       │
│  4. Update Employee Information                       │
│  5. Delete Employee                                   │
│  6. Search Employees (Name / Dept / Role / Salary)    │
│  7. View Analytics & Summary Statistics               │
│  8. Database Connection Settings                      │
│  9. Exit Application                                  │
└──────────────────────────────────────────────────────────┘
```

---

## 🛡️ Validation Rules
- **Name**: 2–100 alphabetic characters.
- **Email**: Strict RFC format (e.g. `user@domain.com`), unique in MySQL.
- **Phone**: 7–20 digits/symbols with optional international prefix (`+`).
- **Salary**: Positive floating-point number.
- **Hire Date**: Standard ISO-8601 format (`YYYY-MM-DD`), defaults to current date.
