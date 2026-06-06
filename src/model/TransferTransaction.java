package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferTransaction extends Transaction {
    private final String destinationAccountId;

    public TransferTransaction(String id, String accountId, BigDecimal amount, Location location,
                               String destinationAccountId) {
        super(id, accountId, amount, location);
        this.destinationAccountId = destinationAccountId;
    }

    public TransferTransaction(String id, String accountId, BigDecimal amount, Location location,
                               LocalDateTime timestamp,
                               String destinationAccountId) {
        super(id, accountId, amount, location, timestamp);
        this.destinationAccountId = destinationAccountId;
    }

    public String getDestinationAccountId() { return destinationAccountId; }

    @Override
    public String getType() {
        return "TRANSFER";
    }
}