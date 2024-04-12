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
                                        ('name', '{bcrypt}' || crypt('secret', gen_salt('bf', 12)));

INSERT INTO Account (currency_iso, merchant_id, amount) VALUES
                                                                   ('USD', 1, 10000.00),
                                                                   ('EUR', 1, 8000.00),
                                                                   ('GBP', 1, 6000.00);