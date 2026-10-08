package com.ems.util;

import com.ems.model.Employee;

import java.util.List;

/**
 * Utility for rendering structured ASCII tables in console/terminal.
 */
public class TableFormatter {

    /**
     * Prints a formatted ASCII table containing a list of employees.
     */
    public static void printEmployeeTable(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println(AnsiColors.yellow("  No employee records found."));
            return;
        }

        String[] headers = {"ID", "Name", "Email", "Department", "Designation", "Salary ($)", "Phone", "Hire Date"};
        int[] colWidths = new int[headers.length];

        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
        }

        // Calculate maximum required widths per column
        for (Employee emp : employees) {
            colWidths[0] = Math.max(colWidths[0], String.valueOf(emp.getId()).length());
            colWidths[1] = Math.max(colWidths[1], nullSafeString(emp.getName()).length());
            colWidths[2] = Math.max(colWidths[2], nullSafeString(emp.getEmail()).length());
            colWidths[3] = Math.max(colWidths[3], nullSafeString(emp.getDepartment()).length());
            colWidths[4] = Math.max(colWidths[4], nullSafeString(emp.getDesignation()).length());
            colWidths[5] = Math.max(colWidths[5], String.format("%,.2f", emp.getSalary()).length());
            colWidths[6] = Math.max(colWidths[6], nullSafeString(emp.getPhone()).length());
            colWidths[7] = Math.max(colWidths[7], emp.getHireDate() != null ? emp.getHireDate().toString().length() : 4);
        }

        // Add padding
        for (int i = 0; i < colWidths.length; i++) {
            colWidths[i] += 2; // 1 space padding on each side
        }

        // Border builders (Standard cross-platform clean borders)
        String topBorder = buildBorder("+", "-", "+", colWidths);
        String midBorder = buildBorder("+", "-", "+", colWidths);
        String botBorder = buildBorder("+", "-", "+", colWidths);

        System.out.println(AnsiColors.CYAN + topBorder + AnsiColors.RESET);

        // Print header
        StringBuilder headerLine = new StringBuilder(AnsiColors.CYAN + "|" + AnsiColors.RESET);
        for (int i = 0; i < headers.length; i++) {
            headerLine.append(AnsiColors.BOLD)
                    .append(center(headers[i], colWidths[i]))
                    .append(AnsiColors.RESET)
                    .append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
        }
        System.out.println(headerLine);
        System.out.println(AnsiColors.CYAN + midBorder + AnsiColors.RESET);

        // Print rows
        for (Employee emp : employees) {
            StringBuilder row = new StringBuilder(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(center(String.valueOf(emp.getId()), colWidths[0])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(leftAlign(" " + nullSafeString(emp.getName()), colWidths[1])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(leftAlign(" " + nullSafeString(emp.getEmail()), colWidths[2])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(leftAlign(" " + nullSafeString(emp.getDepartment()), colWidths[3])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(leftAlign(" " + nullSafeString(emp.getDesignation()), colWidths[4])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(rightAlign(String.format("%,.2f ", emp.getSalary()), colWidths[5])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(center(nullSafeString(emp.getPhone()), colWidths[6])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            row.append(center(emp.getHireDate() != null ? emp.getHireDate().toString() : "N/A", colWidths[7])).append(AnsiColors.CYAN + "|" + AnsiColors.RESET);
            System.out.println(row);
        }

        System.out.println(AnsiColors.CYAN + botBorder + AnsiColors.RESET);
        System.out.println(AnsiColors.boldGreen("  Total Records: " + employees.size()));
    }

    /**
     * Prints detailed card view of a single employee.
     */
    public static void printEmployeeCard(Employee emp) {
        if (emp == null) {
            System.out.println(AnsiColors.yellow("  Employee not found."));
            return;
        }

        System.out.println(AnsiColors.CYAN + "  +--------------------------------------------------------+" + AnsiColors.RESET);
        System.out.printf(AnsiColors.CYAN + "  |" + AnsiColors.BOLD + " %-54s " + AnsiColors.CYAN + "|\n" + AnsiColors.RESET, "EMPLOYEE DETAILS [ID #" + emp.getId() + "]");
        System.out.println(AnsiColors.CYAN + "  +--------------------------------------------------------+" + AnsiColors.RESET);
        System.out.printf("  |  %-16s: %-36s |\n", "Full Name", emp.getName());
        System.out.printf("  |  %-16s: %-36s |\n", "Email Address", emp.getEmail());
        System.out.printf("  |  %-16s: %-36s |\n", "Department", emp.getDepartment());
        System.out.printf("  |  %-16s: %-36s |\n", "Designation", emp.getDesignation());
        System.out.printf("  |  %-16s: $%-35s |\n", "Salary", String.format("%,.2f", emp.getSalary()));
        System.out.printf("  |  %-16s: %-36s |\n", "Phone Number", emp.getPhone());
        System.out.printf("  |  %-16s: %-36s |\n", "Hire Date", emp.getHireDate() != null ? emp.getHireDate().toString() : "N/A");
        System.out.println(AnsiColors.CYAN + "  +--------------------------------------------------------+" + AnsiColors.RESET);
    }

    private static String buildBorder(String left, String mid, String right, int[] widths) {
        StringBuilder sb = new StringBuilder(left);
        for (int i = 0; i < widths.length; i++) {
            sb.append("─".repeat(widths[i]));
            if (i < widths.length - 1) {
                sb.append(mid);
            }
        }
        sb.append(right);
        return sb.toString();
    }

    private static String center(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private static String leftAlign(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        return text + " ".repeat(width - text.length());
    }

    private static String rightAlign(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        return " ".repeat(width - text.length()) + text;
    }

    private static String nullSafeString(String str) {
        return str != null ? str : "";
    }
}
