package model;

import java.math.BigDecimal;
import java.util.List;

public class AmountRule extends Rule {
    private final BigDecimal threshold;

    public AmountRule(String id, String name, Severity severity, BigDecimal threshold) {
        super(id, name, severity);
        this.threshold = threshold;
    }

    public BigDecimal getThreshold() { return threshold; }

    @Override
    public boolean matches(Transaction transaction, List<Transaction> history) {
        return transaction.getAmount().compareTo(threshold) > 0;
    }

    @Override
    public String describe() {
        return "Amount exceeds " + threshold;
    }
}