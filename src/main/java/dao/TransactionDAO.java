package dao;

import db.DBConnection;
import model.Transaction;
import model.Transaction.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public Transaction findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Transaction findByReference(String reference) {
        String sql = "SELECT * FROM transactions WHERE transaction_reference = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reference);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Transaction> findByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE from_account_id = ? " +
                "OR to_account_id = ? ORDER BY transaction_date DESC LIMIT 100";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public boolean save(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_reference, from_account_id, " +
                "to_account_id, transaction_type, amount, balance_after_transaction, " +
                "description, processed_by, transaction_status, channel, fees_charged) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, transaction.getTransactionReference());

            if (transaction.getFromAccountId() != null) {
                stmt.setInt(2, transaction.getFromAccountId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (transaction.getToAccountId() != null) {
                stmt.setInt(3, transaction.getToAccountId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, transaction.getTransactionType().name());
            stmt.setBigDecimal(5, transaction.getAmount());
            stmt.setBigDecimal(6, transaction.getBalanceAfterTransaction());
            stmt.setString(7, transaction.getDescription());

            if (transaction.getProcessedBy() != null) {
                stmt.setInt(8, transaction.getProcessedBy());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, transaction.getTransactionStatus().name());
            stmt.setString(10, transaction.getChannel());
            stmt.setBigDecimal(11, transaction.getFeesCharged());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateTransactionReference() {
        String sql = "SELECT MAX(transaction_id) FROM transactions";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            long timestamp = System.currentTimeMillis();
            return String.format("TXN%d%08d", timestamp, nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "TXN" + System.currentTimeMillis();
        }
    }

    private Transaction extractTransactionFromResultSet(ResultSet rs)
            throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setTransactionReference(rs.getString("transaction_reference"));

        int fromAccountId = rs.getInt("from_account_id");
        if (!rs.wasNull()) {
            transaction.setFromAccountId(fromAccountId);
        }

        int toAccountId = rs.getInt("to_account_id");
        if (!rs.wasNull()) {
            transaction.setToAccountId(toAccountId);
        }

        transaction.setTransactionType(
                TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceAfterTransaction(
                rs.getBigDecimal("balance_after_transaction"));
        transaction.setDescription(rs.getString("description"));
        transaction.setTransactionDate(
                rs.getTimestamp("transaction_date").toLocalDateTime());

        int processedBy = rs.getInt("processed_by");
        if (!rs.wasNull()) {
            transaction.setProcessedBy(processedBy);
        }

        transaction.setTransactionStatus(
                TransactionStatus.valueOf(rs.getString("transaction_status")));
        transaction.setChannel(rs.getString("channel"));
        transaction.setFeesCharged(rs.getBigDecimal("fees_charged"));

        return transaction;
    }
}
