-- V004: Application Seed Data
-- Transaction sequences, Chart of Accounts, Journal Templates

-- ============================================
-- Transaction Sequences - Initialize for 2025
-- ============================================

INSERT INTO transaction_sequences (id, sequence_type, prefix, year, last_number) VALUES
('d0000000-0000-0000-0000-000000000001', 'TRANSACTION', 'TRX', 2025, 0),
('d0000000-0000-0000-0000-000000000002', 'JOURNAL', 'JE', 2025, 0);

-- ============================================
-- Chart of Accounts - IT Services
-- ============================================

-- ASET (1.x.xx) - Permanent accounts
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, level, is_header, active, is_permanent) VALUES
('10000000-0000-0000-0000-000000000001', '1', 'ASET', 'ASSET', 'DEBIT', 1, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('10000000-0000-0000-0000-000000000011', '1.1', 'Aset Lancar', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000012', '1.2', 'Aset Tetap', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000013', '1.3', 'Aset Tak Berwujud', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('10000000-0000-0000-0000-000000000101', '1.1.01', 'Kas', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000102', '1.1.02', 'Bank BCA', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000103', '1.1.03', 'Bank BNI', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000104', '1.1.04', 'Piutang Usaha', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000105', '1.1.05', 'Asuransi Dibayar Dimuka', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000106', '1.1.06', 'Sewa Dibayar Dimuka', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000107', '1.1.07', 'Langganan Dibayar Dimuka', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000108', '1.1.08', 'Piutang Pendapatan', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000121', '1.2.01', 'Peralatan Komputer', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000122', '1.2.02', 'Akum. Penyusutan Peralatan', 'ASSET', 'CREDIT', '10000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000131', '1.3.01', 'Website & Software', 'ASSET', 'DEBIT', '10000000-0000-0000-0000-000000000013', 3, FALSE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000132', '1.3.02', 'Akum. Amortisasi Aset Tak Berwujud', 'ASSET', 'CREDIT', '10000000-0000-0000-0000-000000000013', 3, FALSE, TRUE, TRUE);

-- LIABILITAS (2.x.xx) - Permanent accounts
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, level, is_header, active, is_permanent) VALUES
('20000000-0000-0000-0000-000000000001', '2', 'LIABILITAS', 'LIABILITY', 'CREDIT', 1, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('20000000-0000-0000-0000-000000000011', '2.1', 'Liabilitas Jangka Pendek', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('20000000-0000-0000-0000-000000000101', '2.1.01', 'Hutang Usaha', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000102', '2.1.02', 'Hutang Pajak', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000103', '2.1.03', 'Hutang PPN', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000104', '2.1.04', 'Pendapatan Diterima Dimuka', 'LIABILITY', 'CREDIT', '20000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE);

-- EKUITAS (3.x.xx) - Permanent accounts
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, level, is_header, active, is_permanent) VALUES
('30000000-0000-0000-0000-000000000001', '3', 'EKUITAS', 'EQUITY', 'CREDIT', 1, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('30000000-0000-0000-0000-000000000011', '3.1', 'Modal', 'EQUITY', 'CREDIT', '30000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE),
('30000000-0000-0000-0000-000000000012', '3.2', 'Laba', 'EQUITY', 'CREDIT', '30000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, TRUE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('30000000-0000-0000-0000-000000000101', '3.1.01', 'Modal Disetor', 'EQUITY', 'CREDIT', '30000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, TRUE),
('30000000-0000-0000-0000-000000000121', '3.2.01', 'Laba Ditahan', 'EQUITY', 'CREDIT', '30000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, TRUE),
('30000000-0000-0000-0000-000000000122', '3.2.02', 'Laba Berjalan', 'EQUITY', 'CREDIT', '30000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, TRUE);

-- PENDAPATAN (4.x.xx) - Temporary accounts
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, level, is_header, active, is_permanent) VALUES
('40000000-0000-0000-0000-000000000001', '4', 'PENDAPATAN', 'REVENUE', 'CREDIT', 1, TRUE, TRUE, FALSE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('40000000-0000-0000-0000-000000000011', '4.1', 'Pendapatan Usaha', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, FALSE),
('40000000-0000-0000-0000-000000000012', '4.2', 'Pendapatan Lain-lain', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, FALSE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('40000000-0000-0000-0000-000000000101', '4.1.01', 'Pendapatan Jasa Konsultasi', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('40000000-0000-0000-0000-000000000102', '4.1.02', 'Pendapatan Jasa Development', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('40000000-0000-0000-0000-000000000103', '4.1.03', 'Pendapatan Jasa Training', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('40000000-0000-0000-0000-000000000121', '4.2.01', 'Pendapatan Bunga', 'REVENUE', 'CREDIT', '40000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, FALSE);

-- BEBAN (5.x.xx) - Temporary accounts
INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, level, is_header, active, is_permanent) VALUES
('50000000-0000-0000-0000-000000000001', '5', 'BEBAN', 'EXPENSE', 'DEBIT', 1, TRUE, TRUE, FALSE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('50000000-0000-0000-0000-000000000011', '5.1', 'Beban Operasional', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000012', '5.2', 'Beban Lain-lain', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000001', 2, TRUE, TRUE, FALSE);

INSERT INTO chart_of_accounts (id, account_code, account_name, account_type, normal_balance, id_parent, level, is_header, active, is_permanent) VALUES
('50000000-0000-0000-0000-000000000101', '5.1.01', 'Beban Gaji', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000102', '5.1.02', 'Beban Server & Cloud', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000103', '5.1.03', 'Beban Software & Lisensi', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000104', '5.1.04', 'Beban Internet & Telekomunikasi', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000105', '5.1.05', 'Beban Administrasi & Umum', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000106', '5.1.06', 'Beban Sewa Kantor', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, FALSE, FALSE),
('50000000-0000-0000-0000-000000000107', '5.1.07', 'Beban Penyusutan', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000108', '5.1.08', 'Beban Asuransi', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000109', '5.1.09', 'Beban Amortisasi', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000011', 3, FALSE, TRUE, FALSE),
('50000000-0000-0000-0000-000000000121', '5.2.01', 'Beban Bank', 'EXPENSE', 'DEBIT', '50000000-0000-0000-0000-000000000012', 3, FALSE, TRUE, FALSE);

-- ============================================
-- Journal Templates - IT Services
-- ============================================

-- Template: Pendapatan Jasa Konsultasi
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000001', 'Pendapatan Jasa Konsultasi', 'INCOME', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pendapatan dari jasa konsultasi IT', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000001', 'e0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000002', 'e0000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000101', 'CREDIT', 'amount', 2);

-- Template: Pendapatan Jasa Development
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000002', 'Pendapatan Jasa Development', 'INCOME', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pendapatan dari jasa development', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000003', 'e0000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000004', 'e0000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Pendapatan Jasa Training
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000003', 'Pendapatan Jasa Training', 'INCOME', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pendapatan dari jasa training', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000005', 'e0000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000006', 'e0000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000103', 'CREDIT', 'amount', 2);

-- Template: Bayar Beban Gaji
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000004', 'Bayar Beban Gaji', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran gaji karyawan', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000007', 'e0000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000101', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000008', 'e0000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Bayar Beban Server & Cloud
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000005', 'Bayar Beban Server & Cloud', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran server dan cloud', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000009', 'e0000000-0000-0000-0000-000000000005', '50000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000010', 'e0000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Bayar Beban Software & Lisensi
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000006', 'Bayar Beban Software & Lisensi', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran software dan lisensi', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000011', 'e0000000-0000-0000-0000-000000000006', '50000000-0000-0000-0000-000000000103', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000012', 'e0000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Bayar Beban Internet
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000007', 'Bayar Beban Internet & Telekomunikasi', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran internet dan telekomunikasi', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000013', 'e0000000-0000-0000-0000-000000000007', '50000000-0000-0000-0000-000000000104', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000014', 'e0000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Bayar Beban Administrasi
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000008', 'Bayar Beban Administrasi & Umum', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran beban administrasi dan umum', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000015', 'e0000000-0000-0000-0000-000000000008', '50000000-0000-0000-0000-000000000105', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000016', 'e0000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Transfer Antar Bank
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000009', 'Transfer Antar Bank', 'TRANSFER', 'OPERATING', 'SIMPLE', 'Template untuk mencatat transfer antar rekening bank', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000017', 'e0000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000018', 'e0000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000103', 'CREDIT', 'amount', 2);

-- Template: Terima Pelunasan Piutang
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000010', 'Terima Pelunasan Piutang', 'RECEIPT', 'OPERATING', 'SIMPLE', 'Template untuk mencatat penerimaan pelunasan piutang', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000019', 'e0000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000020', 'e0000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000104', 'CREDIT', 'amount', 2);

-- Template: Bayar Hutang Usaha
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000011', 'Bayar Hutang Usaha', 'PAYMENT', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembayaran hutang usaha', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000021', 'e0000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000101', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000022', 'e0000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);

-- Template: Setoran Modal
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000012', 'Setoran Modal', 'RECEIPT', 'FINANCING', 'SIMPLE', 'Template untuk mencatat setoran modal dari pemilik', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000023', 'e0000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000024', 'e0000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000101', 'CREDIT', 'amount', 2);

-- Template: Pengakuan Pendapatan Proyek (Revenue Recognition from Advance Payment)
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('e0000000-0000-0000-0000-000000000013', 'Pengakuan Pendapatan Proyek', 'INCOME', 'OPERATING', 'SIMPLE', 'Template untuk mengakui pendapatan dari pembayaran dimuka saat milestone selesai', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('e1000000-0000-0000-0000-000000000025', 'e0000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000104', 'DEBIT', 'amount', 1),
('e1000000-0000-0000-0000-000000000026', 'e0000000-0000-0000-0000-000000000013', '40000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2);
