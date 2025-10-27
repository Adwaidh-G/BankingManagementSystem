package ui;

import service.AuthenticationService;
import service.TransactionService;
import service.AccountService;
import model.Account;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class TransferFrame extends JFrame {
    private AuthenticationService authService;
    private TransactionService transactionService;
    private AccountService accountService;

    private JTextField fromAccountField, toAccountField;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton transferButton, cancelButton;
    private JLabel fromBalanceLabel, toBalanceLabel;

    public TransferFrame(AuthenticationService authService) {
        this.authService = authService;
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        fromAccountField = new JTextField(20);
        toAccountField = new JTextField(20);
        amountField = new JTextField(20);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        fromBalanceLabel = new JLabel("From Balance: -");
        toBalanceLabel = new JLabel("To Balance: -");

        transferButton = new JButton("Transfer");
        cancelButton = new JButton("Cancel");

        fromAccountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                checkFromBalance();
            }
        });

        toAccountField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                checkToBalance();
            }
        });

        transferButton.addActionListener(e -> performTransfer());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(102, 51, 153));
        JLabel titleLabel = new JLabel("Transfer Money");
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
        formPanel.add(new JLabel("From Account:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(fromAccountField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        fromBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(fromBalanceLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("To Account:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(toAccountField, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        toBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(toBalanceLabel, gbc);
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
        transferButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(transferButton);
        buttonPanel.add(cancelButton);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setFrameProperties() {
        setTitle("Transfer Money");
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    private void checkFromBalance() {
        String accountNumber = fromAccountField.getText().trim();
        if (!accountNumber.isEmpty()) {
            Account account = accountService.getAccountByNumber(accountNumber);
            if (account != null) {
                fromBalanceLabel.setText("From Balance: ₹" + account.getBalance());
            } else {
                fromBalanceLabel.setText("Account not found");
            }
        }
    }

    private void checkToBalance() {
        String accountNumber = toAccountField.getText().trim();
        if (!accountNumber.isEmpty()) {
            Account account = accountService.getAccountByNumber(accountNumber);
            if (account != null) {
                toBalanceLabel.setText("To Balance: ₹" + account.getBalance());
            } else {
                toBalanceLabel.setText("Account not found");
            }
        }
    }

    private void performTransfer() {
        try {
            String fromAccount = fromAccountField.getText().trim();
            String toAccount = toAccountField.getText().trim();
            String amountStr = amountField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (fromAccount.isEmpty() || toAccount.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all required fields!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account fromAcc = accountService.getAccountByNumber(fromAccount);
            Account toAcc = accountService.getAccountByNumber(toAccount);

            if (fromAcc == null || toAcc == null) {
                JOptionPane.showMessageDialog(this,
                        "One or both accounts not found!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);

            Integer employeeId = null;
            if (authService.getCurrentEmployee() != null) {
                employeeId = authService.getCurrentEmployee().getEmployeeId();
            }

            if (transactionService.transfer(fromAcc.getAccountId(),
                    toAcc.getAccountId(), amount, description, employeeId)) {
                JOptionPane.showMessageDialog(this,
                        "Transfer successful!\n" +
                                "From Account New Balance: ₹" +
                                accountService.getBalance(fromAcc.getAccountId()) + "\n" +
                                "To Account New Balance: ₹" +
                                accountService.getBalance(toAcc.getAccountId()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Transfer failed!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        fromAccountField.setText("");
        toAccountField.setText("");
        amountField.setText("");
        descriptionArea.setText("");
        fromBalanceLabel.setText("From Balance: -");
        toBalanceLabel.setText("To Balance: -");
    }
}
