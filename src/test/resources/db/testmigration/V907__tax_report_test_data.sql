-- Test data for Tax Report functional tests

-- ============================================
-- Tax Transactions - Sales with PPN
-- ============================================

-- Transaction 1: Sales with PPN (Pendapatan Jasa + PPN Keluaran)
-- DPP: 10.000.000, PPN: 1.100.000, Total: 11.100.000
INSERT INTO transactions (id, transaction_number, transaction_date, id_journal_template, amount, description, reference_number, notes, status, posted_at, posted_by, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000001',
    'TRX-TAX-0001',
    CURRENT_DATE - INTERVAL '5 day',
    'e0000000-0000-0000-0000-000000000001', -- Pendapatan Jasa Konsultasi
    11100000,
    'Pendapatan Jasa Konsultasi dengan PPN',
    'INV-TAX-001',
    'DPP: 10.000.000, PPN: 1.100.000',
    'POSTED',
    NOW(),
    'admin',
    NOW(),
    NOW()
);

-- Journal entries for TRX-TAX-0001
-- Debit: Bank BCA 11.100.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000001',
    'JE-TAX-0001',
    CURRENT_DATE - INTERVAL '5 day',
    '10000000-0000-0000-0000-000000000102', -- Bank BCA (1.1.02)
    11100000,
    0,
    'Pendapatan Jasa Konsultasi dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000001',
    NOW(),
    NOW()
);

-- Credit: Pendapatan Jasa Konsultasi 10.000.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000002',
    'JE-TAX-0001',
    CURRENT_DATE - INTERVAL '5 day',
    '40000000-0000-0000-0000-000000000101', -- Pendapatan Jasa Konsultasi (4.1.01)
    0,
    10000000,
    'Pendapatan Jasa Konsultasi dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000001',
    NOW(),
    NOW()
);

-- Credit: Hutang PPN 1.100.000 (PPN Keluaran)
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000003',
    'JE-TAX-0001',
    CURRENT_DATE - INTERVAL '5 day',
    '20000000-0000-0000-0000-000000000103', -- Hutang PPN (2.1.03)
    0,
    1100000,
    'PPN Keluaran 11%',
    'POSTED',
    'c0000000-0000-0000-0000-000000000001',
    NOW(),
    NOW()
);

-- ============================================
-- Tax Transactions - Purchase with PPN
-- ============================================

-- Transaction 2: Purchase with PPN (Beban + PPN Masukan)
-- DPP: 5.000.000, PPN: 550.000, Total: 5.550.000
INSERT INTO transactions (id, transaction_number, transaction_date, id_journal_template, amount, description, reference_number, notes, status, posted_at, posted_by, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000002',
    'TRX-TAX-0002',
    CURRENT_DATE - INTERVAL '4 day',
    'e0000000-0000-0000-0000-000000000005', -- Bayar Beban Server & Cloud
    5550000,
    'Pembelian Server dengan PPN',
    'PO-TAX-001',
    'DPP: 5.000.000, PPN: 550.000',
    'POSTED',
    NOW(),
    'admin',
    NOW(),
    NOW()
);

-- Journal entries for TRX-TAX-0002
-- Debit: Beban Server & Cloud 5.000.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000004',
    'JE-TAX-0002',
    CURRENT_DATE - INTERVAL '4 day',
    '50000000-0000-0000-0000-000000000102', -- Beban Server & Cloud (5.1.02)
    5000000,
    0,
    'Pembelian Server dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000002',
    NOW(),
    NOW()
);

-- Debit: PPN Masukan 550.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000005',
    'JE-TAX-0002',
    CURRENT_DATE - INTERVAL '4 day',
    '10000000-0000-0000-0000-000000000125', -- PPN Masukan (1.1.25)
    550000,
    0,
    'PPN Masukan 11%',
    'POSTED',
    'c0000000-0000-0000-0000-000000000002',
    NOW(),
    NOW()
);

-- Credit: Bank BCA 5.550.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000006',
    'JE-TAX-0002',
    CURRENT_DATE - INTERVAL '4 day',
    '10000000-0000-0000-0000-000000000102', -- Bank BCA (1.1.02)
    0,
    5550000,
    'Pembelian Server dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000002',
    NOW(),
    NOW()
);

-- ============================================
-- Tax Transactions - Payment with PPh 23 withholding
-- ============================================

-- Transaction 3: Payment to vendor with PPh 23 (2% withholding)
-- Gross: 2.000.000, PPh 23: 40.000, Net: 1.960.000
INSERT INTO transactions (id, transaction_number, transaction_date, id_journal_template, amount, description, reference_number, notes, status, posted_at, posted_by, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000003',
    'TRX-TAX-0003',
    CURRENT_DATE - INTERVAL '3 day',
    'e0000000-0000-0000-0000-000000000006', -- Bayar Beban Software & Lisensi
    2000000,
    'Pembayaran Jasa dengan PPh 23',
    'PO-TAX-002',
    'Gross: 2.000.000, PPh 23 (2%): 40.000',
    'POSTED',
    NOW(),
    'admin',
    NOW(),
    NOW()
);

-- Journal entries for TRX-TAX-0003
-- Debit: Beban Software & Lisensi 2.000.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000007',
    'JE-TAX-0003',
    CURRENT_DATE - INTERVAL '3 day',
    '50000000-0000-0000-0000-000000000103', -- Beban Software & Lisensi (5.1.03)
    2000000,
    0,
    'Pembayaran Jasa dengan PPh 23',
    'POSTED',
    'c0000000-0000-0000-0000-000000000003',
    NOW(),
    NOW()
);

-- Credit: Bank BCA 1.960.000 (Net payment)
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000008',
    'JE-TAX-0003',
    CURRENT_DATE - INTERVAL '3 day',
    '10000000-0000-0000-0000-000000000102', -- Bank BCA (1.1.02)
    0,
    1960000,
    'Pembayaran Jasa (Net)',
    'POSTED',
    'c0000000-0000-0000-0000-000000000003',
    NOW(),
    NOW()
);

-- Credit: Hutang PPh 23 40.000 (Withheld)
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000009',
    'JE-TAX-0003',
    CURRENT_DATE - INTERVAL '3 day',
    '20000000-0000-0000-0000-000000000121', -- Hutang PPh 23 (2.1.21)
    0,
    40000,
    'PPh 23 (2%) dipotong',
    'POSTED',
    'c0000000-0000-0000-0000-000000000003',
    NOW(),
    NOW()
);

-- ============================================
-- Tax Transactions - Another Sales with PPN
-- ============================================

-- Transaction 4: Another Sales with PPN
-- DPP: 15.000.000, PPN: 1.650.000, Total: 16.650.000
INSERT INTO transactions (id, transaction_number, transaction_date, id_journal_template, amount, description, reference_number, notes, status, posted_at, posted_by, created_at, updated_at)
VALUES (
    'c0000000-0000-0000-0000-000000000004',
    'TRX-TAX-0004',
    CURRENT_DATE - INTERVAL '2 day',
    'e0000000-0000-0000-0000-000000000002', -- Pendapatan Jasa Development
    16650000,
    'Pendapatan Jasa Development dengan PPN',
    'INV-TAX-002',
    'DPP: 15.000.000, PPN: 1.650.000',
    'POSTED',
    NOW(),
    'admin',
    NOW(),
    NOW()
);

-- Journal entries for TRX-TAX-0004
-- Debit: Bank BCA 16.650.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000010',
    'JE-TAX-0004',
    CURRENT_DATE - INTERVAL '2 day',
    '10000000-0000-0000-0000-000000000102', -- Bank BCA (1.1.02)
    16650000,
    0,
    'Pendapatan Jasa Development dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000004',
    NOW(),
    NOW()
);

-- Credit: Pendapatan Jasa Development 15.000.000
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000011',
    'JE-TAX-0004',
    CURRENT_DATE - INTERVAL '2 day',
    '40000000-0000-0000-0000-000000000102', -- Pendapatan Jasa Development (4.1.02)
    0,
    15000000,
    'Pendapatan Jasa Development dengan PPN',
    'POSTED',
    'c0000000-0000-0000-0000-000000000004',
    NOW(),
    NOW()
);

-- Credit: Hutang PPN 1.650.000 (PPN Keluaran)
INSERT INTO journal_entries (id, journal_number, journal_date, id_account, debit_amount, credit_amount, description, status, id_transaction, created_at, updated_at)
VALUES (
    'c1000000-0000-0000-0000-000000000012',
    'JE-TAX-0004',
    CURRENT_DATE - INTERVAL '2 day',
    '20000000-0000-0000-0000-000000000103', -- Hutang PPN (2.1.03)
    0,
    1650000,
    'PPN Keluaran 11%',
    'POSTED',
    'c0000000-0000-0000-0000-000000000004',
    NOW(),
    NOW()
);

-- ============================================
-- Expected Results Summary:
-- PPN Keluaran (Hutang PPN): 1.100.000 + 1.650.000 = 2.750.000
-- PPN Masukan: 550.000
-- Net PPN (Kurang Bayar): 2.750.000 - 550.000 = 2.200.000
--
-- PPh 23 Withheld: 40.000
-- PPh 23 Deposited: 0
-- PPh 23 Balance: 40.000
-- ============================================
