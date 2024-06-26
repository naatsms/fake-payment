CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE Merchant (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) UNIQUE,
                          secret VARCHAR(255)
);

CREATE TABLE Currency (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE,
                          symbol VARCHAR(5)
);

CREATE TABLE Language (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Card (
                      id SERIAL PRIMARY KEY,
                      card_number VARCHAR UNIQUE,
                      ccv VARCHAR(3),
                      exp_date DATE,
                      card_amount DECIMAL(12,2),
                      CONSTRAINT ccv_length CHECK (LENGTH(ccv) = 3)
);

CREATE TABLE Account (
                                id SERIAL PRIMARY KEY,
                                currency_iso VARCHAR(255) REFERENCES Currency(name),
                                merchant_id INT REFERENCES Merchant(id) ON DELETE CASCADE,
                                amount DECIMAL(12,2) DEFAULT 0,
                                CONSTRAINT pk_account UNIQUE (currency_iso, merchant_id)
);

CREATE TABLE Customer (
                          id SERIAL PRIMARY KEY,
                          card_id INT REFERENCES Card(id) ON DELETE CASCADE,
                          first_name VARCHAR(255),
                          last_name VARCHAR(255),
                          country VARCHAR(255),
                          CONSTRAINT pk_customer UNIQUE (card_id, first_name, last_name)
);

CREATE TABLE PaymentTransaction (
                                    uuid UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                                    payment_method VARCHAR(255),
                                    type VARCHAR(255),
                                    status VARCHAR(255),
                                    account_id INT REFERENCES Account(id),
                                    customer_id INT REFERENCES Customer(id),
                                    language_iso VARCHAR(255) REFERENCES Language(name),
                                    currency_iso VARCHAR(255) REFERENCES Currency(name),
                                    amount DECIMAL(12,2),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    message VARCHAR(4000),
                                    notification_url VARCHAR(1000)
);

CREATE TABLE NotificationLog (
                          id SERIAL PRIMARY KEY,
                          transaction_uuid UUID REFERENCES PaymentTransaction(uuid) UNIQUE,
                          request_payload VARCHAR(4000),
                          response_payload VARCHAR(4000),
                          response_status INT,
                          url VARCHAR (1000)
);

CREATE OR REPLACE FUNCTION update_modified_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_modified_trigger
    BEFORE UPDATE ON PaymentTransaction
    FOR EACH ROW EXECUTE FUNCTION update_modified_timestamp();