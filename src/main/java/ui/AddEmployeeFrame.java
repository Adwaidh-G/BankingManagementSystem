package ui;

import service.AuthenticationService;
import service.EmployeeService;
import model.User;
import model.Employee;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.math.BigDecimal;

public class AddEmployeeFrame extends JFrame {
    private AuthenticationService authService;
    private EmployeeService employeeService;

    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField firstNameField, lastNameField;
    private JTextField positionField, departmentField;
    private JTextField hireDateField, salaryField;
    private JTextField branchIdField;
    private JCheckBox managerCheckBox;
    private JButton addButton, cancelButton;

    public AddEmployeeFrame(AuthenticationService authService) {
        this.authService = authService;
        this.employeeService = new EmployeeService();

        if (!authService.isManager()) {
            JOptionPane.showMessageDialog(null,
                    "Only managers can add employees!", "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        positionField = new JTextField(20);
        departmentField = new JTextField(20);
        hireDateField = new JTextField(20);
        hireDateField.setText(LocalDate.now().toString());
        salaryField = new JTextField(20);
        branchIdField = new JTextField(20);
        branchIdField.setText("1");
        managerCheckBox = new JCheckBox("Is Manager");

        addButton = new JButton("Add Employee");
        cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> addEmployee());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(204, 0, 0));
        JLabel titleLabel = new JLabel("Add New Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addSectionLabel(formPanel, gbc, row++, "User Account");
        addFormField(formPanel, gbc, row++, "Username:", usernameField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Phone:", phoneField);
        addFormField(formPanel, gbc, row++, "Password:", passwordField);
        addFormField(formPanel, gbc, row++, "Confirm Password:", confirmPasswordField);

        row++;
        addSectionLabel(formPanel, gbc, row++, "Employee Details");
        addFormField(formPanel, gbc, row++, "First Name:", firstNameField);
        addFormField(formPanel, gbc, row++, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, row++, "Position:", positionField);
        addFormField(formPanel, gbc, row++, "Department:", departmentField);
        addFormField(formPanel, gbc, row++, "Hire Date:", hireDateField);
        addFormField(formPanel, gbc, row++, "Salary:", salaryField);
        addFormField(formPanel, gbc, row++, "Branch ID:", branchIdField);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formPanel.add(managerCheckBox, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton.setPreferredSize(new Dimension(150, 35));
        cancelButton.setPreferredSize(new Dimension(150, 35));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc,
                                 int row, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(204, 0, 0));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(label, gbc);
        gbc.gridwidth = 1;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc,
                              int row, String label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private void setFrameProperties() {
        setTitle("Add Employee (Manager Only)");
        setSize(550, 650);
        setLocationRelativeTo(null);
    }

    private void addEmployee() {
        try {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setUsername(usernameField.getText().trim());
            user.setPasswordHash(password);
            user.setEmail(emailField.getText().trim());
            user.setPhone(phoneField.getText().trim());

            Employee employee = new Employee();
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            employee.setDepartment(departmentField.getText().trim());
            employee.setHireDate(LocalDate.parse(hireDateField.getText().trim()));
            employee.setSalary(new BigDecimal(salaryField.getText().trim()));
            employee.setBranchId(Integer.parseInt(branchIdField.getText().trim()));

            boolean isManager = managerCheckBox.isSelected();

            if (employeeService.addEmployee(user, employee, isManager)) {
                JOptionPane.showMessageDialog(this,
                        "Employee added successfully!\nEmployee Code: " +
                                employee.getEmployeeCode(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add employee!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
