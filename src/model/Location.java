package model;

import java.util.Objects;

public class Location {
    private final String country;
    private final String city;

    public Location(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getCountry() { return country; }
    public String getCity() { return city; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location other = (Location) o;
        return Objects.equals(country, other.country) && Objects.equals(city, other.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city);
    }

    @Override
    public String toString() {
        return city + ", " + country;
    }
}