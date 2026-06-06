package service;

import exception.InsufficientFundsException;
import model.Account;
import model.Alert;
import model.Card;
import model.CardTransaction;
import model.Location;
import model.Transaction;
import model.TransferTransaction;
import model.WithdrawalTransaction;
import util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionService {
    private final List<Transaction> transactions = new ArrayList<>();

    private final AccountService accountService;
    private final CardService cardService;
    private final FraudDetectionService fraudDetectionService;
    private final AlertService alertService;

    public TransactionService(AccountService accountService,
                              CardService cardService,
                              FraudDetectionService fraudDetectionService,
                              AlertService alertService) {
        this.accountService = accountService;
        this.cardService = cardService;
        this.fraudDetectionService = fraudDetectionService;
        this.alertService = alertService;
    }

    public CardTransaction recordCardTransaction(String accountId, BigDecimal amount, Location location,
                                                 String cardId, String merchantId, boolean online) {
        Card card = cardService.findById(cardId);
        if (!card.isUsable()) {
            throw new IllegalStateException("Card is not usable: " + cardId);
        }
        Account account = accountService.findById(accountId);
        ensureSufficientFunds(account, amount);

        account.withdraw(amount);
        CardTransaction tx = new CardTransaction(IdGenerator.nextTransactionId(), accountId, amount,
                location, cardId, merchantId, online);
        registerAndDetect(tx);
        return tx;
    }

    public TransferTransaction recordTransfer(String sourceAccountId, String destinationAccountId,
                                              BigDecimal amount, Location location) {
        Account source = accountService.findById(sourceAccountId);
        Account destination = accountService.findById(destinationAccountId);
        ensureSufficientFunds(source, amount);

        source.withdraw(amount);
        destination.deposit(amount);
        TransferTransaction tx = new TransferTransaction(IdGenerator.nextTransactionId(), sourceAccountId,
                amount, location, destinationAccountId);
        registerAndDetect(tx);
        return tx;
    }

    public WithdrawalTransaction recordWithdrawal(String accountId, BigDecimal amount, Location location,
                                                  String atmId) {
        Account account = accountService.findById(accountId);
        ensureSufficientFunds(account, amount);

        account.withdraw(amount);
        WithdrawalTransaction tx = new WithdrawalTransaction(IdGenerator.nextTransactionId(), accountId,
                amount, location, atmId);
        registerAndDetect(tx);
        return tx;
    }

    public List<Transaction> findByAccount(String accountId) {
        return transactions.stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .toList();
    }

    public List<Transaction> findInRange(LocalDateTime from, LocalDateTime to) {
        return transactions.stream()
                .filter(t -> !t.getTimestamp().isBefore(from) && !t.getTimestamp().isAfter(to))
                .toList();
    }

    public List<Transaction> findByMerchant(String merchantId) {
        return transactions.stream()
                .filter(t -> t instanceof CardTransaction ct && ct.getMerchantId().equals(merchantId))
                .toList();
    }

    public List<Transaction> findAll() {
        return Collections.unmodifiableList(transactions);
    }

    private void ensureSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Account " + account.getId() + " has " + account.getBalance() + ", needs " + amount);
        }
    }

    private void registerAndDetect(Transaction tx) {
        transactions.add(tx);
        List<Alert> alerts = fraudDetectionService.evaluate(tx, transactions);
        for (Alert alert : alerts) {
            alertService.add(alert);
        }
    }
}