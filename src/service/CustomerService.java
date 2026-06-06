package service;

import exception.EntityNotFoundException;
import model.Customer;
import util.IdGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {
    private final Map<String, Customer> customers = new HashMap<>();

    public Customer createCustomer(String firstName, String lastName, String email) {
        String id = IdGenerator.nextCustomerId();
        Customer customer = new Customer(id, firstName, lastName, email);
        customers.put(id, customer);
        return customer;
    }

    public Customer findById(String id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new EntityNotFoundException("Customer not found: " + id);
        }
        return customer;
    }

    public Collection<Customer> findAll() {
        return Collections.unmodifiableCollection(customers.values());
    }

    public void updateEmail(String id, String email) {
        findById(id).setEmail(email);
    }
}