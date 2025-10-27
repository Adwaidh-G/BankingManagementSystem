package service;

import dao.AccountDAO;
import model.Account;
import model.Account.AccountStatus;
import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public boolean createAccount(Account account) {
        if (account.getCustomerId() <= 0) {
            throw new IllegalArgumentException("Invalid customer ID");
        }

        if (account.getAccountTypeId() <= 0) {
            throw new IllegalArgumentException("Invalid account type");
        }

        if (account.getBranchId() <= 0) {
            throw new IllegalArgumentException("Invalid branch ID");
        }

        account.setAccountNumber(accountDAO.generateAccountNumber());

        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        account.setAvailableBalance(account.getBalance());

        return accountDAO.save(account);
    }

    public Account getAccountById(int accountId) {
        return accountDAO.findById(accountId);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    public List<Account> getCustomerAccounts(int customerId) {
        return accountDAO.findByCustomerId(customerId);
    }

    public BigDecimal getBalance(int accountId) {
        Account account = accountDAO.findById(accountId);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    public boolean freezeAccount(int accountId) {
        return accountDAO.updateStatus(accountId, AccountStatus.FROZEN);
    }

    public boolean unfreezeAccount(int accountId) {
        return accountDAO.updateStatus(accountId, AccountStatus.ACTIVE);
    }

    public boolean closeAccount(int accountId) {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            return false;
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }

        return accountDAO.updateStatus(accountId, AccountStatus.CLOSED);
    }

    public boolean isAccountActive(int accountId) {
        Account account = accountDAO.findById(accountId);
        return account != null && account.getStatus() == AccountStatus.ACTIVE;
    }
}
