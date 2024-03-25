CREATE TABLE Merchant (
                          MerchantId SERIAL PRIMARY KEY,
                          MerchantName VARCHAR(255) UNIQUE,
                          SecretKey VARCHAR(255)
);

CREATE TYPE PaymentMethod AS ENUM ('CARD');
CREATE TYPE TransactionType AS ENUM ('TRANSACTION','PAYOUT');
CREATE TYPE TransactionStatus AS ENUM ('SUCCESS','IN_PROGRESS','ERROR');

