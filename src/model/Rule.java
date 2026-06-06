package model;

import java.util.List;

public abstract class Rule {
    private final String id;
    private final String name;
    private final Severity severity;
    private boolean enabled;

    protected Rule(String id, String name, Severity severity) {
        this.id = id;
        this.name = name;
        this.severity = severity;
        this.enabled = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Severity getSeverity() { return severity; }
    public boolean isEnabled() { return enabled; }

    public void enable() { this.enabled = true; }
    public void disable() { this.enabled = false; }

    public abstract boolean matches(Transaction transaction, List<Transaction> history);

    public abstract String describe();

    @Override
    public String toString() {
        return name + " [" + severity + ", enabled=" + enabled + "]";
    }
}