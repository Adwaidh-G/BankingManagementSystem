package dao;

import db.DBConnection;
import model.Account;
import model.Account.AccountStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public Account findById(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> findByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? " +
                "ORDER BY opening_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public boolean save(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, " +
                "account_type_id, branch_id, balance, available_balance, " +
                "status, opened_by, daily_transaction_limit, " +
                "monthly_transaction_limit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setInt(2, account.getCustomerId());
            stmt.setInt(3, account.getAccountTypeId());
            stmt.setInt(4, account.getBranchId());
            stmt.setBigDecimal(5, account.getBalance());
            stmt.setBigDecimal(6, account.getAvailableBalance());
            stmt.setString(7, account.getStatus().name());
            stmt.setInt(8, account.getOpenedBy());
            stmt.setBigDecimal(9, account.getDailyTransactionLimit());
            stmt.setBigDecimal(10, account.getMonthlyTransactionLimit());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setAccountId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBalance(int accountId, java.math.BigDecimal newBalance) {
        String sql = "UPDATE accounts SET balance = ?, available_balance = ?, " +
                "last_transaction_date = CURRENT_TIMESTAMP, " +
                "updated_at = CURRENT_TIMESTAMP WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setBigDecimal(2, newBalance);
            stmt.setInt(3, accountId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int accountId, AccountStatus status) {
        String sql = "UPDATE accounts SET status = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, accountId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateAccountNumber() {
        String sql = "SELECT MAX(account_id) FROM accounts";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            return String.format("ACC%012d", nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "ACC000000000001";
        }
    }

    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountTypeId(rs.getInt("account_type_id"));
        account.setBranchId(rs.getInt("branch_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setAvailableBalance(rs.getBigDecimal("available_balance"));
        account.setOpeningDate(rs.getDate("opening_date").toLocalDate());

        Date closingDate = rs.getDate("closing_date");
        if (closingDate != null) {
            account.setClosingDate(closingDate.toLocalDate());
        }

        account.setStatus(AccountStatus.valueOf(rs.getString("status")));
        account.setOpenedBy(rs.getInt("opened_by"));

        Timestamp lastTransaction = rs.getTimestamp("last_transaction_date");
        if (lastTransaction != null) {
            account.setLastTransactionDate(lastTransaction.toLocalDateTime());
        }

        account.setDailyTransactionLimit(rs.getBigDecimal("daily_transaction_limit"));
        account.setMonthlyTransactionLimit(rs.getBigDecimal("monthly_transaction_limit"));

        return account;
    }
}
