package ui;

import service.AuthenticationService;
import service.TransactionService;
import service.AccountService;
import model.Account;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DepositFrame extends JFrame {
    private AuthenticationService authService;
    private TransactionService transactionService;
    private AccountService accountService;

    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton depositButton, cancelButton;
    private JLabel balanceLabel;

    public DepositFrame(AuthenticationService authService) {
        this.authService = authService;
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        accountNumberField = new JTextField(20);
        amountField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        balanceLabel = new JLabel("Current Balance: -");

        depositButton = new JButton("Deposit");
        cancelButton = new JButton("Cancel");

        accountNumberField.addActionListener(e -> checkBalance());
        depositButton.addActionListener(e -> performDeposit());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 153, 0));
        JLabel titleLabel = new JLabel("Deposit Money");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(accountNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(balanceLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        depositButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(depositButton);
        buttonPanel.add(cancelButton);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setFrameProperties() {
        setTitle("Deposit Money");
        setSize(500, 450);
        setLocationRelativeTo(null);
    }

    private void checkBalance() {
        String accountNumber = accountNumberField.getText().trim();
        if (accountNumber.isEmpty()) return;

        Account account = accountService.getAccountByNumber(accountNumber);
        if (account != null) {
            balanceLabel.setText("Current Balance: ₹" + account.getBalance());
        } else {
            balanceLabel.setText("Account not found");
        }
    }

    private void performDeposit() {
        try {
            String accountNumber = accountNumberField.getText().trim();
            String amountStr = amountField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (accountNumber.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all required fields!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account account = accountService.getAccountByNumber(accountNumber);
            if (account == null) {
                JOptionPane.showMessageDialog(this,
                        "Account not found!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);

            Integer employeeId = null;
            if (authService.getCurrentEmployee() != null) {
                employeeId = authService.getCurrentEmployee().getEmployeeId();
            }

            if (transactionService.deposit(account.getAccountId(), amount,
                    description, employeeId)) {
                JOptionPane.showMessageDialog(this,
                        "Deposit successful!\nNew Balance: ₹" +
                                accountService.getBalance(account.getAccountId()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Deposit failed!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid amount!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        accountNumberField.setText("");
        amountField.setText("");
        descriptionArea.setText("");
        balanceLabel.setText("Current Balance: -");
    }
}
