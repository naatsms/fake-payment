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
                      amount DECIMAL(12,2) DEFAULT 1000,
                      CONSTRAINT ccv_length CHECK (LENGTH(ccv) = 3)
);

CREATE TABLE AccountBalance (
                                id SERIAL PRIMARY KEY,
                                currency_iso VARCHAR(255) REFERENCES Currency(name),
                                merchant_id INT REFERENCES Merchant(id),
                                amount DECIMAL(12,2) DEFAULT 0,
                                CONSTRAINT pk_account UNIQUE (currency_iso, merchant_id)
);

CREATE TABLE Customer (
                          id SERIAL PRIMARY KEY,
                          card_id INT REFERENCES Card(id),
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
                                    account_id INT REFERENCES AccountBalance(id),
                                    customer_id INT REFERENCES Customer(id),
                                    language_iso VARCHAR(255) REFERENCES Language(name),
                                    amount DECIMAL(12,2),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    message VARCHAR(255),
                                    notification_url VARCHAR(1000)
);