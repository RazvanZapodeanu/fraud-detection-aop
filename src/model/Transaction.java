package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Transaction {
    private final String id;
    private final String accountId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final Location location;

    protected Transaction(String id, String accountId, BigDecimal amount, Location location) {
        this(id, accountId, amount, location, LocalDateTime.now());
    }

    protected Transaction(String id, String accountId, BigDecimal amount, Location location,
                          LocalDateTime timestamp) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.location = location;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Location getLocation() { return location; }

    public abstract String getType();

    @Override
    public String toString() {
        return getType() + " " + id + " amount=" + amount + " at " + location + " on " + timestamp;
    }
}