package model;

import java.time.LocalDateTime;
import java.util.List;

public class LocationRule extends Rule {
    private final int impossibleTravelMinutes;

    public LocationRule(String id, String name, Severity severity, int impossibleTravelMinutes) {
        super(id, name, severity);
        this.impossibleTravelMinutes = impossibleTravelMinutes;
    }

    public int getImpossibleTravelMinutes() { return impossibleTravelMinutes; }

    @Override
    public boolean matches(Transaction transaction, List<Transaction> history) {
        LocalDateTime windowStart = transaction.getTimestamp().minusMinutes(impossibleTravelMinutes);
        String currentCountry = transaction.getLocation().getCountry();

        return history.stream()
                .filter(t -> t.getAccountId().equals(transaction.getAccountId()))
                .filter(t -> t.getTimestamp().isAfter(windowStart))
                .anyMatch(t -> !t.getLocation().getCountry().equals(currentCountry));
    }

    @Override
    public String describe() {
        return "Transaction from different country within " + impossibleTravelMinutes + " minutes";
    }
}