package service;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;
import model.Transaction;
import model.Transaction.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import db.DBConnection;

public class TransactionService {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public TransactionService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public boolean deposit(int accountId, BigDecimal amount,
                           String description, Integer processedBy) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findById(accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found");
            }

            if (!account.isActive()) {
                throw new IllegalStateException("Account is not active");
            }

            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            accountDAO.updateBalance(accountId, newBalance);

            Transaction transaction = new Transaction();
            transaction.setTransactionReference(
                    transactionDAO.generateTransactionReference());
            transaction.setToAccountId(accountId);
            transaction.setTransactionType(TransactionType.DEPOSIT);
            transaction.setAmount(amount);
            transaction.setBalanceAfterTransaction(newBalance);
            transaction.setDescription(description != null ? description : "Deposit");
            transaction.setProcessedBy(processedBy);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setChannel("BRANCH");

            transactionDAO.save(transaction);

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean withdraw(int accountId, BigDecimal amount,
                            String description, Integer processedBy) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Account account = accountDAO.findById(accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found");
            }

            if (!account.isActive()) {
                throw new IllegalStateException("Account is not active");
            }

            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }

            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);
            accountDAO.updateBalance(accountId, newBalance);

            Transaction transaction = new Transaction();
            transaction.setTransactionReference(
                    transactionDAO.generateTransactionReference());
            transaction.setFromAccountId(accountId);
            transaction.setTransactionType(TransactionType.WITHDRAWAL);
            transaction.setAmount(amount);
            transaction.setBalanceAfterTransaction(newBalance);
            transaction.setDescription(description != null ? description : "Withdrawal");
            transaction.setProcessedBy(processedBy);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setChannel("BRANCH");

            transactionDAO.save(transaction);

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean transfer(int fromAccountId, int toAccountId,
                            BigDecimal amount, String description,
                            Integer processedBy) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Account fromAccount = accountDAO.findById(fromAccountId);
            Account toAccount = accountDAO.findById(toAccountId);

            if (fromAccount == null || toAccount == null) {
                throw new IllegalArgumentException("Account not found");
            }

            if (!fromAccount.isActive() || !toAccount.isActive()) {
                throw new IllegalStateException("Account is not active");
            }

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }

            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            accountDAO.updateBalance(fromAccountId, fromNewBalance);

            BigDecimal toNewBalance = toAccount.getBalance().add(amount);
            accountDAO.updateBalance(toAccountId, toNewBalance);

            Transaction transaction = new Transaction();
            transaction.setTransactionReference(
                    transactionDAO.generateTransactionReference());
            transaction.setFromAccountId(fromAccountId);
            transaction.setToAccountId(toAccountId);
            transaction.setTransactionType(TransactionType.TRANSFER);
            transaction.setAmount(amount);
            transaction.setBalanceAfterTransaction(fromNewBalance);
            transaction.setDescription(description != null ? description : "Transfer");
            transaction.setProcessedBy(processedBy);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setChannel("BRANCH");

            transactionDAO.save(transaction);

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public java.util.List<Transaction> getAccountTransactions(int accountId) {
        return transactionDAO.findByAccountId(accountId);
    }

    public Transaction getTransactionById(int transactionId) {
        return transactionDAO.findById(transactionId);
    }

    public Transaction getTransactionByReference(String reference) {
        return transactionDAO.findByReference(reference);
    }
}
