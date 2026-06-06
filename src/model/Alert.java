package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Alert implements Comparable<Alert> {
    private final String id;
    private final String transactionId;
    private final String ruleId;
    private final Severity severity;
    private final String message;
    private final LocalDateTime timestamp;
    private AlertStatus status;
    private String assignedAnalystId;
    private LocalDateTime resolvedAt;
    private final String accountId;

    public Alert(String id, String transactionId, String accountId, String ruleId,
                 Severity severity, String message) {
        this.id = id;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.status = AlertStatus.OPEN;
    }

    public Alert(String id, String transactionId, String accountId, String ruleId,
                 Severity severity, String message,
                 LocalDateTime timestamp, AlertStatus status,
                 String assignedAnalystId, LocalDateTime resolvedAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
        this.assignedAnalystId = assignedAnalystId;
        this.resolvedAt = resolvedAt;
    }



    public String getId() { return id; }
    public String getAccountId() { return accountId; }
    public String getTransactionId() { return transactionId; }
    public String getRuleId() { return ruleId; }
    public Severity getSeverity() { return severity; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public AlertStatus getStatus() { return status; }
    public String getAssignedAnalystId() { return assignedAnalystId; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    public void assignTo(String analystId) {
        this.assignedAnalystId = analystId;
    }

    public void confirmFraud() {
        this.status = AlertStatus.CONFIRMED_FRAUD;
        this.resolvedAt = LocalDateTime.now();
    }

    public void markFalsePositive() {
        this.status = AlertStatus.FALSE_POSITIVE;
        this.resolvedAt = LocalDateTime.now();
    }

    public boolean isResolved() {
        return status != AlertStatus.OPEN;
    }

    @Override
    public int compareTo(Alert other) {
        int bySeverity = other.severity.compareTo(this.severity);
        if (bySeverity != 0) return bySeverity;

        int byTime = other.timestamp.compareTo(this.timestamp);
        if (byTime != 0) return byTime;

        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alert)) return false;
        return id.equals(((Alert) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + message + " (alert=" + id + ", status=" + status + ")";
    }
}