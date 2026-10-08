package com.ems.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing an Employee in the Employee Management System.
 */
public class Employee {
    private int id;
    private String name;
    private String email;
    private String department;
    private String designation;
    private double salary;
    private String phone;
    private LocalDate hireDate;

    // Default Constructor
    public Employee() {
    }

    // Constructor without ID (useful when adding a new employee where ID is auto-generated)
    public Employee(String name, String email, String department, String designation, double salary, String phone, LocalDate hireDate) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.phone = phone;
        this.hireDate = hireDate;
    }

    // Full Constructor
    public Employee(int id, String name, String email, String department, String designation, double salary, String phone, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.phone = phone;
        this.hireDate = hireDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", salary=" + String.format("$%.2f", salary) +
                ", phone='" + phone + '\'' +
                ", hireDate=" + hireDate +
                '}';
    }
}
