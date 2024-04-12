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

INSERT INTO AccountBalance (currency_iso, merchant_id, amount) VALUES
                                                                   ('USD', 1, 10000.00),
                                                                   ('EUR', 1, 8000.00),
                                                                   ('GBP', 1, 6000.00),
                                                                   ('USD', 2, 5000.00),
                                                                   ('EUR', 2, 3000.00),
                                                                   ('GBP', 2, 3000.00),
                                                                   ('USD', 3, 2000.00),
                                                                   ('EUR', 3, 4000.00),
                                                                   ('GBP', 3, 6000.00);