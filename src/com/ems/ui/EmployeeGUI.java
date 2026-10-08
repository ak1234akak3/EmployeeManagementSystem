package com.ems.ui;

import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import com.ems.service.EmployeeService.ServiceResult;
import com.ems.service.EmployeeService.Statistics;
import com.ems.util.DBConnection;
import com.ems.util.DatabaseInitializer;
import com.ems.util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Modern Swing Graphical User Interface for the Employee Management System.
 */
public class EmployeeGUI extends JFrame {

    private final EmployeeService employeeService;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchCategoryCombo;
    private JLabel statusLabel;
    private JLabel statsBadge;

    public EmployeeGUI() {
        this.employeeService = new EmployeeService();
        initializeLookAndFeel();
        initializeDatabaseAsync();
        initializeUI();
        loadEmployeeData();
    }

    private void initializeLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Nimbus fallback to system default
        }
    }

    private void initializeDatabaseAsync() {
        SwingUtilities.invokeLater(() -> {
            boolean ready = DatabaseInitializer.initializeDatabase();
            updateStatus(ready ? "Connected to MySQL (" + DBConnection.getDbName() + ")" : "Database Offline - check settings", ready);
        });
    }

    private void initializeUI() {
        setTitle("Employee Management System - Enterprise Edition");
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(new Color(245, 247, 250));

        // 1. Top Header & Search Panel
        JPanel topContainer = new JPanel(new BorderLayout(5, 10));
        topContainer.setOpaque(false);

        // Header Title Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🏢 Employee Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        statsBadge = new JLabel("Loading records...");
        statsBadge.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsBadge.setForeground(new Color(203, 213, 225));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsBadge, BorderLayout.EAST);

        // Search & Filter Toolbar
        JPanel searchToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchToolbar.setBackground(Color.WHITE);
        searchToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel searchIcon = new JLabel("🔍 Search:");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Type keyword to search...");
        searchField.addActionListener(e -> performSearch());

        searchCategoryCombo = new JComboBox<>(new String[]{"All Fields / Name", "Department", "Designation", "Salary Range (min-max)"});
        searchCategoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton searchBtn = createStyledButton("Search", new Color(59, 130, 246), Color.WHITE);
        searchBtn.addActionListener(e -> performSearch());

        JButton resetBtn = createStyledButton("Reset", new Color(100, 116, 139), Color.WHITE);
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadEmployeeData();
        });

        searchToolbar.add(searchIcon);
        searchToolbar.add(searchField);
        searchToolbar.add(searchCategoryCombo);
        searchToolbar.add(searchBtn);
        searchToolbar.add(resetBtn);

        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(searchToolbar, BorderLayout.SOUTH);
        mainPanel.add(topContainer, BorderLayout.NORTH);

        // 2. Center Table
        String[] columnNames = {"ID", "Name", "Email", "Department", "Designation", "Salary ($)", "Phone", "Hire Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        employeeTable.setRowHeight(28);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setGridColor(new Color(241, 245, 249));
        employeeTable.setShowGrid(true);

        // Styling Table Header
        JTableHeader tableHeader = employeeTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(new Color(51, 65, 85));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 35));

        // Center align ID & Hire Date, Right align Salary
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        employeeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        employeeTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        employeeTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. Right Action Buttons Panel
        JPanel actionPanel = new JPanel(new GridLayout(7, 1, 8, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(0, 8, 0, 0));

        JButton addBtn = createStyledButton("➕ Add Employee", new Color(16, 185, 129), Color.WHITE);
        addBtn.addActionListener(e -> openAddEmployeeDialog());

        JButton editBtn = createStyledButton("✏️ Edit Employee", new Color(245, 158, 11), Color.WHITE);
        editBtn.addActionListener(e -> openEditEmployeeDialog());

        JButton deleteBtn = createStyledButton("🗑️ Delete Employee", new Color(239, 68, 68), Color.WHITE);
        deleteBtn.addActionListener(e -> handleDeleteEmployee());

        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(99, 102, 241), Color.WHITE);
        refreshBtn.addActionListener(e -> loadEmployeeData());

        JButton statsBtn = createStyledButton("📊 Analytics", new Color(139, 92, 246), Color.WHITE);
        statsBtn.addActionListener(e -> showAnalyticsDialog());

        JButton exportBtn = createStyledButton("📤 Export CSV", new Color(75, 85, 99), Color.WHITE);
        exportBtn.addActionListener(e -> exportToCsv());

        JButton dbSettingsBtn = createStyledButton("⚙️ DB Settings", new Color(107, 114, 128), Color.WHITE);
        dbSettingsBtn.addActionListener(e -> openDbConfigDialog());

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        actionPanel.add(statsBtn);
        actionPanel.add(exportBtn);
        actionPanel.add(dbSettingsBtn);

        mainPanel.add(actionPanel, BorderLayout.EAST);

        // 4. Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        statusLabel = new JLabel("System Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel infoLabel = new JLabel("Java 25 • MySQL JDBC Client");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(infoLabel, BorderLayout.EAST);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<Employee> list = employeeService.getAllEmployees();
        for (Employee emp : list) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getDepartment(),
                    emp.getDesignation(),
                    String.format("%,.2f", emp.getSalary()),
                    emp.getPhone(),
                    emp.getHireDate() != null ? emp.getHireDate().toString() : ""
            });
        }
        statsBadge.setText("Total Employees: " + list.size());
        updateStatus("Loaded " + list.size() + " employee records from database.", true);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        int category = searchCategoryCombo.getSelectedIndex();
        List<Employee> results;

        if (keyword.isEmpty()) {
            loadEmployeeData();
            return;
        }

        switch (category) {
            case 1 -> results = employeeService.searchByDepartment(keyword);
            case 2 -> results = employeeService.searchByDesignation(keyword);
            case 3 -> {
                // Parse min-max salary
                String[] parts = keyword.split("[-:, ]+");
                try {
                    double min = parts.length > 0 ? Double.parseDouble(parts[0]) : 0;
                    double max = parts.length > 1 ? Double.parseDouble(parts[1]) : 1_000_000;
                    results = employeeService.searchBySalaryRange(min, max);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter salary range like: 50000-100000", "Format Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            default -> results = employeeService.searchByName(keyword);
        }

        tableModel.setRowCount(0);
        for (Employee emp : results) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getDepartment(),
                    emp.getDesignation(),
                    String.format("%,.2f", emp.getSalary()),
                    emp.getPhone(),
                    emp.getHireDate() != null ? emp.getHireDate().toString() : ""
            });
        }
        updateStatus("Found " + results.size() + " matches for query: '" + keyword + "'", true);
    }

    private void openAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 8, 12));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField desigField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email Address:"));
        panel.add(emailField);
        panel.add(new JLabel("Department:"));
        panel.add(deptField);
        panel.add(new JLabel("Designation:"));
        panel.add(desigField);
        panel.add(new JLabel("Salary ($):"));
        panel.add(salaryField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        panel.add(dateField);

        JButton saveBtn = createStyledButton("Save Employee", new Color(16, 185, 129), Color.WHITE);
        JButton cancelBtn = createStyledButton("Cancel", new Color(100, 116, 139), Color.WHITE);

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String dept = deptField.getText().trim();
                String desig = desigField.getText().trim();
                double salary = Double.parseDouble(salaryField.getText().trim());
                String phone = phoneField.getText().trim();
                LocalDate hireDate = InputValidator.parseDate(dateField.getText().trim());

                Employee emp = new Employee(name, email, dept, desig, salary, phone, hireDate);
                ServiceResult<Employee> res = employeeService.addEmployee(emp);

                if (res.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, res.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(dialog, res.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid salary value. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void openEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee row from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Employee emp = employeeService.getEmployeeById(id);
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Employee not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit Employee #" + id, true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 8, 12));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JTextField nameField = new JTextField(emp.getName());
        JTextField emailField = new JTextField(emp.getEmail());
        JTextField deptField = new JTextField(emp.getDepartment());
        JTextField desigField = new JTextField(emp.getDesignation());
        JTextField salaryField = new JTextField(String.valueOf(emp.getSalary()));
        JTextField phoneField = new JTextField(emp.getPhone());
        JTextField dateField = new JTextField(emp.getHireDate() != null ? emp.getHireDate().toString() : "");

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email Address:"));
        panel.add(emailField);
        panel.add(new JLabel("Department:"));
        panel.add(deptField);
        panel.add(new JLabel("Designation:"));
        panel.add(desigField);
        panel.add(new JLabel("Salary ($):"));
        panel.add(salaryField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        panel.add(dateField);

        JButton saveBtn = createStyledButton("Update Employee", new Color(245, 158, 11), Color.WHITE);
        JButton cancelBtn = createStyledButton("Cancel", new Color(100, 116, 139), Color.WHITE);

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String dept = deptField.getText().trim();
                String desig = desigField.getText().trim();
                double salary = Double.parseDouble(salaryField.getText().trim());
                String phone = phoneField.getText().trim();
                LocalDate hireDate = InputValidator.parseDate(dateField.getText().trim());

                Employee updated = new Employee(id, name, email, dept, desig, salary, phone, hireDate);
                ServiceResult<Employee> res = employeeService.updateEmployee(updated);

                if (res.isSuccess()) {
                    JOptionPane.showMessageDialog(dialog, res.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(dialog, res.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid salary value. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(saveBtn);
        panel.add(cancelBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void handleDeleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee row to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete Employee #" + id + " (" + name + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            ServiceResult<Boolean> res = employeeService.deleteEmployee(id);
            if (res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage(), "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this, res.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAnalyticsDialog() {
        Statistics stats = employeeService.getStatistics();
        StringBuilder sb = new StringBuilder();
        sb.append("📊 Summary Analytics:\n");
        sb.append("──────────────────────────────────────────\n");
        sb.append(String.format("• Total Active Employees : %d\n", stats.totalEmployees));
        sb.append(String.format("• Average Salary         : $%,.2f\n", stats.averageSalary));
        sb.append(String.format("• Lowest Salary          : $%,.2f\n", stats.minSalary));
        sb.append(String.format("• Highest Salary         : $%,.2f\n\n", stats.maxSalary));
        sb.append("📁 Department Distribution:\n");
        for (Map.Entry<String, Long> entry : stats.departmentDistribution.entrySet()) {
            sb.append(String.format("  - %-20s : %d\n", entry.getKey(), entry.getValue()));
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setOpaque(false);

        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Employee Analytics & Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("employees_export.csv"));
        int choice = fileChooser.showSaveDialog(this);

        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<Employee> all = employeeService.getAllEmployees();

            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("ID,Name,Email,Department,Designation,Salary,Phone,HireDate");
                for (Employee emp : all) {
                    pw.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\"\n",
                            emp.getId(),
                            emp.getName().replace("\"", "\"\""),
                            emp.getEmail().replace("\"", "\"\""),
                            emp.getDepartment().replace("\"", "\"\""),
                            emp.getDesignation().replace("\"", "\"\""),
                            emp.getSalary(),
                            emp.getPhone(),
                            emp.getHireDate() != null ? emp.getHireDate().toString() : ""
                    );
                }
                JOptionPane.showMessageDialog(this, "Exported " + all.size() + " records to " + file.getName(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openDbConfigDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        JTextField hostField = new JTextField(DBConnection.getHost());
        JTextField portField = new JTextField(DBConnection.getPort());
        JTextField dbField = new JTextField(DBConnection.getDbName());
        JTextField userField = new JTextField(DBConnection.getUsername());
        JPasswordField passField = new JPasswordField(DBConnection.getPassword());

        panel.add(new JLabel("MySQL Host:"));
        panel.add(hostField);
        panel.add(new JLabel("MySQL Port:"));
        panel.add(portField);
        panel.add(new JLabel("Database Name:"));
        panel.add(dbField);
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "MySQL Database Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            DBConnection.setCredentials(
                    hostField.getText().trim(),
                    portField.getText().trim(),
                    dbField.getText().trim(),
                    userField.getText().trim(),
                    new String(passField.getPassword())
            );

            boolean ready = DatabaseInitializer.initializeDatabase();
            if (ready) {
                JOptionPane.showMessageDialog(this, "Connected successfully to MySQL database!", "Connection Established", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this, "Could not connect to MySQL with provided settings.", "Connection Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? new Color(22, 101, 52) : new Color(185, 28, 28));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 34));
        return btn;
    }
}
