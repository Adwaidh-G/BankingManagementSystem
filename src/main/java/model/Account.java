package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Account {
    private int accountId;
    private String accountNumber;
    private int customerId;
    private int accountTypeId;
    private int branchId;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private AccountStatus status;
    private int openedBy;
    private LocalDateTime lastTransactionDate;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal monthlyTransactionLimit;

    public enum AccountStatus { ACTIVE, INACTIVE, FROZEN, CLOSED }

    public Account() {
        this.balance = BigDecimal.ZERO;
        this.availableBalance = BigDecimal.ZERO;
        this.openingDate = LocalDate.now();
        this.status = AccountStatus.ACTIVE;
        this.dailyTransactionLimit = new BigDecimal("50000.00");
        this.monthlyTransactionLimit = new BigDecimal("1000000.00");
    }

    // Business methods
    public boolean canDebit(BigDecimal amount) {
        return balance.compareTo(amount) >= 0 && status == AccountStatus.ACTIVE;
    }

    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
            this.availableBalance = this.availableBalance.add(amount);
            this.lastTransactionDate = LocalDateTime.now();
        }
    }

    public boolean debit(BigDecimal amount) {
        if (canDebit(amount)) {
            this.balance = this.balance.subtract(amount);
            this.availableBalance = this.availableBalance.subtract(amount);
            this.lastTransactionDate = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getAccountTypeId() { return accountTypeId; }
    public void setAccountTypeId(int accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public LocalDate getOpeningDate() { return openingDate; }
    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public LocalDate getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public int getOpenedBy() { return openedBy; }
    public void setOpenedBy(int openedBy) { this.openedBy = openedBy; }

    public LocalDateTime getLastTransactionDate() { return lastTransactionDate; }
    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public BigDecimal getDailyTransactionLimit() { return dailyTransactionLimit; }
    public void setDailyTransactionLimit(BigDecimal dailyTransactionLimit) {
        this.dailyTransactionLimit = dailyTransactionLimit;
    }

    public BigDecimal getMonthlyTransactionLimit() {
        return monthlyTransactionLimit;
    }
    public void setMonthlyTransactionLimit(BigDecimal monthlyTransactionLimit) {
        this.monthlyTransactionLimit = monthlyTransactionLimit;
    }
}
