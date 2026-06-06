package service;

import exception.EntityNotFoundException;
import model.Alert;
import model.Severity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AlertService {
    private final TreeSet<Alert> alerts = new TreeSet<>();
    private final Map<String, Alert> alertsById = new HashMap<>();

    public void add(Alert alert) {
        alerts.add(alert);
        alertsById.put(alert.getId(), alert);
    }

    public Alert findById(String id) {
        Alert alert = alertsById.get(id);
        if (alert == null) {
            throw new EntityNotFoundException("Alert not found: " + id);
        }
        return alert;
    }

    public Set<Alert> findAll() {
        return Collections.unmodifiableSet(alerts);
    }

    public List<Alert> findBySeverity(Severity severity) {
        return alerts.stream()
                .filter(a -> a.getSeverity() == severity)
                .toList();
    }

    public List<Alert> findByAccount(String accountId) {
        return alerts.stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .toList();
    }

    public List<Alert> findOpen() {
        return alerts.stream()
                .filter(a -> !a.isResolved())
                .toList();
    }

    public void assignToAnalyst(String alertId, String analystId) {
        findById(alertId).assignTo(analystId);
    }

    public void confirmFraud(String alertId) {
        findById(alertId).confirmFraud();
    }

    public void markFalsePositive(String alertId) {
        findById(alertId).markFalsePositive();
    }
}