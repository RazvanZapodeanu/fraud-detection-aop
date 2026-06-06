package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Account {
    private final String id;
    private final String customerId;
    private BigDecimal balance;
    private final LocalDateTime openedAt;

    protected Account(String id, String customerId, BigDecimal balance) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.openedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getOpenedAt() { return openedAt; }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public abstract String getAccountType();

    @Override
    public String toString() {
        return getAccountType() + " " + id + " [balance=" + balance + "]";
    }
}