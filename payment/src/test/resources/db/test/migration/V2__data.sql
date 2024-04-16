INSERT INTO language (name) VALUES
('en'),
('de'),
('it');

INSERT INTO currency (name, symbol) VALUES
('BRL', 'R$'),
('USD', '$'),
('EUR', '€'),
('GBP', '£');

INSERT INTO merchant (name, secret) values
                                        ('name', '{bcrypt}' || crypt('secret', gen_salt('bf', 12))),
                                        ('name2', '{bcrypt}' || crypt('secret2', gen_salt('bf', 12))),
                                        ('name3', '{bcrypt}' || crypt('secret3', gen_salt('bf', 12)));

INSERT INTO Account (currency_iso, merchant_id, amount) VALUES
                                                                   ('USD', 1, 10000.00),
                                                                   ('EUR', 1, 8000.00),
                                                                   ('GBP', 1, 6000.00),
                                                                   ('USD', 2, 5000.00),
                                                                   ('EUR', 2, 3000.00),
                                                                   ('GBP', 2, 3000.00),
                                                                   ('USD', 3, 2000.00),
                                                                   ('EUR', 3, 4000.00),
                                                                   ('GBP', 3, 6000.00);

INSERT INTO Card (card_number, ccv, exp_date, card_amount) VALUES
                                                               ('1234567890123456', '123', '2024-12-31', 1500.00),
                                                               ('2345678901234567', '234', '2023-11-30', 800.00),
                                                               ('3456789012345678', '345', '2022-10-31', 700.00);

INSERT INTO Customer (card_id, first_name, last_name, country) VALUES
                                                                   (1, 'John', 'Doe', 'USA'),
                                                                   (2, 'Alice', 'Smith', 'UK'),
                                                                   (3, 'Michael', 'Johnson', 'Canada');