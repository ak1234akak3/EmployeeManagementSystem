package com.ems.dao;

import com.ems.model.Employee;
import com.ems.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Implementation of the EmployeeDAO interface for MySQL.
 */
public class EmployeeDAOImpl implements EmployeeDAO {

    private static final String SQL_INSERT = 
            "INSERT INTO employees (name, email, department, designation, salary, phone, hire_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees ORDER BY id ASC";

    private static final String SQL_SELECT_BY_ID = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees WHERE id = ?";

    private static final String SQL_UPDATE = 
            "UPDATE employees SET name = ?, email = ?, department = ?, designation = ?, salary = ?, phone = ?, hire_date = ? WHERE id = ?";

    private static final String SQL_DELETE = 
            "DELETE FROM employees WHERE id = ?";

    private static final String SQL_SEARCH_NAME = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees WHERE LOWER(name) LIKE ? ORDER BY id ASC";

    private static final String SQL_SEARCH_DEPT = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees WHERE LOWER(department) LIKE ? ORDER BY id ASC";

    private static final String SQL_SEARCH_DESIGNATION = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees WHERE LOWER(designation) LIKE ? ORDER BY id ASC";

    private static final String SQL_SEARCH_SALARY = 
            "SELECT id, name, email, department, designation, salary, phone, hire_date FROM employees WHERE salary >= ? AND salary <= ? ORDER BY salary ASC";

    private static final String SQL_CHECK_EMAIL = 
            "SELECT COUNT(*) FROM employees WHERE email = ? AND id != ?";

    private static final String SQL_COUNT = 
            "SELECT COUNT(*) FROM employees";

    private static final String SQL_AVG_SALARY = 
            "SELECT AVG(salary) FROM employees";

    @Override
    public boolean addEmployee(Employee employee) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getDepartment());
            stmt.setString(4, employee.getDesignation());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPhone());
            stmt.setDate(7, Date.valueOf(employee.getHireDate() != null ? employee.getHireDate() : LocalDate.now()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] addEmployee failed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] getAllEmployees failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Employee getEmployeeById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] getEmployeeById failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getDepartment());
            stmt.setString(4, employee.getDesignation());
            stmt.setDouble(5, employee.getSalary());
            stmt.setString(6, employee.getPhone());
            stmt.setDate(7, Date.valueOf(employee.getHireDate() != null ? employee.getHireDate() : LocalDate.now()));
            stmt.setInt(8, employee.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] updateEmployee failed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteEmployee(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("[DAO Error] deleteEmployee failed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Employee> searchByName(String name) {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_NAME)) {

            stmt.setString(1, "%" + name.toLowerCase().trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] searchByName failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Employee> searchByDepartment(String department) {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_DEPT)) {

            stmt.setString(1, "%" + department.toLowerCase().trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] searchByDepartment failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Employee> searchByDesignation(String designation) {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_DESIGNATION)) {

            stmt.setString(1, "%" + designation.toLowerCase().trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] searchByDesignation failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Employee> searchBySalaryRange(double minSalary, double maxSalary) {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_SALARY)) {

            stmt.setDouble(1, minSalary);
            stmt.setDouble(2, maxSalary);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEmployee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] searchBySalaryRange failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean isEmailExists(String email, int excludeId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CHECK_EMAIL)) {

            stmt.setString(1, email.trim().toLowerCase());
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] isEmailExists failed: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getTotalEmployeeCount() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] getTotalEmployeeCount failed: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getAverageSalary() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_AVG_SALARY);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO Error] getAverageSalary failed: " + e.getMessage());
        }
        return 0.0;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String department = rs.getString("department");
        String designation = rs.getString("designation");
        double salary = rs.getDouble("salary");
        String phone = rs.getString("phone");
        Date hireDateSql = rs.getDate("hire_date");
        LocalDate hireDate = hireDateSql != null ? hireDateSql.toLocalDate() : null;

        return new Employee(id, name, email, department, designation, salary, phone, hireDate);
    }
}
