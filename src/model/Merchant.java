package model;

public class Merchant {
    private final String id;
    private final String name;
    private final MerchantCategory category;
    private final Location location;

    public Merchant(String id, String name, MerchantCategory category, Location location) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public MerchantCategory getCategory() { return category; }
    public Location getLocation() { return location; }

    @Override
    public String toString() {
        return name + " [" + category + "]";
    }
}