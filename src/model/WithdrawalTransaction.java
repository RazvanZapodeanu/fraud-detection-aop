package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalTransaction extends Transaction {
    private final String atmId;

    public WithdrawalTransaction(String id, String accountId, BigDecimal amount, Location location,
                                 String atmId) {
        super(id, accountId, amount, location);
        this.atmId = atmId;
    }

    public WithdrawalTransaction(String id, String accountId, BigDecimal amount, Location location,
                                 LocalDateTime timestamp,
                                 String atmId) {
        super(id, accountId, amount, location, timestamp);
        this.atmId = atmId;
    }

    public String getAtmId() { return atmId; }

    @Override
    public String getType() {
        return "WITHDRAWAL";
    }
}