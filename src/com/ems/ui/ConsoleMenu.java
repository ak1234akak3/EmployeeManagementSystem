package com.ems.ui;

import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import com.ems.service.EmployeeService.ServiceResult;
import com.ems.service.EmployeeService.Statistics;
import com.ems.util.AnsiColors;
import com.ems.util.DBConnection;
import com.ems.util.DatabaseInitializer;
import com.ems.util.InputValidator;
import com.ems.util.TableFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive Console / Terminal User Interface for Employee Management System.
 */
public class ConsoleMenu {

    private final EmployeeService employeeService;
    private final Scanner scanner;

    public ConsoleMenu() {
        this.employeeService = new EmployeeService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        // Check & Initialize Database Connection
        System.out.println(AnsiColors.cyan("Connecting to MySQL database..."));
        boolean dbReady = DatabaseInitializer.initializeDatabase();
        if (dbReady) {
            System.out.println(AnsiColors.boldGreen("✔ Connected to MySQL successfully! [Database: " + DBConnection.getDbName() + "]\n"));
        } else {
            System.out.println(AnsiColors.boldYellow("⚠ Warning: Could not connect to MySQL with default settings."));
            System.out.println(AnsiColors.yellow("  You can reconfigure credentials in Menu Option #8 or edit 'resources/db.properties'.\n"));
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntPrompt("👉 Enter your choice [1-9]: ", 1, 9);
            System.out.println();

            switch (choice) {
                case 1 -> handleAddEmployee();
                case 2 -> handleViewAllEmployees();
                case 3 -> handleViewEmployeeById();
                case 4 -> handleUpdateEmployee();
                case 5 -> handleDeleteEmployee();
                case 6 -> handleSearchEmployees();
                case 7 -> handleViewStatistics();
                case 8 -> handleDatabaseSettings();
                case 9 -> {
                    running = false;
                    System.out.println(AnsiColors.boldGreen("Thank you for using Employee Management System. Goodbye!\n"));
                }
            }

            if (running) {
                pressEnterToContinue();
            }
        }
    }

    private void printBanner() {
        System.out.println(AnsiColors.BRIGHT_CYAN + """
        +======================================================================+
        |                 EMPLOYEE MANAGEMENT SYSTEM (EMS)                     |
        |                 Java 25 + MySQL JDBC Enterprise Edition              |
        +======================================================================+
        """ + AnsiColors.RESET);
    }

    private void printMainMenu() {
        System.out.println(AnsiColors.CYAN + "+----------------------- " + AnsiColors.BOLD + "MAIN MENU" + AnsiColors.RESET + AnsiColors.CYAN + " ------------------------+" + AnsiColors.RESET);
        System.out.println("|  " + AnsiColors.boldCyan("1.") + " Add New Employee                                  |");
        System.out.println("|  " + AnsiColors.boldCyan("2.") + " View All Employees                                |");
        System.out.println("|  " + AnsiColors.boldCyan("3.") + " View Employee Details by ID                       |");
        System.out.println("|  " + AnsiColors.boldCyan("4.") + " Update Employee Information                       |");
        System.out.println("|  " + AnsiColors.boldCyan("5.") + " Delete Employee                                   |");
        System.out.println("|  " + AnsiColors.boldCyan("6.") + " Search Employees (Name / Dept / Role / Salary)    |");
        System.out.println("|  " + AnsiColors.boldCyan("7.") + " View Analytics & Summary Statistics               |");
        System.out.println("|  " + AnsiColors.boldCyan("8.") + " Database Connection Settings                      |");
        System.out.println("|  " + AnsiColors.boldCyan("9.") + " Exit Application                                  |");
        System.out.println(AnsiColors.CYAN + "+----------------------------------------------------------+" + AnsiColors.RESET);
    }

    // -------------------------------------------------------------
    // Operation 1: Add Employee
    // -------------------------------------------------------------
    private void handleAddEmployee() {
        System.out.println(AnsiColors.boldCyan("=== [+] ADD NEW EMPLOYEE ==="));

        String name = readValidString("Enter Full Name: ", InputValidator::isValidName, "Invalid Name (2-100 characters, letters only).");
        
        String email;
        while (true) {
            email = readValidString("Enter Email: ", InputValidator::isValidEmail, "Invalid Email format (e.g. name@company.com).");
            if (new com.ems.dao.EmployeeDAOImpl().isEmailExists(email, 0)) {
                System.out.println(AnsiColors.boldRed("✖ An employee with this email already exists! Please use a unique email."));
            } else {
                break;
            }
        }

        String department = readValidString("Enter Department (e.g., Engineering, HR, Finance): ", 
                s -> InputValidator.isNonEmpty(s, 2, 50), "Department must be 2-50 characters.");

        String designation = readValidString("Enter Designation (e.g., Senior Software Engineer): ", 
                s -> InputValidator.isNonEmpty(s, 2, 50), "Designation must be 2-50 characters.");

        double salary = readDoublePrompt("Enter Monthly / Annual Salary ($): ", 0.01, 10_000_000.0);

        String phone = readValidString("Enter Phone Number (e.g. +1-555-0199): ", 
                InputValidator::isValidPhone, "Invalid Phone format (7-20 digits/symbols).");

        LocalDate hireDate = readDatePrompt("Enter Hire Date (YYYY-MM-DD) [Press Enter for Today]: ", true);

        Employee newEmp = new Employee(name, email, department, designation, salary, phone, hireDate);
        ServiceResult<Employee> result = employeeService.addEmployee(newEmp);

        if (result.isSuccess()) {
            System.out.println(AnsiColors.boldGreen("\n✔ " + result.getMessage()));
            TableFormatter.printEmployeeCard(result.getData());
        } else {
            System.out.println(AnsiColors.boldRed("\n✖ Failed to add employee: " + result.getMessage()));
        }
    }

    // -------------------------------------------------------------
    // Operation 2: View All Employees
    // -------------------------------------------------------------
    private void handleViewAllEmployees() {
        System.out.println(AnsiColors.boldCyan("=== [📋] ALL EMPLOYEES DIRECTORY ==="));
        List<Employee> list = employeeService.getAllEmployees();
        TableFormatter.printEmployeeTable(list);
    }

    // -------------------------------------------------------------
    // Operation 3: View Employee by ID
    // -------------------------------------------------------------
    private void handleViewEmployeeById() {
        System.out.println(AnsiColors.boldCyan("=== [🔍] VIEW EMPLOYEE BY ID ==="));
        int id = readIntPrompt("Enter Employee ID: ", 1, Integer.MAX_VALUE);
        Employee emp = employeeService.getEmployeeById(id);

        if (emp != null) {
            TableFormatter.printEmployeeCard(emp);
        } else {
            System.out.println(AnsiColors.boldRed("✖ No employee found with ID #" + id));
        }
    }

    // -------------------------------------------------------------
    // Operation 4: Update Employee
    // -------------------------------------------------------------
    private void handleUpdateEmployee() {
        System.out.println(AnsiColors.boldCyan("=== [✏️] UPDATE EMPLOYEE INFORMATION ==="));
        int id = readIntPrompt("Enter Employee ID to update: ", 1, Integer.MAX_VALUE);
        Employee existing = employeeService.getEmployeeById(id);

        if (existing == null) {
            System.out.println(AnsiColors.boldRed("✖ Employee with ID #" + id + " not found!"));
            return;
        }

        System.out.println(AnsiColors.yellow("\nCurrent Details:"));
        TableFormatter.printEmployeeCard(existing);
        System.out.println(AnsiColors.cyan("Tip: Press [Enter] to keep the current value.\n"));

        // Name
        System.out.print("Update Name [" + existing.getName() + "]: ");
        String nameInput = scanner.nextLine().trim();
        String name = nameInput.isEmpty() ? existing.getName() : nameInput;
        if (!InputValidator.isValidName(name)) {
            System.out.println(AnsiColors.yellow("Invalid name entered. Keeping existing: " + existing.getName()));
            name = existing.getName();
        }

        // Email
        System.out.print("Update Email [" + existing.getEmail() + "]: ");
        String emailInput = scanner.nextLine().trim();
        String email = emailInput.isEmpty() ? existing.getEmail() : emailInput;
        if (!InputValidator.isValidEmail(email)) {
            System.out.println(AnsiColors.yellow("Invalid email entered. Keeping existing: " + existing.getEmail()));
            email = existing.getEmail();
        }

        // Department
        System.out.print("Update Department [" + existing.getDepartment() + "]: ");
        String deptInput = scanner.nextLine().trim();
        String department = deptInput.isEmpty() ? existing.getDepartment() : deptInput;

        // Designation
        System.out.print("Update Designation [" + existing.getDesignation() + "]: ");
        String desigInput = scanner.nextLine().trim();
        String designation = desigInput.isEmpty() ? existing.getDesignation() : desigInput;

        // Salary
        System.out.print("Update Salary [$" + String.format("%.2f", existing.getSalary()) + "]: ");
        String salaryInput = scanner.nextLine().trim();
        double salary = existing.getSalary();
        if (!salaryInput.isEmpty()) {
            try {
                double parsed = Double.parseDouble(salaryInput);
                if (InputValidator.isValidSalary(parsed)) {
                    salary = parsed;
                } else {
                    System.out.println(AnsiColors.yellow("Salary must be positive. Keeping existing: $" + salary));
                }
            } catch (NumberFormatException e) {
                System.out.println(AnsiColors.yellow("Invalid number. Keeping existing salary: $" + salary));
            }
        }

        // Phone
        System.out.print("Update Phone [" + existing.getPhone() + "]: ");
        String phoneInput = scanner.nextLine().trim();
        String phone = phoneInput.isEmpty() ? existing.getPhone() : phoneInput;
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println(AnsiColors.yellow("Invalid phone entered. Keeping existing: " + existing.getPhone()));
            phone = existing.getPhone();
        }

        // Hire Date
        System.out.print("Update Hire Date [" + existing.getHireDate() + "] (YYYY-MM-DD): ");
        String dateInput = scanner.nextLine().trim();
        LocalDate hireDate = existing.getHireDate();
        if (!dateInput.isEmpty()) {
            LocalDate parsed = InputValidator.parseDate(dateInput);
            if (parsed != null) {
                hireDate = parsed;
            } else {
                System.out.println(AnsiColors.yellow("Invalid date format. Keeping existing: " + hireDate));
            }
        }

        Employee updated = new Employee(id, name, email, department, designation, salary, phone, hireDate);
        ServiceResult<Employee> result = employeeService.updateEmployee(updated);

        if (result.isSuccess()) {
            System.out.println(AnsiColors.boldGreen("\n✔ " + result.getMessage()));
            TableFormatter.printEmployeeCard(result.getData());
        } else {
            System.out.println(AnsiColors.boldRed("\n✖ Update failed: " + result.getMessage()));
        }
    }

    // -------------------------------------------------------------
    // Operation 5: Delete Employee
    // -------------------------------------------------------------
    private void handleDeleteEmployee() {
        System.out.println(AnsiColors.boldCyan("=== [🗑️] DELETE EMPLOYEE ==="));
        int id = readIntPrompt("Enter Employee ID to delete: ", 1, Integer.MAX_VALUE);
        Employee existing = employeeService.getEmployeeById(id);

        if (existing == null) {
            System.out.println(AnsiColors.boldRed("✖ Employee with ID #" + id + " not found!"));
            return;
        }

        TableFormatter.printEmployeeCard(existing);
        System.out.print(AnsiColors.boldRed("⚠ Are you sure you want to permanently delete this employee? (yes/no): "));
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            ServiceResult<Boolean> result = employeeService.deleteEmployee(id);
            if (result.isSuccess()) {
                System.out.println(AnsiColors.boldGreen("✔ " + result.getMessage()));
            } else {
                System.out.println(AnsiColors.boldRed("✖ " + result.getMessage()));
            }
        } else {
            System.out.println(AnsiColors.yellow("Deletion cancelled."));
        }
    }

    // -------------------------------------------------------------
    // Operation 6: Search Employees
    // -------------------------------------------------------------
    private void handleSearchEmployees() {
        System.out.println(AnsiColors.boldCyan("=== [🔎] SEARCH EMPLOYEES ==="));
        System.out.println("1. Search by Name (Partial match)");
        System.out.println("2. Search by Department");
        System.out.println("3. Search by Designation / Role");
        System.out.println("4. Search by Salary Range");
        System.out.println("5. Back to Main Menu");

        int choice = readIntPrompt("Enter search option [1-5]: ", 1, 5);
        List<Employee> results = List.of();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter name keyword: ");
                String query = scanner.nextLine().trim();
                results = employeeService.searchByName(query);
            }
            case 2 -> {
                System.out.print("Enter department name: ");
                String query = scanner.nextLine().trim();
                results = employeeService.searchByDepartment(query);
            }
            case 3 -> {
                System.out.print("Enter designation/role keyword: ");
                String query = scanner.nextLine().trim();
                results = employeeService.searchByDesignation(query);
            }
            case 4 -> {
                double min = readDoublePrompt("Enter Minimum Salary ($): ", 0, 10_000_000.0);
                double max = readDoublePrompt("Enter Maximum Salary ($): ", min, 10_000_000.0);
                results = employeeService.searchBySalaryRange(min, max);
            }
            case 5 -> { return; }
        }

        System.out.println(AnsiColors.boldCyan("\n--- Search Results ---"));
        TableFormatter.printEmployeeTable(results);
    }

    // -------------------------------------------------------------
    // Operation 7: Statistics & Analytics
    // -------------------------------------------------------------
    private void handleViewStatistics() {
        System.out.println(AnsiColors.boldCyan("=== [📊] EMPLOYEE ANALYTICS & SUMMARY ==="));
        Statistics stats = employeeService.getStatistics();

        System.out.println("  Total Employees     : " + AnsiColors.boldGreen(String.valueOf(stats.totalEmployees)));
        System.out.printf("  Average Salary      : " + AnsiColors.boldGreen("$%,.2f\n"), stats.averageSalary);
        System.out.printf("  Minimum Salary      : " + AnsiColors.boldGreen("$%,.2f\n"), stats.minSalary);
        System.out.printf("  Maximum Salary      : " + AnsiColors.boldGreen("$%,.2f\n"), stats.maxSalary);
        System.out.println("\n  " + AnsiColors.boldCyan("Department Breakdown:"));
        for (Map.Entry<String, Long> entry : stats.departmentDistribution.entrySet()) {
            System.out.printf("    %-20s : %d employee(s)\n", entry.getKey(), entry.getValue());
        }
    }

    // -------------------------------------------------------------
    // Operation 8: Database Settings
    // -------------------------------------------------------------
    private void handleDatabaseSettings() {
        System.out.println(AnsiColors.boldCyan("=== [⚙️] DATABASE CONNECTION CONFIGURATION ==="));
        System.out.println("Current Host     : " + DBConnection.getHost());
        System.out.println("Current Port     : " + DBConnection.getPort());
        System.out.println("Current Database : " + DBConnection.getDbName());
        System.out.println("Current User     : " + DBConnection.getUsername());
        System.out.println("Password Set     : " + (DBConnection.getPassword().isEmpty() ? "(empty)" : "******"));

        boolean isConn = DBConnection.testConnection();
        System.out.println("Live Status      : " + (isConn ? AnsiColors.boldGreen("ONLINE") : AnsiColors.boldRed("OFFLINE")));

        System.out.print("\nDo you want to re-enter MySQL connection credentials? (y/n): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("y") || ans.equals("yes")) {
            System.out.print("Enter MySQL Host [default " + DBConnection.getHost() + "]: ");
            String h = scanner.nextLine().trim();
            System.out.print("Enter MySQL Port [default " + DBConnection.getPort() + "]: ");
            String p = scanner.nextLine().trim();
            System.out.print("Enter Database Name [default " + DBConnection.getDbName() + "]: ");
            String db = scanner.nextLine().trim();
            System.out.print("Enter MySQL Username [default " + DBConnection.getUsername() + "]: ");
            String u = scanner.nextLine().trim();
            System.out.print("Enter MySQL Password: ");
            String pass = scanner.nextLine();

            DBConnection.setCredentials(
                    h.isEmpty() ? null : h,
                    p.isEmpty() ? null : p,
                    db.isEmpty() ? null : db,
                    u.isEmpty() ? null : u,
                    pass
            );

            System.out.println(AnsiColors.cyan("Testing new database connection..."));
            if (DatabaseInitializer.initializeDatabase()) {
                System.out.println(AnsiColors.boldGreen("✔ Successfully reconnected to MySQL!"));
            } else {
                System.out.println(AnsiColors.boldRed("✖ Failed to connect with the provided credentials."));
            }
        }
    }

    // -------------------------------------------------------------
    // Helper Input Methods
    // -------------------------------------------------------------
    private int readIntPrompt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println(AnsiColors.red("Please enter a number between " + min + " and " + max + "."));
            } catch (NumberFormatException e) {
                System.out.println(AnsiColors.red("Invalid input. Please enter a valid integer number."));
            }
        }
    }

    private double readDoublePrompt(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf(AnsiColors.red("Please enter a value between %,.2f and %,.2f.\n"), min, max);
            } catch (NumberFormatException e) {
                System.out.println(AnsiColors.red("Invalid input. Please enter a valid number (e.g. 55000.00)."));
            }
        }
    }

    private String readValidString(String prompt, java.util.function.Predicate<String> validator, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println(AnsiColors.red(errorMsg));
        }
    }

    private LocalDate readDatePrompt(String prompt, boolean allowDefaultToday) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && allowDefaultToday) {
                return LocalDate.now();
            }
            LocalDate date = InputValidator.parseDate(input);
            if (date != null) {
                return date;
            }
            System.out.println(AnsiColors.red("Invalid date format. Please use YYYY-MM-DD (e.g., 2024-05-15)."));
        }
    }

    private void pressEnterToContinue() {
        System.out.print(AnsiColors.CYAN + "\nPress [Enter] to return to menu..." + AnsiColors.RESET);
        scanner.nextLine();
        System.out.println();
    }
}
