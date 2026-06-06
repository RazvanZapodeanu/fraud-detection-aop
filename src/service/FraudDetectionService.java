package service;

import exception.EntityNotFoundException;
import model.Alert;
import model.Rule;
import model.Transaction;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FraudDetectionService {
    private final Map<String, Rule> rules = new HashMap<>();

    public Rule addRule(Rule rule) {
        rules.put(rule.getId(), rule);
        return rule;
    }

    public void removeRule(String ruleId) {
        if (rules.remove(ruleId) == null) {
            throw new EntityNotFoundException("Rule not found: " + ruleId);
        }
    }

    public Rule findRule(String ruleId) {
        Rule rule = rules.get(ruleId);
        if (rule == null) {
            throw new EntityNotFoundException("Rule not found: " + ruleId);
        }
        return rule;
    }

    public Collection<Rule> findAllRules() {
        return Collections.unmodifiableCollection(rules.values());
    }

    public void enableRule(String ruleId) {
        findRule(ruleId).enable();
    }

    public void disableRule(String ruleId) {
        findRule(ruleId).disable();
    }

    public List<Alert> evaluate(Transaction transaction, List<Transaction> history) {
        List<Alert> triggered = new ArrayList<>();
        for (Rule rule : rules.values()) {
            if (!rule.isEnabled()) continue;
            if (rule.matches(transaction, history)) {
                String alertId = IdGenerator.nextAlertId();
                Alert alert = new Alert(alertId, transaction.getId(), transaction.getAccountId(),
                        rule.getId(), rule.getSeverity(), rule.describe());
                triggered.add(alert);
            }
        }
        return triggered;
    }
}