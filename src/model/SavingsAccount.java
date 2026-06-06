package model;

import java.math.BigDecimal;

public class SavingsAccount extends Account {
    private final BigDecimal interestRate;

    public SavingsAccount(String id, String customerId, BigDecimal balance, BigDecimal interestRate) {
        super(id, customerId, balance);
        this.interestRate = interestRate;
    }

    public BigDecimal getInterestRate() { return interestRate; }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}