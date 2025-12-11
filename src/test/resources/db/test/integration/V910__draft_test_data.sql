-- V910: Test data for Draft Transactions

-- Draft 1: PENDING status (for approval testing)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, overall_confidence, status, created_by, created_at)
VALUES (
    'd0000000-0000-0000-0000-000000000001',
    'MANUAL',
    'TEST-REF-001',
    'Toko Bangunan Jaya',
    CURRENT_DATE,
    250000.00,
    'IDR',
    'Toko Bangunan Jaya\nJl. Raya No. 123\nTotal: Rp 250.000',
    'receipt',
    0.85,
    'PENDING',
    'admin',
    NOW()
);

-- Draft 2: PENDING status (for rejection testing)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, overall_confidence, status, created_by, created_at)
VALUES (
    'd0000000-0000-0000-0000-000000000002',
    'TELEGRAM',
    'TEST-REF-002',
    'Warung Makan Sederhana',
    CURRENT_DATE - INTERVAL '1 day',
    75000.00,
    'IDR',
    'Warung Makan Sederhana\nNasi + Ayam\nTotal: 75.000',
    'food_receipt',
    0.92,
    'PENDING',
    'admin',
    NOW() - INTERVAL '1 hour'
);

-- Draft 3: PENDING status (for delete testing)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, overall_confidence, status, created_by, created_at)
VALUES (
    'd0000000-0000-0000-0000-000000000003',
    'MANUAL',
    'TEST-REF-003',
    'SPBU Pertamina',
    CURRENT_DATE - INTERVAL '2 days',
    500000.00,
    'IDR',
    'SPBU PERTAMINA\nPertalite 40L\nTotal: Rp 500.000',
    'fuel',
    0.95,
    'PENDING',
    'admin',
    NOW() - INTERVAL '2 hours'
);

-- Draft 4: APPROVED status (for viewing approved drafts)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, overall_confidence, status, created_by, created_at, processed_by, processed_at)
VALUES (
    'd0000000-0000-0000-0000-000000000004',
    'TELEGRAM',
    'TEST-REF-004',
    'Indomaret',
    CURRENT_DATE - INTERVAL '3 days',
    150000.00,
    'IDR',
    'INDOMARET\nBelanja bulanan\nTotal: 150.000',
    'retail',
    0.88,
    'APPROVED',
    'admin',
    NOW() - INTERVAL '1 day',
    'admin',
    NOW() - INTERVAL '12 hours'
);

-- Draft 5: REJECTED status (for viewing rejected drafts)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, overall_confidence, status, rejection_reason, created_by, created_at, processed_by, processed_at)
VALUES (
    'd0000000-0000-0000-0000-000000000005',
    'MANUAL',
    'TEST-REF-005',
    'Unknown Merchant',
    CURRENT_DATE - INTERVAL '4 days',
    50000.00,
    'IDR',
    'Struk tidak jelas\nTotal: ???',
    'unknown',
    0.25,
    'REJECTED',
    'Struk tidak bisa dibaca dengan jelas',
    'admin',
    NOW() - INTERVAL '2 days',
    'admin',
    NOW() - INTERVAL '1 day'
);

-- Draft 6: PENDING with low confidence (for UI display testing)
INSERT INTO draft_transactions (id, source, source_reference, merchant_name, transaction_date, amount, currency, raw_ocr_text, receipt_type, merchant_confidence, amount_confidence, date_confidence, overall_confidence, status, created_by, created_at)
VALUES (
    'd0000000-0000-0000-0000-000000000006',
    'TELEGRAM',
    'TEST-REF-006',
    'Warung ???',
    NULL,
    NULL,
    'IDR',
    'Struk rusak\n...\n...',
    'unknown',
    0.20,
    0.10,
    0.00,
    0.10,
    'PENDING',
    'admin',
    NOW() - INTERVAL '30 minutes'
);
