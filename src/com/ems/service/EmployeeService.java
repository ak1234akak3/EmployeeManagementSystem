package com.ems.service;

import com.ems.dao.EmployeeDAO;
import com.ems.dao.EmployeeDAOImpl;
import com.ems.model.Employee;
import com.ems.util.InputValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer coordinating business rules, data validation, and database operations.
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    /**
     * Adds a new employee after validating all constraints.
     */
    public ServiceResult<Employee> addEmployee(Employee emp) {
        if (emp == null) {
            return ServiceResult.failure("Employee data cannot be null.");
        }

        if (!InputValidator.isValidName(emp.getName())) {
            return ServiceResult.failure("Invalid Name. Must be 2-100 alphabetic characters.");
        }

        if (!InputValidator.isValidEmail(emp.getEmail())) {
            return ServiceResult.failure("Invalid Email format (e.g. user@company.com).");
        }

        if (employeeDAO.isEmailExists(emp.getEmail(), 0)) {
            return ServiceResult.failure("An employee with email '" + emp.getEmail() + "' already exists.");
        }

        if (!InputValidator.isNonEmpty(emp.getDepartment(), 2, 50)) {
            return ServiceResult.failure("Department must be between 2 and 50 characters.");
        }

        if (!InputValidator.isNonEmpty(emp.getDesignation(), 2, 50)) {
            return ServiceResult.failure("Designation must be between 2 and 50 characters.");
        }

        if (!InputValidator.isValidSalary(emp.getSalary())) {
            return ServiceResult.failure("Salary must be a positive number.");
        }

        if (!InputValidator.isValidPhone(emp.getPhone())) {
            return ServiceResult.failure("Invalid Phone number format (7-20 digits).");
        }

        if (emp.getHireDate() == null) {
            return ServiceResult.failure("Hire date is required (YYYY-MM-DD).");
        }

        boolean success = employeeDAO.addEmployee(emp);
        if (success) {
            return ServiceResult.success("Employee successfully added with ID: #" + emp.getId(), emp);
        } else {
            return ServiceResult.failure("Failed to save employee to MySQL database.");
        }
    }

    /**
     * Retrieves all employees.
     */
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    /**
     * Finds an employee by ID.
     */
    public Employee getEmployeeById(int id) {
        if (id <= 0) return null;
        return employeeDAO.getEmployeeById(id);
    }

    /**
     * Updates an existing employee after validating constraints.
     */
    public ServiceResult<Employee> updateEmployee(Employee emp) {
        if (emp == null || emp.getId() <= 0) {
            return ServiceResult.failure("Invalid Employee ID for update.");
        }

        Employee existing = employeeDAO.getEmployeeById(emp.getId());
        if (existing == null) {
            return ServiceResult.failure("Employee with ID #" + emp.getId() + " does not exist.");
        }

        if (!InputValidator.isValidName(emp.getName())) {
            return ServiceResult.failure("Invalid Name. Must be 2-100 characters.");
        }

        if (!InputValidator.isValidEmail(emp.getEmail())) {
            return ServiceResult.failure("Invalid Email format.");
        }

        if (employeeDAO.isEmailExists(emp.getEmail(), emp.getId())) {
            return ServiceResult.failure("Email '" + emp.getEmail() + "' is already used by another employee.");
        }

        if (!InputValidator.isNonEmpty(emp.getDepartment(), 2, 50)) {
            return ServiceResult.failure("Department must be between 2 and 50 characters.");
        }

        if (!InputValidator.isNonEmpty(emp.getDesignation(), 2, 50)) {
            return ServiceResult.failure("Designation must be between 2 and 50 characters.");
        }

        if (!InputValidator.isValidSalary(emp.getSalary())) {
            return ServiceResult.failure("Salary must be a positive number.");
        }

        if (!InputValidator.isValidPhone(emp.getPhone())) {
            return ServiceResult.failure("Invalid Phone number format.");
        }

        if (emp.getHireDate() == null) {
            return ServiceResult.failure("Hire date cannot be null.");
        }

        boolean success = employeeDAO.updateEmployee(emp);
        if (success) {
            return ServiceResult.success("Employee #" + emp.getId() + " updated successfully!", emp);
        } else {
            return ServiceResult.failure("Failed to update employee in database.");
        }
    }

    /**
     * Deletes an employee by ID.
     */
    public ServiceResult<Boolean> deleteEmployee(int id) {
        if (id <= 0) {
            return ServiceResult.failure("Invalid Employee ID.");
        }

        Employee existing = employeeDAO.getEmployeeById(id);
        if (existing == null) {
            return ServiceResult.failure("Employee with ID #" + id + " does not exist.");
        }

        boolean success = employeeDAO.deleteEmployee(id);
        if (success) {
            return ServiceResult.success("Employee #" + id + " (" + existing.getName() + ") deleted successfully.", true);
        } else {
            return ServiceResult.failure("Failed to delete employee with ID #" + id);
        }
    }

    /**
     * Searches employees by name.
     */
    public List<Employee> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeDAO.searchByName(name.trim());
    }

    /**
     * Searches employees by department.
     */
    public List<Employee> searchByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeDAO.searchByDepartment(department.trim());
    }

    /**
     * Searches employees by designation.
     */
    public List<Employee> searchByDesignation(String designation) {
        if (designation == null || designation.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeDAO.searchByDesignation(designation.trim());
    }

    /**
     * Searches employees by salary range.
     */
    public List<Employee> searchBySalaryRange(double min, double max) {
        if (min < 0) min = 0;
        if (max < min) {
            double temp = min;
            min = max;
            max = temp;
        }
        return employeeDAO.searchBySalaryRange(min, max);
    }

    /**
     * Aggregates employee statistics.
     */
    public Statistics getStatistics() {
        List<Employee> all = getAllEmployees();
        int total = all.size();
        if (total == 0) {
            return new Statistics(0, 0, 0, 0, Map.of());
        }

        double totalSalary = all.stream().mapToDouble(Employee::getSalary).sum();
        double avgSalary = totalSalary / total;
        double minSalary = all.stream().mapToDouble(Employee::getSalary).min().orElse(0.0);
        double maxSalary = all.stream().mapToDouble(Employee::getSalary).max().orElse(0.0);

        Map<String, Long> deptCounts = all.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

        return new Statistics(total, avgSalary, minSalary, maxSalary, deptCounts);
    }

    /**
     * Result container for service operations with status & message.
     */
    public static class ServiceResult<T> {
        private final boolean success;
        private final String message;
        private final T data;

        private ServiceResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ServiceResult<T> success(String message, T data) {
            return new ServiceResult<>(true, message, data);
        }

        public static <T> ServiceResult<T> failure(String message) {
            return new ServiceResult<>(false, message, null);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }

    /**
     * Summary statistics container.
     */
    public static class Statistics {
        public final int totalEmployees;
        public final double averageSalary;
        public final double minSalary;
        public final double maxSalary;
        public final Map<String, Long> departmentDistribution;

        public Statistics(int totalEmployees, double averageSalary, double minSalary, double maxSalary, Map<String, Long> departmentDistribution) {
            this.totalEmployees = totalEmployees;
            this.averageSalary = averageSalary;
            this.minSalary = minSalary;
            this.maxSalary = maxSalary;
            this.departmentDistribution = departmentDistribution;
        }
    }
}
