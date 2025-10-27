package model;

import java.math.BigDecimal;

public class AccountType {
    private int typeId;
    private String typeName;
    private String typeCode;
    private String description;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private BigDecimal monthlyFee;
    private boolean isActive;

    public AccountType() {
        this.minimumBalance = BigDecimal.ZERO;
        this.interestRate = BigDecimal.ZERO;
        this.monthlyFee = BigDecimal.ZERO;
        this.isActive = true;
    }

    // Getters and Setters
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMinimumBalance() { return minimumBalance; }
    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
