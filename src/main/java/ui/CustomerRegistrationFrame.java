package ui;

import service.AuthenticationService;
import service.CustomerService;
import model.User;
import model.Customer;
import model.Customer.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.math.BigDecimal;

public class CustomerRegistrationFrame extends JFrame {
    private AuthenticationService authService;
    private CustomerService customerService;

    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextField firstNameField, lastNameField;
    private JTextField dobField, addressLine1Field, addressLine2Field;
    private JTextField cityField, stateField, postalCodeField;
    private JTextField occupationField, annualIncomeField;
    private JTextField identityNumberField;
    private JComboBox<Gender> genderCombo;
    private JComboBox<IdentityType> identityTypeCombo;
    private JButton registerButton, cancelButton;

    public CustomerRegistrationFrame(AuthenticationService authService) {
        this.authService = authService;
        this.customerService = new CustomerService();
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
        dobField = new JTextField(20);
        dobField.setToolTipText("Format: YYYY-MM-DD");
        addressLine1Field = new JTextField(20);
        addressLine2Field = new JTextField(20);
        cityField = new JTextField(20);
        stateField = new JTextField(20);
        postalCodeField = new JTextField(20);
        occupationField = new JTextField(20);
        annualIncomeField = new JTextField(20);
        identityNumberField = new JTextField(20);

        genderCombo = new JComboBox<>(Gender.values());
        identityTypeCombo = new JComboBox<>(IdentityType.values());

        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        registerButton.addActionListener(e -> registerCustomer());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Customer Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addSectionLabel(formPanel, gbc, row++, "User Account Information");
        addFormField(formPanel, gbc, row++, "Username:", usernameField);
        addFormField(formPanel, gbc, row++, "Email:", emailField);
        addFormField(formPanel, gbc, row++, "Phone:", phoneField);
        addFormField(formPanel, gbc, row++, "Password:", passwordField);
        addFormField(formPanel, gbc, row++, "Confirm Password:", confirmPasswordField);

        row++;
        addSectionLabel(formPanel, gbc, row++, "Personal Information");
        addFormField(formPanel, gbc, row++, "First Name:", firstNameField);
        addFormField(formPanel, gbc, row++, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, row++, "Date of Birth:", dobField);
        addFormField(formPanel, gbc, row++, "Gender:", genderCombo);

        row++;
        addSectionLabel(formPanel, gbc, row++, "Address Information");
        addFormField(formPanel, gbc, row++, "Address Line 1:", addressLine1Field);
        addFormField(formPanel, gbc, row++, "Address Line 2:", addressLine2Field);
        addFormField(formPanel, gbc, row++, "City:", cityField);
        addFormField(formPanel, gbc, row++, "State:", stateField);
        addFormField(formPanel, gbc, row++, "Postal Code:", postalCodeField);

        row++;
        addSectionLabel(formPanel, gbc, row++, "Other Information");
        addFormField(formPanel, gbc, row++, "Occupation:", occupationField);
        addFormField(formPanel, gbc, row++, "Annual Income:", annualIncomeField);
        addFormField(formPanel, gbc, row++, "Identity Type:", identityTypeCombo);
        addFormField(formPanel, gbc, row++, "Identity Number:", identityNumberField);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        registerButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc,
                                 int row, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(0, 102, 204));
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
        setTitle("Register New Customer");
        setSize(600, 700);
        setLocationRelativeTo(null);
    }

    private void registerCustomer() {
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

            Customer customer = new Customer();
            customer.setFirstName(firstNameField.getText().trim());
            customer.setLastName(lastNameField.getText().trim());
            customer.setDateOfBirth(LocalDate.parse(dobField.getText().trim()));
            customer.setGender((Gender) genderCombo.getSelectedItem());
            customer.setAddressLine1(addressLine1Field.getText().trim());
            customer.setAddressLine2(addressLine2Field.getText().trim());
            customer.setCity(cityField.getText().trim());
            customer.setState(stateField.getText().trim());
            customer.setPostalCode(postalCodeField.getText().trim());
            customer.setOccupation(occupationField.getText().trim());
            customer.setAnnualIncome(new BigDecimal(annualIncomeField.getText().trim()));
            customer.setIdentityType((IdentityType) identityTypeCombo.getSelectedItem());
            customer.setIdentityNumber(identityNumberField.getText().trim());

            if (customerService.registerCustomer(user, customer)) {
                JOptionPane.showMessageDialog(this,
                        "Customer registered successfully!\nCustomer Number: " +
                                customer.getCustomerNumber(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to register customer!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
