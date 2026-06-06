package model;

import java.time.LocalDateTime;

public class RiskScore {
    private final String accountId;
    private int score;
    private LocalDateTime lastUpdated;

    public RiskScore(String accountId) {
        this.accountId = accountId;
        this.score = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getAccountId() { return accountId; }
    public int getScore() { return score; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    public void update(int newScore) {
        this.score = Math.max(0, Math.min(100, newScore));
        this.lastUpdated = LocalDateTime.now();
    }

    public RiskLevel getLevel() {
        if (score >= 75) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    @Override
    public String toString() {
        return "RiskScore[account=" + accountId + ", score=" + score + ", level=" + getLevel() + "]";
    }
}