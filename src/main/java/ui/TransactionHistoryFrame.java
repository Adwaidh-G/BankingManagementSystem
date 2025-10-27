package ui;

import service.AuthenticationService;
import service.TransactionService;
import service.AccountService;
import model.Account;
import model.Transaction;
import util.DateUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionHistoryFrame extends JFrame {
    private TransactionService transactionService;
    private AccountService accountService;
    private JTextField accountNumberField;
    private JButton searchButton, closeButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public TransactionHistoryFrame(AuthenticationService authService) {
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        accountNumberField = new JTextField(20);
        searchButton = new JButton("Search");
        closeButton = new JButton("Close");

        String[] columns = {"Date", "Type", "Amount", "Balance", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchButton.addActionListener(e -> searchTransactions());
        closeButton.addActionListener(e -> dispose());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        searchPanel.add(new JLabel("Account Number:"));
        searchPanel.add(accountNumberField);
        searchPanel.add(searchButton);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        closeButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(closeButton);

        add(titlePanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    private void setFrameProperties() {
        setTitle("Transaction History");
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void searchTransactions() {
        String accountNumber = accountNumberField.getText().trim();

        if (accountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter account number!", "Error",
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

        List<Transaction> transactions =
                transactionService.getAccountTransactions(account.getAccountId());

        tableModel.setRowCount(0);

        for (Transaction txn : transactions) {
            Object[] row = {
                    DateUtil.formatDateTime(txn.getTransactionDate()),
                    txn.getTransactionType(),
                    "₹" + txn.getAmount(),
                    "₹" + txn.getBalanceAfterTransaction(),
                    txn.getDescription()
            };
            tableModel.addRow(row);
        }

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No transactions found!", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
