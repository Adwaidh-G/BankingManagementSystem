package ui;

import service.AuthenticationService;
import service.AccountService;
import model.Account;
import javax.swing.*;
import java.awt.*;

public class BalanceFrame extends JFrame {
    private AccountService accountService;
    private JTextField accountNumberField;
    private JButton checkButton, closeButton;
    private JTextArea resultArea;

    public BalanceFrame(AuthenticationService authService) {
        this.accountService = new AccountService();
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        accountNumberField = new JTextField(20);
        checkButton = new JButton("Check Balance");
        closeButton = new JButton("Close");
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        checkButton.addActionListener(e -> checkBalance());
        closeButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Check Balance");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputPanel.add(new JLabel("Account Number:"));
        inputPanel.add(accountNumberField);
        inputPanel.add(checkButton);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(closeButton);

        add(titlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    private void setFrameProperties() {
        setTitle("Check Balance");
        setSize(600, 450);
        setLocationRelativeTo(null);
    }

    private void checkBalance() {
        String accountNumber = accountNumberField.getText().trim();

        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter account number!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Account account = accountService.getAccountByNumber(accountNumber);

        if (account == null) {
            resultArea.setText("Account not found!");
            return;
        }

        StringBuilder result = new StringBuilder();
        result.append("═══════════════════════════════════════════════\n");
        result.append("            ACCOUNT BALANCE INQUIRY            \n");
        result.append("═══════════════════════════════════════════════\n\n");
        result.append("Account Number    : ").append(account.getAccountNumber()).append("\n");
        result.append("Account Status    : ").append(account.getStatus()).append("\n");
        result.append("Opening Date      : ").append(account.getOpeningDate()).append("\n");
        result.append("\n");
        result.append("Current Balance   : ₹").append(account.getBalance()).append("\n");
        result.append("Available Balance : ₹").append(account.getAvailableBalance()).append("\n");
        result.append("\n");
        result.append("Daily Limit       : ₹").append(account.getDailyTransactionLimit()).append("\n");
        result.append("Monthly Limit     : ₹").append(account.getMonthlyTransactionLimit()).append("\n");
        result.append("\n═══════════════════════════════════════════════\n");

        resultArea.setText(result.toString());
    }
}
