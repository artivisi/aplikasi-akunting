-- ============================================
-- Test-only: Inventory Journal Templates
-- ============================================
-- These templates are only needed for inventory/production tests.
-- They are NOT included in production V004 to keep the database clean
-- for industries that don't use inventory (IT services, consulting, etc.)

-- Template: Pembelian Persediaan (Inventory Purchase)
-- Uses: inventoryAccount (dynamic), amount (purchase cost)
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('f5000000-0000-0000-0000-000000000001', 'Pembelian Persediaan', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat pembelian persediaan/barang. Variabel: amount (total pembelian)', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description, account_hint) VALUES
-- Debit: Inventory account (dynamic from product)
('f5100000-0000-0000-0000-000000000001', 'f5000000-0000-0000-0000-000000000001', NULL, 'DEBIT', 'amount', 1, 'Persediaan masuk', 'PERSEDIAAN'),
-- Credit: Bank/Cash
('f5100000-0000-0000-0000-000000000002', 'f5000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000102', 'CREDIT', 'amount', 2, 'Pembayaran dari bank', NULL);

-- Template: Penjualan Persediaan (Inventory Sale - COGS)
-- Uses: cogsAmount (cost of goods sold), revenueAmount (selling price), inventoryAccount, cogsAccount, salesAccount (all dynamic)
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('f5000000-0000-0000-0000-000000000002', 'Penjualan Persediaan', 'INCOME', 'OPERATING', 'DETAILED', 'Template untuk mencatat penjualan persediaan dengan HPP. Variabel: cogsAmount, revenueAmount', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description, account_hint) VALUES
-- Debit: Bank/Cash (revenue received)
('f5100000-0000-0000-0000-000000000003', 'f5000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'revenueAmount', 1, 'Penerimaan penjualan', NULL),
-- Debit: COGS (cost of goods sold)
('f5100000-0000-0000-0000-000000000004', 'f5000000-0000-0000-0000-000000000002', NULL, 'DEBIT', 'cogsAmount', 2, 'Harga pokok penjualan', 'HPP'),
-- Credit: Sales Revenue
('f5100000-0000-0000-0000-000000000005', 'f5000000-0000-0000-0000-000000000002', NULL, 'CREDIT', 'revenueAmount', 3, 'Pendapatan penjualan', 'PENJUALAN'),
-- Credit: Inventory (reduce inventory)
('f5100000-0000-0000-0000-000000000006', 'f5000000-0000-0000-0000-000000000002', NULL, 'CREDIT', 'cogsAmount', 4, 'Persediaan keluar', 'PERSEDIAAN');

-- Template: Penyesuaian Persediaan Masuk (Inventory Adjustment In)
-- Uses: amount (adjustment value)
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('f5000000-0000-0000-0000-000000000003', 'Penyesuaian Persediaan Masuk', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat penyesuaian persediaan masuk (stock opname lebih). Variabel: amount', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description, account_hint) VALUES
-- Debit: Inventory account
('f5100000-0000-0000-0000-000000000007', 'f5000000-0000-0000-0000-000000000003', NULL, 'DEBIT', 'amount', 1, 'Penyesuaian persediaan masuk', 'PERSEDIAAN'),
-- Credit: Other Income (inventory gain)
('f5100000-0000-0000-0000-000000000008', 'f5000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000121', 'CREDIT', 'amount', 2, 'Pendapatan penyesuaian persediaan', NULL);

-- Template: Penyesuaian Persediaan Keluar (Inventory Adjustment Out)
-- Uses: amount (adjustment value)
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active) VALUES
('f5000000-0000-0000-0000-000000000004', 'Penyesuaian Persediaan Keluar', 'EXPENSE', 'OPERATING', 'SIMPLE', 'Template untuk mencatat penyesuaian persediaan keluar (stock opname kurang). Variabel: amount', TRUE, TRUE);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order, description, account_hint) VALUES
-- Debit: Inventory shrinkage expense
('f5100000-0000-0000-0000-000000000009', 'f5000000-0000-0000-0000-000000000004', '50000000-0000-0000-0000-000000000121', 'DEBIT', 'amount', 1, 'Beban penyesuaian persediaan', NULL),
-- Credit: Inventory account
('f5100000-0000-0000-0000-000000000010', 'f5000000-0000-0000-0000-000000000004', NULL, 'CREDIT', 'amount', 2, 'Penyesuaian persediaan keluar', 'PERSEDIAAN');
