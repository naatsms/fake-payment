INSERT INTO Card (card_number, ccv, exp_date, card_amount) VALUES
('1234567890123456', '123', '2024-12-31', 1500.00),
('2345678901234567', '234', '2023-11-30', 800.00),
('3456789012345678', '345', '2022-10-31', 700.00),
('3456789012345689', '347', '2022-08-29', 900.00);

INSERT INTO Customer (card_id, first_name, last_name, country) VALUES
                                                                   (1, 'John', 'Doe', 'USA'),
                                                                   (2, 'Alice', 'Smith', 'UK'),
                                                                   (3, 'Michael', 'Johnson', 'Canada'),
                                                                   (4, 'Emily', 'Brown', 'Australia');

INSERT INTO PaymentTransaction (payment_method, type, status, account_id, customer_id, language_iso, currency_iso, amount, message, notification_url) VALUES
('CARD', 'TRANSACTION', 'IN_PROGRESS', 1, 1, 'en', 'USD', 100.00, 'Sample message 1', 'https://example.com/notification1'),
('CARD', 'TRANSACTION', 'SUCCESS', 2, 2, 'en', 'EUR', 200.00, 'Sample message 2', 'https://example.com/notification2'),
('CARD', 'TRANSACTION', 'ERROR', 3, 3, 'en', 'GBP', 300.00, 'Sample message 3', 'https://example.com/notification3'),
('CARD', 'PAYOUT', 'IN_PROGRESS', 4, 4, 'en', 'USD', 400.00, 'Sample message 4', 'https://example.com/notification4'),
('CARD', 'PAYOUT', 'SUCCESS', 5, 1, 'en', 'EUR', 500.00, 'Sample message 5', 'https://example.com/notification5'),
('CARD', 'PAYOUT', 'ERROR', 6, 2, 'en', 'GBP', 600.00, 'Sample message 6', 'https://example.com/notification6'),
('CARD', 'TRANSACTION', 'IN_PROGRESS', 7, 3, 'en', 'USD', 'Sample message 7', 'https://example.com/notification7'),
('CARD', 'TRANSACTION', 'SUCCESS', 8, 4, 'en', 'EUR', 800.00, 'Sample message 8', 'https://example.com/notification8'),
('CARD', 'TRANSACTION', 'ERROR', 9, 1, 'en', 'GBP', 900.00, 'Sample message 9', 'https://example.com/notification9'),
('CARD', 'PAYOUT', 'IN_PROGRESS', 10, 2, 'en', 'USD', 1000.00, 'Sample message 10', 'https://example.com/notification10');
