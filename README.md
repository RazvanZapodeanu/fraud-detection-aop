# Fraud Detection

Bank transaction fraud detection system. University project for Advanced Object-Oriented Programming (Java).

Basic idea: you record transactions, the system runs some rules on them, and if something looks suspicious it generates an alert. Plus a risk score per account that goes up when alerts pile up.

## What it does

- 3 transaction types: card, transfer between accounts, ATM withdrawal
- 3 detection rules: amount over threshold, too many transactions in a short window, "impossible travel" (transactions in different countries within minutes)
- Alerts with severity (LOW/MEDIUM/HIGH/CRITICAL) and status (open/confirmed/false positive)
- An analyst processes alerts and marks them as fraud or false positive
- Risk score 0-100 per account, calculated from active alerts

Everything persisted in Postgres, plus an audit log in CSV for every action.

## Stack

Java 21, PostgreSQL, JDBC. No frameworks - everything written manually so the patterns are visible (Singleton, generic DAO, dependency injection).

## How to run it

1. Install Postgres locally, create a database called `fraud_detection`
2. Run `schema.sql` against it (creates the tables)
3. Add the Postgres JDBC driver as a dependency in IntelliJ
4. In `repository/DatabaseConnection.java` change user/password if yours are different
5. Run `Main.java`

Main runs a full end-to-end scenario - inserts everything from scratch, runs some suspicious transactions, you see how alerts get generated.

## Structure

```
src/
  model/        - entities (Customer, Account, Transaction etc.)
  service/      - business logic
  repository/   - DAO for DB access
  util/         - IdGenerator
  exception/    - custom exceptions
```

3 inheritance hierarchies: Transaction (card/transfer/withdrawal), Account (checking/savings), Rule (amount/frequency/location).

## Notes

The DB gets wiped on every Main run (clearTables) so the demo is reproducible. In a real app you obviously wouldn't do this.
