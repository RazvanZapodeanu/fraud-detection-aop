package service;

import model.RiskScore;
import model.Severity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RiskScoreService {
    private final Map<String, RiskScore> scores = new HashMap<>();
    private final AlertService alertService;

    public RiskScoreService(AlertService alertService) {
        this.alertService = alertService;
    }

    public RiskScore recalculate(String accountId) {
        RiskScore score = scores.computeIfAbsent(accountId, RiskScore::new);
        int total = alertService.findByAccount(accountId).stream()
                .filter(a -> !a.isResolved())
                .mapToInt(a -> severityWeight(a.getSeverity()))
                .sum();
        score.update(total);
        return score;
    }

    public RiskScore getScore(String accountId) {
        return scores.computeIfAbsent(accountId, RiskScore::new);
    }

    public Collection<RiskScore> findAll() {
        return Collections.unmodifiableCollection(scores.values());
    }

    private int severityWeight(Severity severity) {
        return switch (severity) {
            case LOW -> 5;
            case MEDIUM -> 15;
            case HIGH -> 30;
            case CRITICAL -> 50;
        };
    }
}