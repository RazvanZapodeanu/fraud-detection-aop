package service;

import exception.EntityNotFoundException;
import model.Account;
import model.CheckingAccount;
import model.Customer;
import model.SavingsAccount;
import util.IdGenerator;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private final Map<String, Account> accounts = new HashMap<>();
    private final CustomerService customerService;

    public AccountService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public CheckingAccount openCheckingAccount(String customerId, BigDecimal initialDeposit, BigDecimal overdraftLimit) {
        Customer customer = customerService.findById(customerId);
        String accountId = IdGenerator.nextAccountId();
        CheckingAccount account = new CheckingAccount(accountId, customerId, initialDeposit, overdraftLimit);
        accounts.put(accountId, account);
        customer.addAccount(account);
        return account;
    }

    public SavingsAccount openSavingsAccount(String customerId, BigDecimal initialDeposit, BigDecimal interestRate) {
        Customer customer = customerService.findById(customerId);
        String accountId = IdGenerator.nextAccountId();
        SavingsAccount account = new SavingsAccount(accountId, customerId, initialDeposit, interestRate);
        accounts.put(accountId, account);
        customer.addAccount(account);
        return account;
    }

    public Account findById(String id) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new EntityNotFoundException("Account not found: " + id);
        }
        return account;
    }

    public Collection<Account> findAll() {
        return Collections.unmodifiableCollection(accounts.values());
    }
}