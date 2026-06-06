package model;

import java.time.LocalDateTime;
import java.util.List;

public class FrequencyRule extends Rule {
    private final int maxTransactions;
    private final int windowMinutes;

    public FrequencyRule(String id, String name, Severity severity, int maxTransactions, int windowMinutes) {
        super(id, name, severity);
        this.maxTransactions = maxTransactions;
        this.windowMinutes = windowMinutes;
    }

    public int getMaxTransactions() { return maxTransactions; }
    public int getWindowMinutes() { return windowMinutes; }

    @Override
    public boolean matches(Transaction transaction, List<Transaction> history) {
        LocalDateTime windowStart = transaction.getTimestamp().minusMinutes(windowMinutes);
        long recentCount = history.stream()
                .filter(t -> t.getAccountId().equals(transaction.getAccountId()))
                .filter(t -> t.getTimestamp().isAfter(windowStart))
                .count();
        return recentCount > maxTransactions;
    }

    @Override
    public String describe() {
        return "More than " + maxTransactions + " transactions in " + windowMinutes + " minutes";
    }
}