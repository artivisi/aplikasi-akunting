-- V903: Test templates with formula calculations for functional testing
-- Tests SpEL formula evaluation (per Decision #13)

-- Add test accounts for tax scenarios
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('20000000-0000-0000-0000-000000000105', '2.1.05', 'Hutang PPh 23', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000109', '1.1.09', 'PPN Masukan', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('50000000-0000-0000-0000-000000000110', '5.1.10', 'Beban Jasa Profesional', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE);

-- =====================================================
-- Template 1: Penjualan dengan PPN 11%
-- Scenario: Gross amount includes PPN
-- Input: Rp 11,100,000 (gross)
-- Output: DPP = 10,000,000, PPN = 1,100,000
-- =====================================================
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, is_favorite, active, version) VALUES
('f0000000-0000-0000-0000-000000000011', 'Penjualan Jasa dengan PPN', 'INCOME', 'OPERATING', 'SIMPLE',
 'Mencatat penjualan jasa dengan PPN 11%. Input jumlah gross (sudah termasuk PPN).', FALSE, FALSE, TRUE, 1);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description) VALUES
-- Debit Bank: full amount
('f1000000-0000-0000-0000-000000000011', 'f0000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1, 'Terima dari pelanggan'),
-- Credit Pendapatan: DPP = amount / 1.11
('f1000000-0000-0000-0000-000000000012', 'f0000000-0000-0000-0000-000000000011', '40000000-0000-0000-0000-000000000101', 'CREDIT', 'amount / 1.11', 2, 'DPP pendapatan'),
-- Credit PPN Keluaran: PPN = amount - (amount / 1.11)
('f1000000-0000-0000-0000-000000000013', 'f0000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000103', 'CREDIT', 'amount - (amount / 1.11)', 3, 'PPN Keluaran 11%');

-- =====================================================
-- Template 2: Pembelian Perlengkapan dengan PPN 11%
-- Scenario: Purchase with input VAT
-- Input: Rp 5,550,000 (gross including PPN)
-- Output: DPP = 5,000,000, PPN Masukan = 550,000
-- =====================================================
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, is_favorite, active, version) VALUES
('f0000000-0000-0000-0000-000000000012', 'Pembelian dengan PPN', 'EXPENSE', 'OPERATING', 'SIMPLE',
 'Mencatat pembelian dengan PPN 11% masukan. Input jumlah gross.', FALSE, FALSE, TRUE, 1);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description) VALUES
-- Debit Perlengkapan: DPP
('f1000000-0000-0000-0000-000000000021', 'f0000000-0000-0000-0000-000000000012', '50000000-0000-0000-0000-000000000105', 'DEBIT', 'amount / 1.11', 1, 'DPP pembelian'),
-- Debit PPN Masukan
('f1000000-0000-0000-0000-000000000022', 'f0000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000109', 'DEBIT', 'amount - (amount / 1.11)', 2, 'PPN Masukan 11%'),
-- Credit Bank: full amount
('f1000000-0000-0000-0000-000000000023', 'f0000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 3, 'Bayar ke supplier');

-- =====================================================
-- Template 3: Bayar Jasa dengan PPh 23 (Conditional)
-- Scenario: Pay for professional services with WHT if > 2,000,000
-- Input: Rp 5,000,000
-- Output: PPh 23 = 100,000 (2%), Net payment = 4,900,000
-- If input <= 2,000,000: No PPh 23 deduction
-- =====================================================
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, is_favorite, active, version) VALUES
('f0000000-0000-0000-0000-000000000013', 'Bayar Jasa dengan PPh 23', 'EXPENSE', 'OPERATING', 'SIMPLE',
 'Mencatat pembayaran jasa profesional dengan pemotongan PPh 23 (2%) jika > Rp 2.000.000.', FALSE, FALSE, TRUE, 1);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description) VALUES
-- Debit Beban Jasa: full amount
('f1000000-0000-0000-0000-000000000031', 'f0000000-0000-0000-0000-000000000013', '50000000-0000-0000-0000-000000000110', 'DEBIT', 'amount', 1, 'Beban jasa profesional'),
-- Credit Bank: amount - PPh23 (if applicable)
('f1000000-0000-0000-0000-000000000032', 'f0000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount - (amount > 2000000 ? amount * 0.02 : 0)', 2, 'Pembayaran ke vendor'),
-- Credit Hutang PPh 23: conditional
('f1000000-0000-0000-0000-000000000033', 'f0000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000105', 'CREDIT', 'amount > 2000000 ? amount * 0.02 : 0', 3, 'PPh 23 (2% jika > 2jt)');

-- =====================================================
-- Template 4: Gaji dengan Potongan Tetap
-- Scenario: Salary with fixed BPJS deduction
-- Input: Rp 8,000,000
-- Output: BPJS = 320,000 (fixed), Net = 7,680,000
-- =====================================================
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, is_favorite, active, version) VALUES
('f0000000-0000-0000-0000-000000000014', 'Bayar Gaji (Potongan Tetap)', 'EXPENSE', 'OPERATING', 'SIMPLE',
 'Mencatat pembayaran gaji dengan potongan BPJS tetap Rp 320.000.', FALSE, FALSE, TRUE, 1);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description) VALUES
-- Debit Beban Gaji: full amount
('f1000000-0000-0000-0000-000000000041', 'f0000000-0000-0000-0000-000000000014', '50000000-0000-0000-0000-000000000101', 'DEBIT', 'amount', 1, 'Gaji karyawan'),
-- Credit Bank: amount - 320000
('f1000000-0000-0000-0000-000000000042', 'f0000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount - 320000', 2, 'Transfer gaji bersih'),
-- Credit Hutang BPJS (using Hutang Pajak as placeholder)
('f1000000-0000-0000-0000-000000000043', 'f0000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000102', 'CREDIT', '320000', 3, 'Potongan BPJS');
