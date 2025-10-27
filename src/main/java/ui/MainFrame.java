package ui;

import service.AuthenticationService;
import model.User.UserType;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private AuthenticationService authService;
    private JLabel welcomeLabel;

    public MainFrame(AuthenticationService authService) {
        this.authService = authService;
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        String userName = authService.getCurrentUser().getUsername();
        UserType userType = authService.getCurrentUser().getUserType();
        welcomeLabel = new JLabel("Welcome, " + userName + " (" + userType + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel);

        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        menuPanel.setBackground(Color.WHITE);

        // Customer operations (available to employees and managers)
        if (authService.isEmployee() || authService.isManager()) {
            addMenuButton(menuPanel, "Register Customer",
                    e -> new CustomerRegistrationFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "Customer Profile",
                    e -> new CustomerProfileFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "Deposit Money",
                    e -> new DepositFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "Withdraw Money",
                    e -> new WithdrawFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "Transfer Money",
                    e -> new TransferFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "View Balance",
                    e -> new BalanceFrame(authService).setVisible(true));
            addMenuButton(menuPanel, "Transaction History",
                    e -> new TransactionHistoryFrame(authService).setVisible(true));
        }

        // Manager-only operations
        if (authService.isManager()) {
            addMenuButton(menuPanel, "Add Employee",
                    e -> new AddEmployeeFrame(authService).setVisible(true));
        }

        // Common operations
        addMenuButton(menuPanel, "Change Password", e -> changePassword());
        addMenuButton(menuPanel, "Logout", e -> logout());

        add(topPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.CENTER);
    }

    private void addMenuButton(JPanel panel, String text,
                               java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(new Color(240, 240, 240));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void setFrameProperties() {
        setTitle("Banking System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
    }

    private void changePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Old Password:", oldPasswordField,
                "New Password:", newPasswordField,
                "Confirm Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                        "New passwords do not match!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.changePassword(oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Password changed successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to change password!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
