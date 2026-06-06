import model.*;
import repository.*;
import service.*;
import util.IdGenerator;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        AuditService audit = AuditService.getInstance();
        CustomerDao customerDao = CustomerDao.getInstance();
        TransactionDao transactionDao = TransactionDao.getInstance();
        RuleDao ruleDao = RuleDao.getInstance();
        AlertDao alertDao = AlertDao.getInstance();

        clearTables();
        audit.log("CLEAR_TABLES");

        CustomerService customerService = new CustomerService();
        AccountService accountService = new AccountService(customerService);
        CardService cardService = new CardService(accountService);
        MerchantService merchantService = new MerchantService();
        AlertService alertService = new AlertService();
        FraudDetectionService fraudService = new FraudDetectionService();
        TransactionService transactionService = new TransactionService(
                accountService, cardService, fraudService, alertService);
        AnalystService analystService = new AnalystService();
        RiskScoreService riskScoreService = new RiskScoreService(alertService);

        Rule largeAmount = fraudService.addRule(new AmountRule(
                IdGenerator.nextRuleId(), "Large transaction", Severity.HIGH, new BigDecimal("5000")));
        ruleDao.insert(largeAmount);
        audit.log("CREATE_RULE");

        Rule burstFreq = fraudService.addRule(new FrequencyRule(
                IdGenerator.nextRuleId(), "Burst transactions", Severity.MEDIUM, 3, 5));
        ruleDao.insert(burstFreq);
        audit.log("CREATE_RULE");

        Rule impossibleTravel = fraudService.addRule(new LocationRule(
                IdGenerator.nextRuleId(), "Impossible travel", Severity.CRITICAL, 60));
        ruleDao.insert(impossibleTravel);
        audit.log("CREATE_RULE");

        Customer alice = customerService.createCustomer("Alice", "Popescu", "alice@example.ro");
        customerDao.insert(alice);
        audit.log("CREATE_CUSTOMER");

        Analyst analyst = analystService.createAnalyst("Maria Vasilescu", "maria@bank.ro");
        audit.log("CREATE_ANALYST");

        CheckingAccount account = accountService.openCheckingAccount(
                alice.getId(), new BigDecimal("20000"), new BigDecimal("500"));
        insertAccount(account);
        audit.log("OPEN_ACCOUNT");

        Card card = cardService.issueCard(account.getId(), alice.getFullName());
        audit.log("ISSUE_CARD");

        Location bucharest = new Location("Romania", "Bucuresti");
        Location bangkok = new Location("Thailand", "Bangkok");
        Merchant kaufland = merchantService.registerMerchant("Kaufland", MerchantCategory.GROCERY, bucharest);
        Merchant casino = merchantService.registerMerchant("Bangkok Casino", MerchantCategory.GAMBLING, bangkok);
        audit.log("REGISTER_MERCHANTS");

        Transaction tx;

        tx = transactionService.recordCardTransaction(account.getId(), new BigDecimal("250"), bucharest,
                card.getId(), kaufland.getId(), false);
        transactionDao.insert(tx);
        audit.log("RECORD_TRANSACTION");

        tx = transactionService.recordCardTransaction(account.getId(), new BigDecimal("8500"), bucharest,
                card.getId(), kaufland.getId(), true);
        transactionDao.insert(tx);
        audit.log("RECORD_TRANSACTION");

        for (int i = 0; i < 4; i++) {
            tx = transactionService.recordCardTransaction(account.getId(), new BigDecimal("50"), bucharest,
                    card.getId(), kaufland.getId(), false);
            transactionDao.insert(tx);
            audit.log("RECORD_TRANSACTION");
        }

        tx = transactionService.recordCardTransaction(account.getId(), new BigDecimal("1200"), bangkok,
                card.getId(), casino.getId(), true);
        transactionDao.insert(tx);
        audit.log("RECORD_TRANSACTION");

        for (Alert alert : alertService.findAll()) {
            alertDao.insert(alert);
            audit.log("CREATE_ALERT");
        }

        System.out.println("Customers:");
        customerDao.findAll().forEach(System.out::println);

        System.out.println("\nRules:");
        ruleDao.findAll().forEach(System.out::println);

        System.out.println("\nTransactions:");
        transactionDao.findAll().forEach(System.out::println);

        System.out.println("\nAlerts:");
        alertDao.findAll().forEach(System.out::println);

        System.out.println("\nRisk score for Alice: " + riskScoreService.recalculate(account.getId()));

        System.out.println("\nAnalyst review:");
        for (Alert alert : new ArrayList<>(alertService.findAll())) {
            alertService.assignToAnalyst(alert.getId(), analyst.getId());

            if (alert.getSeverity() == Severity.CRITICAL || alert.getSeverity() == Severity.HIGH) {
                alertService.confirmFraud(alert.getId());
                alertDao.update(alert);
                audit.log("CONFIRM_FRAUD");
                System.out.println("Confirmed fraud: " + alert);
            } else {
                alertService.markFalsePositive(alert.getId());
                alertDao.update(alert);
                audit.log("MARK_FALSE_POSITIVE");
            }
            analystService.recordResolution(analyst.getId());
        }

        System.out.println("Analyst: " + analyst);
        System.out.println("Risk score after review: " + riskScoreService.recalculate(account.getId()));

        burstFreq.disable();
        ruleDao.update(burstFreq);
        audit.log("DISABLE_RULE");
        System.out.println("\nDisabled rule: " + burstFreq);

        Customer bob = customerService.createCustomer("Bob", "Ionescu", "bob@example.ro");
        customerDao.insert(bob);
        audit.log("CREATE_CUSTOMER");

        customerDao.delete(bob.getId());
        audit.log("DELETE_CUSTOMER");
        System.out.println("Deleted customer: " + bob.getId());
    }

    private static void clearTables() throws Exception {
        try (Statement stmt = DatabaseConnection.getInstance().getConnection().createStatement()) {
            stmt.execute("DELETE FROM alerts");
            stmt.execute("DELETE FROM transactions");
            stmt.execute("DELETE FROM cards");
            stmt.execute("DELETE FROM accounts");
            stmt.execute("DELETE FROM rules");
            stmt.execute("DELETE FROM merchants");
            stmt.execute("DELETE FROM customers");
        }
    }

    private static void insertAccount(CheckingAccount account) throws Exception {
        String sql = "INSERT INTO accounts (id, customer_id, account_type, balance, opened_at, overdraft_limit) " +
                "VALUES (?, ?, 'CHECKING', ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, account.getId());
            stmt.setString(2, account.getCustomerId());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.setTimestamp(4, Timestamp.valueOf(account.getOpenedAt()));
            stmt.setBigDecimal(5, account.getOverdraftLimit());
            stmt.executeUpdate();
        }
    }
}