package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {
    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getFullName() { return firstName + " " + lastName; }

    public void setEmail(String email) { this.email = email; }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    @Override
    public String toString() {
        return getFullName() + " (" + id + ")";
    }
}