package com.ems.dao;

import com.ems.model.Employee;

import java.util.List;

/**
 * Data Access Object (DAO) interface for Employee operations.
 */
public interface EmployeeDAO {

    /**
     * Inserts a new employee record into MySQL.
     *
     * @param employee the employee to add
     * @return true if successfully inserted, false otherwise
     */
    boolean addEmployee(Employee employee);

    /**
     * Retrieves all employee records from MySQL.
     *
     * @return list of all employees
     */
    List<Employee> getAllEmployees();

    /**
     * Retrieves an employee by their unique ID.
     *
     * @param id the employee ID
     * @return the Employee object if found, null otherwise
     */
    Employee getEmployeeById(int id);

    /**
     * Updates an existing employee's details in MySQL.
     *
     * @param employee the updated employee entity
     * @return true if updated successfully, false otherwise
     */
    boolean updateEmployee(Employee employee);

    /**
     * Deletes an employee record by their unique ID.
     *
     * @param id the employee ID to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteEmployee(int id);

    /**
     * Searches for employees whose names contain the given query string (case-insensitive).
     *
     * @param name keyword to search for
     * @return list of matching employees
     */
    List<Employee> searchByName(String name);

    /**
     * Searches for employees belonging to a specific department.
     *
     * @param department department name keyword
     * @return list of matching employees
     */
    List<Employee> searchByDepartment(String department);

    /**
     * Searches for employees with a specific designation / role.
     *
     * @param designation designation keyword
     * @return list of matching employees
     */
    List<Employee> searchByDesignation(String designation);

    /**
     * Searches for employees whose salary falls within the specified range [min, max].
     *
     * @param minSalary minimum salary
     * @param maxSalary maximum salary
     * @return list of matching employees
     */
    List<Employee> searchBySalaryRange(double minSalary, double maxSalary);

    /**
     * Checks if an email is already registered, excluding a specific ID (useful during updates).
     *
     * @param email email to check
     * @param excludeId employee ID to exclude (or 0 for new additions)
     * @return true if email exists for another record, false otherwise
     */
    boolean isEmailExists(String email, int excludeId);

    /**
     * Gets the total number of employees in the database.
     */
    int getTotalEmployeeCount();

    /**
     * Gets the average salary across all employees.
     */
    double getAverageSalary();
}
