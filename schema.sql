DROP TABLE IF EXISTS alerts;
DROP TABLE IF EXISTS rules;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS merchants;
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
                           id          VARCHAR(20) PRIMARY KEY,
                           first_name  VARCHAR(100) NOT NULL,
                           last_name   VARCHAR(100) NOT NULL,
                           email       VARCHAR(200) NOT NULL
);

CREATE TABLE accounts (
                          id              VARCHAR(20) PRIMARY KEY,
                          customer_id     VARCHAR(20) NOT NULL REFERENCES customers(id),
                          account_type    VARCHAR(20) NOT NULL,
                          balance         NUMERIC(15, 2) NOT NULL,
                          opened_at       TIMESTAMP NOT NULL,
                          overdraft_limit NUMERIC(15, 2),
                          interest_rate   NUMERIC(5, 4)
);

CREATE TABLE cards (
                       id              VARCHAR(20) PRIMARY KEY,
                       account_id      VARCHAR(20) NOT NULL REFERENCES accounts(id),
                       cardholder_name VARCHAR(200) NOT NULL,
                       expiry_date     DATE NOT NULL,
                       status          VARCHAR(20) NOT NULL
);

CREATE TABLE merchants (
                           id       VARCHAR(20) PRIMARY KEY,
                           name     VARCHAR(200) NOT NULL,
                           category VARCHAR(50) NOT NULL,
                           country  VARCHAR(100) NOT NULL,
                           city     VARCHAR(100) NOT NULL
);

CREATE TABLE transactions (
                              id                     VARCHAR(20) PRIMARY KEY,
                              account_id             VARCHAR(20) NOT NULL REFERENCES accounts(id),
                              transaction_type       VARCHAR(20) NOT NULL,
                              amount                 NUMERIC(15, 2) NOT NULL,
                              timestamp              TIMESTAMP NOT NULL,
                              country                VARCHAR(100) NOT NULL,
                              city                   VARCHAR(100) NOT NULL,
                              card_id                VARCHAR(20),
                              merchant_id            VARCHAR(20),
                              is_online              BOOLEAN,
                              destination_account_id VARCHAR(20),
                              atm_id                 VARCHAR(50)
);

CREATE TABLE rules (
                       id               VARCHAR(20) PRIMARY KEY,
                       name             VARCHAR(200) NOT NULL,
                       rule_type        VARCHAR(20) NOT NULL,
                       severity         VARCHAR(20) NOT NULL,
                       enabled          BOOLEAN NOT NULL,
                       threshold        NUMERIC(15, 2),
                       max_transactions INT,
                       window_minutes   INT
);

CREATE TABLE alerts (
                        id                  VARCHAR(20) PRIMARY KEY,
                        transaction_id      VARCHAR(20) NOT NULL REFERENCES transactions(id),
                        account_id          VARCHAR(20) NOT NULL REFERENCES accounts(id),
                        rule_id             VARCHAR(20) NOT NULL REFERENCES rules(id),
                        severity            VARCHAR(20) NOT NULL,
                        message             TEXT NOT NULL,
                        timestamp           TIMESTAMP NOT NULL,
                        status              VARCHAR(30) NOT NULL,
                        assigned_analyst_id VARCHAR(20),
                        resolved_at         TIMESTAMP
);