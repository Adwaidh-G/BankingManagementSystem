package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private String transactionReference;
    private Integer fromAccountId;
    private Integer toAccountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;
    private String description;
    private LocalDateTime transactionDate;
    private Integer processedBy;
    private TransactionStatus transactionStatus;
    private String channel;
    private BigDecimal feesCharged;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, FEE, INTEREST
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }

    public Transaction() {
        this.transactionDate = LocalDateTime.now();
        this.transactionStatus = TransactionStatus.PENDING;
        this.feesCharged = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Integer getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() { return toAccountId; }
    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }
    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getProcessedBy() { return processedBy; }
    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }

    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public BigDecimal getFeesCharged() { return feesCharged; }
    public void setFeesCharged(BigDecimal feesCharged) {
        this.feesCharged = feesCharged;
    }
}
