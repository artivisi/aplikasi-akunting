-- V901: Report Test Data
-- Test data for Basic Reports (1.3) - Trial Balance, General Ledger, Balance Sheet, Income Statement
-- This migration runs only in test profile

-- =============================================================================
-- TEST DATA SUMMARY
-- =============================================================================
-- Jan 2024: Capital injection + Equipment purchase
-- Feb 2024: Consulting revenue + Salary expense
-- Mar 2024: Development revenue + Cloud expense
-- Apr 2024: Consulting revenue + VOID entry (should be excluded)
-- May 2024: Salary expense
-- Jun 2024: Equipment on credit + Depreciation
--
-- VOID entry: JRN-2024-0008 should NOT be included in calculations

-- =============================================================================
-- EXPECTED TOTALS (for test assertions)
-- =============================================================================
-- TRIAL BALANCE:
--   Total Debits  = 163,000,000
--   Total Credits = 163,000,000
--
-- BALANCE SHEET (as of 2024-06-30):
--   Total Assets      = 143,000,000 (Cash 84M + BCA 30M + Peralatan 30M - Akum 1M)
--   Total Liabilities =  10,000,000 (Hutang Usaha)
--   Total Equity      = 100,000,000 (Modal Disetor)
--   Net Income        =  33,000,000 (Revenue 52M - Expense 19M)
--   A = L + E + NI    = 143,000,000 ✓
--
-- INCOME STATEMENT (2024-01-01 to 2024-06-30):
--   Total Revenue  = 52,000,000 (Konsultasi 27M + Development 25M)
--   Total Expense  = 19,000,000 (Gaji 16M + Server 2M + Penyusutan 1M)
--   Net Income     = 33,000,000

-- =============================================================================
-- ACCOUNT IDs (from V002 seed data)
-- =============================================================================
-- Assets:
--   Cash (1.1.01)                    : 10000000-0000-0000-0000-000000000101
--   Bank BCA (1.1.02)                : 10000000-0000-0000-0000-000000000102
--   Peralatan Komputer (1.2.01)      : 10000000-0000-0000-0000-000000000121
--   Akum. Peny. Peralatan (1.2.02)   : 10000000-0000-0000-0000-000000000122
--
-- Liabilities:
--   Hutang Usaha (2.1.01)            : 20000000-0000-0000-0000-000000000101
--
-- Equity:
--   Modal Disetor (3.1.01)           : 30000000-0000-0000-0000-000000000101
--
-- Revenue:
--   Pendapatan Jasa Konsultasi (4.1.01) : 40000000-0000-0000-0000-000000000101
--   Pendapatan Jasa Development (4.1.02): 40000000-0000-0000-0000-000000000102
--
-- Expense:
--   Beban Gaji (5.1.01)              : 50000000-0000-0000-0000-000000000101
--   Beban Server & Cloud (5.1.02)    : 50000000-0000-0000-0000-000000000102
--   Beban Penyusutan (5.1.07)        : 50000000-0000-0000-0000-000000000107

-- =============================================================================
-- JOURNAL ENTRIES
-- =============================================================================

-- JRN-2024-0001: Initial Capital Injection (Jan 2024)
-- Cash (D) 100,000,000 / Modal Disetor (C) 100,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000001', 'JRN-2024-0001', '2024-01-05', 'POSTED', '2024-01-05 10:00:00', '10000000-0000-0000-0000-000000000101', 'Setoran modal awal', 100000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000002', 'JRN-2024-0001', '2024-01-05', 'POSTED', '2024-01-05 10:00:00', '30000000-0000-0000-0000-000000000101', 'Setoran modal awal', 0.00, 100000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0002: Equipment Purchase via Bank Transfer (Jan 2024)
-- Peralatan Komputer (D) 20,000,000 / Bank BCA (C) 20,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000003', 'JRN-2024-0002', '2024-01-15', 'POSTED', '2024-01-15 14:00:00', '10000000-0000-0000-0000-000000000121', 'Pembelian laptop dan server', 20000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000004', 'JRN-2024-0002', '2024-01-15', 'POSTED', '2024-01-15 14:00:00', '10000000-0000-0000-0000-000000000102', 'Pembelian laptop dan server', 0.00, 20000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0003: Consulting Revenue (Feb 2024)
-- Bank BCA (D) 15,000,000 / Pendapatan Jasa Konsultasi (C) 15,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000005', 'JRN-2024-0003', '2024-02-10', 'POSTED', '2024-02-10 11:00:00', '10000000-0000-0000-0000-000000000102', 'Pembayaran jasa konsultasi PT ABC', 15000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000006', 'JRN-2024-0003', '2024-02-10', 'POSTED', '2024-02-10 11:00:00', '40000000-0000-0000-0000-000000000101', 'Pembayaran jasa konsultasi PT ABC', 0.00, 15000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0004: Salary Expense (Feb 2024)
-- Beban Gaji (D) 8,000,000 / Cash (C) 8,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000007', 'JRN-2024-0004', '2024-02-28', 'POSTED', '2024-02-28 16:00:00', '50000000-0000-0000-0000-000000000101', 'Gaji karyawan Februari 2024', 8000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000008', 'JRN-2024-0004', '2024-02-28', 'POSTED', '2024-02-28 16:00:00', '10000000-0000-0000-0000-000000000101', 'Gaji karyawan Februari 2024', 0.00, 8000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0005: Development Project Payment (Mar 2024)
-- Bank BCA (D) 25,000,000 / Pendapatan Jasa Development (C) 25,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000009', 'JRN-2024-0005', '2024-03-15', 'POSTED', '2024-03-15 10:00:00', '10000000-0000-0000-0000-000000000102', 'Pembayaran proyek development PT XYZ', 25000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000010', 'JRN-2024-0005', '2024-03-15', 'POSTED', '2024-03-15 10:00:00', '40000000-0000-0000-0000-000000000102', 'Pembayaran proyek development PT XYZ', 0.00, 25000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0006: Cloud Expense (Mar 2024)
-- Beban Server & Cloud (D) 2,000,000 / Bank BCA (C) 2,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000011', 'JRN-2024-0006', '2024-03-20', 'POSTED', '2024-03-20 09:00:00', '50000000-0000-0000-0000-000000000102', 'Biaya AWS bulan Maret', 2000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000012', 'JRN-2024-0006', '2024-03-20', 'POSTED', '2024-03-20 09:00:00', '10000000-0000-0000-0000-000000000102', 'Biaya AWS bulan Maret', 0.00, 2000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0007: Consulting Revenue (Apr 2024)
-- Bank BCA (D) 12,000,000 / Pendapatan Jasa Konsultasi (C) 12,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000013', 'JRN-2024-0007', '2024-04-05', 'POSTED', '2024-04-05 11:00:00', '10000000-0000-0000-0000-000000000102', 'Pembayaran jasa konsultasi PT DEF', 12000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000014', 'JRN-2024-0007', '2024-04-05', 'POSTED', '2024-04-05 11:00:00', '40000000-0000-0000-0000-000000000101', 'Pembayaran jasa konsultasi PT DEF', 0.00, 12000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0008: VOID Entry (Apr 2024) - Should be EXCLUDED from calculations
-- Beban Gaji (D) 5,000,000 / Cash (C) 5,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, voided_at, void_reason, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000015', 'JRN-2024-0008', '2024-04-15', 'VOID', '2024-04-15 10:00:00', '2024-04-16 09:00:00', 'Salah input jumlah', '50000000-0000-0000-0000-000000000101', 'Gaji karyawan - VOID', 5000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000016', 'JRN-2024-0008', '2024-04-15', 'VOID', '2024-04-15 10:00:00', '2024-04-16 09:00:00', 'Salah input jumlah', '10000000-0000-0000-0000-000000000101', 'Gaji karyawan - VOID', 0.00, 5000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0009: Salary Expense (May 2024)
-- Beban Gaji (D) 8,000,000 / Cash (C) 8,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000017', 'JRN-2024-0009', '2024-05-31', 'POSTED', '2024-05-31 16:00:00', '50000000-0000-0000-0000-000000000101', 'Gaji karyawan Mei 2024', 8000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000018', 'JRN-2024-0009', '2024-05-31', 'POSTED', '2024-05-31 16:00:00', '10000000-0000-0000-0000-000000000101', 'Gaji karyawan Mei 2024', 0.00, 8000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0010: Equipment Purchase on Credit (Jun 2024)
-- Peralatan Komputer (D) 10,000,000 / Hutang Usaha (C) 10,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000019', 'JRN-2024-0010', '2024-06-10', 'POSTED', '2024-06-10 14:00:00', '10000000-0000-0000-0000-000000000121', 'Pembelian monitor kredit', 10000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000020', 'JRN-2024-0010', '2024-06-10', 'POSTED', '2024-06-10 14:00:00', '20000000-0000-0000-0000-000000000101', 'Pembelian monitor kredit', 0.00, 10000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0011: Depreciation (Jun 2024)
-- Beban Penyusutan (D) 1,000,000 / Akum. Penyusutan Peralatan (C) 1,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000021', 'JRN-2024-0011', '2024-06-30', 'POSTED', '2024-06-30 17:00:00', '50000000-0000-0000-0000-000000000107', 'Penyusutan peralatan Q2 2024', 1000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000022', 'JRN-2024-0011', '2024-06-30', 'POSTED', '2024-06-30 17:00:00', '10000000-0000-0000-0000-000000000122', 'Penyusutan peralatan Q2 2024', 0.00, 1000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0012: DRAFT Entry (should be excluded from report calculations)
-- Beban Gaji (D) 3,000,000 / Cash (C) 3,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000023', 'JRN-2024-0012', '2024-06-30', 'DRAFT', '50000000-0000-0000-0000-000000000101', 'Bonus karyawan - DRAFT', 3000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000024', 'JRN-2024-0012', '2024-06-30', 'DRAFT', '10000000-0000-0000-0000-000000000101', 'Bonus karyawan - DRAFT', 0.00, 3000000.00, NOW(), NOW(), 'system', 'system');

-- =============================================================================
-- PRIOR PERIOD ENTRIES (2023) - Should be included in Balance Sheet, excluded from 2024 Income Statement
-- =============================================================================

-- JRN-2023-0001: Prior Year Capital (Dec 2023)
-- Cash (D) 50,000,000 / Modal Disetor (C) 50,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000025', 'JRN-2023-0001', '2023-12-01', 'POSTED', '2023-12-01 10:00:00', '10000000-0000-0000-0000-000000000101', 'Modal awal tahun 2023', 50000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000026', 'JRN-2023-0001', '2023-12-01', 'POSTED', '2023-12-01 10:00:00', '30000000-0000-0000-0000-000000000101', 'Modal awal tahun 2023', 0.00, 50000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2023-0002: Prior Year Revenue (Dec 2023)
-- Bank BCA (D) 10,000,000 / Pendapatan Jasa Konsultasi (C) 10,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000027', 'JRN-2023-0002', '2023-12-15', 'POSTED', '2023-12-15 11:00:00', '10000000-0000-0000-0000-000000000102', 'Jasa konsultasi 2023', 10000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000028', 'JRN-2023-0002', '2023-12-15', 'POSTED', '2023-12-15 11:00:00', '40000000-0000-0000-0000-000000000101', 'Jasa konsultasi 2023', 0.00, 10000000.00, NOW(), NOW(), 'system', 'system');

-- =============================================================================
-- FUTURE PERIOD ENTRIES (Jul 2024+) - Should be excluded when reporting as of 2024-06-30
-- =============================================================================

-- JRN-2024-0013: July 2024 Revenue (should be excluded from Q2 reports)
-- Bank BCA (D) 20,000,000 / Pendapatan Jasa Konsultasi (C) 20,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000029', 'JRN-2024-0013', '2024-07-05', 'POSTED', '2024-07-05 10:00:00', '10000000-0000-0000-0000-000000000102', 'Jasa konsultasi Juli 2024', 20000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000030', 'JRN-2024-0013', '2024-07-05', 'POSTED', '2024-07-05 10:00:00', '40000000-0000-0000-0000-000000000101', 'Jasa konsultasi Juli 2024', 0.00, 20000000.00, NOW(), NOW(), 'system', 'system');

-- JRN-2024-0014: July 2024 Expense (should be excluded from Q2 reports)
-- Beban Gaji (D) 8,000,000 / Cash (C) 8,000,000
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by)
VALUES
('90100000-0000-0000-0000-000000000031', 'JRN-2024-0014', '2024-07-31', 'POSTED', '2024-07-31 16:00:00', '50000000-0000-0000-0000-000000000101', 'Gaji Juli 2024', 8000000.00, 0.00, NOW(), NOW(), 'system', 'system'),
('90100000-0000-0000-0000-000000000032', 'JRN-2024-0014', '2024-07-31', 'POSTED', '2024-07-31 16:00:00', '10000000-0000-0000-0000-000000000101', 'Gaji Juli 2024', 0.00, 8000000.00, NOW(), NOW(), 'system', 'system');

-- =============================================================================
-- SOFT-DELETED ENTRIES - Should be excluded by @SQLRestriction
-- =============================================================================

-- JRN-2024-0015: Soft-deleted entry (deleted_at IS NOT NULL)
-- Should NOT appear in any reports or calculations
INSERT INTO journal_entries (id, journal_number, journal_date, status, posted_at, id_account, description, debit_amount, credit_amount, created_at, updated_at, created_by, updated_by, deleted_at)
VALUES
('90100000-0000-0000-0000-000000000033', 'JRN-2024-0015', '2024-05-15', 'POSTED', '2024-05-15 10:00:00', '50000000-0000-0000-0000-000000000101', 'Entry yang dihapus - SOFT DELETE', 7000000.00, 0.00, NOW(), NOW(), 'system', 'system', NOW()),
('90100000-0000-0000-0000-000000000034', 'JRN-2024-0015', '2024-05-15', 'POSTED', '2024-05-15 10:00:00', '10000000-0000-0000-0000-000000000101', 'Entry yang dihapus - SOFT DELETE', 0.00, 7000000.00, NOW(), NOW(), 'system', 'system', NOW());

-- =============================================================================
-- EXPECTED ACCOUNT BALANCES (POSTED entries only, as of 2024-06-30)
-- =============================================================================
-- NOTE: These totals include 2023 prior period entries but exclude:
--   - VOID entries (JRN-2024-0008)
--   - DRAFT entries (JRN-2024-0012)
--   - Future entries (JRN-2024-0013, JRN-2024-0014)
--   - Soft-deleted entries (JRN-2024-0015)
--
-- Account                          | Debit        | Credit       | Balance
-- ---------------------------------|--------------|--------------|-------------
-- Cash (1.1.01)                    | 150,000,000  | 16,000,000   | D 134,000,000
-- Bank BCA (1.1.02)                | 62,000,000   | 22,000,000   | D 40,000,000
-- Peralatan Komputer (1.2.01)      | 30,000,000   | 0            | D 30,000,000
-- Akum. Peny. Peralatan (1.2.02)   | 0            | 1,000,000    | C 1,000,000
-- Hutang Usaha (2.1.01)            | 0            | 10,000,000   | C 10,000,000
-- Modal Disetor (3.1.01)           | 0            | 150,000,000  | C 150,000,000 (100M 2024 + 50M 2023)
-- Pendapatan Jasa Konsultasi       | 0            | 37,000,000   | C 37,000,000 (27M 2024 + 10M 2023)
-- Pendapatan Jasa Development      | 0            | 25,000,000   | C 25,000,000
-- Beban Gaji (5.1.01)              | 16,000,000   | 0            | D 16,000,000
-- Beban Server & Cloud (5.1.02)    | 2,000,000    | 0            | D 2,000,000
-- Beban Penyusutan (5.1.07)        | 1,000,000    | 0            | D 1,000,000
-- ---------------------------------|--------------|--------------|-------------
-- TOTALS                           | 261,000,000  | 261,000,000  |
--
-- INCOME STATEMENT (2024-01-01 to 2024-06-30) - Current year only:
--   Total Revenue  = 52,000,000 (Konsultasi 27M + Development 25M)
--   Total Expense  = 19,000,000 (Gaji 16M + Server 2M + Penyusutan 1M)
--   Net Income     = 33,000,000
--
-- BALANCE SHEET (as of 2024-06-30):
--   Total Assets      = 203,000,000 (Cash 134M + BCA 40M + Peralatan 30M - Akum 1M)
--   Total Liabilities =  10,000,000 (Hutang Usaha)
--   Total Equity      = 150,000,000 (Modal Disetor 100M 2024 + 50M 2023)
--   Prior Year Earnings = 10,000,000 (2023 revenue from Konsultasi)
--   Current Year Net Income = 33,000,000
--   A = L + E + Prior + Current = 10M + 150M + 10M + 33M = 203M ✓
