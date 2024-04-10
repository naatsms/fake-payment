INSERT INTO Currency (name, symbol) VALUES
('USD', '$'),
('EUR', '€'),
('GBP', '£');

INSERT INTO Language (name) VALUES
('en');

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

INSERT INTO Card (id, card_number, ccv, exp_date, card_amount) VALUES
(1, '1234567890123456', '123', '2024-12-31', 1500.00),
(2, '2345678901234567', '234', '2023-11-30', 800.00),
(3, '3456789012345678', '345', '2022-10-31', 700.00),
(4, '3456789012345689', '347', '2022-08-29', 900.00);

INSERT INTO Customer (card_id, first_name, last_name, country) VALUES
                                                                   (1, 'John', 'Doe', 'USA'),
                                                                   (2, 'Alice', 'Smith', 'UK'),
                                                                   (3, 'Michael', 'Johnson', 'Canada'),
                                                                   (4, 'Emily', 'Brown', 'Australia');


INSERT INTO PaymentTransaction (uuid, payment_method, type, status, account_id, customer_id, language_iso, currency_iso, amount, created_at, updated_at, message, notification_url) VALUES
('a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6', 'CARD', 'TRANSACTION', 'IN_PROGRESS', 1, 1, 'en', 'USD', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 1', 'https://example.com/notification1'),
('b1c2d3e4-f5g6-h7i8-j9k0-l1m2n3o4p5q6', 'CARD', 'TRANSACTION', 'SUCCESS', 2, 2, 'en', 'EUR', 200.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 2', 'https://example.com/notification2'),
('c1d2e3f4-g5h6-i7j8-k9l0-m1n2o3p4q5r6', 'CARD', 'TRANSACTION', 'ERROR', 3, 3, 'en', 'GBP', 300.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 3', 'https://example.com/notification3'),
('d1e2f3g4-h5i6-j7k8-l9m0-n1o2p3q4r5s6', 'CARD', 'PAYOUT', 'IN_PROGRESS', 4, 4, 'en', 'USD', 400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 4', 'https://example.com/notification4'),
('e1f2g3h4-i5j6-k7l8-m9n0-o1p2q3r4s5t6', 'CARD', 'PAYOUT', 'SUCCESS', 5, 1, 'en', 'EUR', 500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 5', 'https://example.com/notification5'),
('f1g2h3i4-j5k6-l7m8-n9o0-p1q2r3s4t5u6', 'CARD', 'PAYOUT', 'ERROR', 6, 2, 'en', 'GBP', 600.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 6', 'https://example.com/notification6'),
('g1h2i3j4-k5l6-m7n8-o9p0-q1r2s3t4u5v6', 'CARD', 'TRANSACTION', 'IN_PROGRESS', 7, 3, 'en', 'USD', 700.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 7', 'https://example.com/notification7'),
('h1i2j3k4-l5m6-n7o8-p9q0-r1s2t3u4v5w6', 'CARD', 'TRANSACTION', 'SUCCESS', 8, 4, 'en', 'EUR', 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 8', 'https://example.com/notification8'),
('i1j2k3l4-m5n6-o7p8-q9r0-s1t2u3v4w5x6', 'CARD', 'TRANSACTION', 'ERROR', 9, 1, 'en', 'GBP', 900.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 9', 'https://example.com/notification9'),
('j1k2l3m4-n5o6-p7q8-r9s0-t1u2v3w4x5y6', 'CARD', 'PAYOUT', 'IN_PROGRESS', 10, 2, 'en', 'USD', 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sample message 10', 'https://example.com/notification10');
