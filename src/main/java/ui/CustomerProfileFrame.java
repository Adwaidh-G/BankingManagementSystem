package ui;

import service.AuthenticationService;
import service.CustomerService;
import model.Customer;
import javax.swing.*;
import java.awt.*;

public class CustomerProfileFrame extends JFrame {
    private CustomerService customerService;
    private JTextField customerNumberField;
    private JButton searchButton, closeButton;
    private JTextArea profileArea;

    public CustomerProfileFrame(AuthenticationService authService) {
        this.customerService = new CustomerService();
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        customerNumberField = new JTextField(20);
        searchButton = new JButton("Search");
        closeButton = new JButton("Close");
        profileArea = new JTextArea(15, 50);
        profileArea.setEditable(false);
        profileArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        searchButton.addActionListener(e -> searchCustomer());
        closeButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Customer Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        searchPanel.add(new JLabel("Customer Number:"));
        searchPanel.add(customerNumberField);
        searchPanel.add(searchButton);

        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        profilePanel.add(new JScrollPane(profileArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(closeButton);

        add(titlePanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(profilePanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    private void setFrameProperties() {
        setTitle("Customer Profile");
        setSize(700, 550);
        setLocationRelativeTo(null);
    }

    private void searchCustomer() {
        String customerNumber = customerNumberField.getText().trim();

        if (customerNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter customer number!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = customerService.getCustomerByCustomerNumber(customerNumber);

        if (customer == null) {
            profileArea.setText("Customer not found!");
            return;
        }

        StringBuilder profile = new StringBuilder();
        profile.append("═════════════════════════════════════════════════════\n");
        profile.append("                  CUSTOMER PROFILE                   \n");
        profile.append("═════════════════════════════════════════════════════\n\n");
        profile.append("Customer Number   : ").append(customer.getCustomerNumber()).append("\n");
        profile.append("Full Name         : ").append(customer.getFullName()).append("\n");
        profile.append("Date of Birth     : ").append(customer.getDateOfBirth()).append("\n");
        profile.append("Gender            : ").append(customer.getGender()).append("\n");
        profile.append("\n");
        profile.append("Contact Details:\n");
        profile.append("Address           : ").append(customer.getAddressLine1()).append("\n");
        if (customer.getAddressLine2() != null && !customer.getAddressLine2().isEmpty()) {
            profile.append("                    ").append(customer.getAddressLine2()).append("\n");
        }
        profile.append("City              : ").append(customer.getCity()).append("\n");
        profile.append("State             : ").append(customer.getState()).append("\n");
        profile.append("Postal Code       : ").append(customer.getPostalCode()).append("\n");
        profile.append("Country           : ").append(customer.getCountry()).append("\n");
        profile.append("\n");
        profile.append("Other Details:\n");
        profile.append("Occupation        : ").append(customer.getOccupation()).append("\n");
        profile.append("Annual Income     : ₹").append(customer.getAnnualIncome()).append("\n");
        profile.append("Identity Type     : ").append(customer.getIdentityType()).append("\n");
        profile.append("Identity Number   : ").append(customer.getIdentityNumber()).append("\n");
        profile.append("KYC Status        : ").append(customer.getKycStatus()).append("\n");
        profile.append("Registration Date : ").append(customer.getRegistrationDate()).append("\n");
        profile.append("\n═════════════════════════════════════════════════════\n");

        profileArea.setText(profile.toString());
    }
}
