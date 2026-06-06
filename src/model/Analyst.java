package model;

public class Analyst {
    private final String id;
    private final String name;
    private final String email;
    private int alertsResolved;

    public Analyst(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.alertsResolved = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAlertsResolved() { return alertsResolved; }

    public void incrementResolved() {
        alertsResolved++;
    }

    @Override
    public String toString() {
        return name + " (" + id + "), resolved=" + alertsResolved;
    }
}