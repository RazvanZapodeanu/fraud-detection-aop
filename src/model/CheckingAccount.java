package model;

import java.math.BigDecimal;

public class CheckingAccount extends Account {
    private final BigDecimal overdraftLimit;

    public CheckingAccount(String id, String customerId, BigDecimal balance, BigDecimal overdraftLimit) {
        super(id, customerId, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public BigDecimal getOverdraftLimit() { return overdraftLimit; }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}