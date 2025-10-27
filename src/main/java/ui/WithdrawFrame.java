package ui;

import service.AuthenticationService;
import service.TransactionService;
import service.AccountService;
import model.Account;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class WithdrawFrame extends JFrame {
    private AuthenticationService authService;
    private TransactionService transactionService;
    private AccountService accountService;

    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton withdrawButton, cancelButton;
    private JLabel balanceLabel;

    public WithdrawFrame(AuthenticationService authService) {
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

        withdrawButton = new JButton("Withdraw");
        cancelButton = new JButton("Cancel");

        accountNumberField.addActionListener(e -> checkBalance());
        withdrawButton.addActionListener(e -> performWithdrawal());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(204, 51, 0));
        JLabel titleLabel = new JLabel("Withdraw Money");
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
        withdrawButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(withdrawButton);
        buttonPanel.add(cancelButton);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setFrameProperties() {
        setTitle("Withdraw Money");
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

    private void performWithdrawal() {
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

            if (transactionService.withdraw(account.getAccountId(), amount,
                    description, employeeId)) {
                JOptionPane.showMessageDialog(this,
                        "Withdrawal successful!\nNew Balance: ₹" +
                                accountService.getBalance(account.getAccountId()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Withdrawal failed!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Error",
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
