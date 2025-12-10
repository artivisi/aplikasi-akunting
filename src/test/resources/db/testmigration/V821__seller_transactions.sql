-- V821: Online Seller Transactions
-- Purchase, Sales, Marketplace transactions with FIFO costing
-- ID Convention: 52=Seller, 40=Transaction, 70=Journal Entry

-- ============================================
-- Inventory Transactions - Purchases
-- ============================================

-- Purchase 10 iPhone 15 Pro @ Rp 15,000,000 each from Erajaya
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400001-0000-0000-0000-000000000001', '52200001-0000-0000-0000-000000000001', 'PURCHASE', '2024-01-05', 10, 15000000, 150000000, 'PO-001', 'Purchase iPhone 15 Pro dari Erajaya', 10, 150000000, NOW(), 'admin');

-- Create FIFO layer for iPhone purchase
INSERT INTO inventory_fifo_layers (id, id_product, id_inventory_transaction, layer_date, original_quantity, remaining_quantity, unit_cost, fully_consumed)
VALUES ('52600001-0000-0000-0000-000000000001', '52200001-0000-0000-0000-000000000001', '52400001-0000-0000-0000-000000000001', '2024-01-05', 10, 10, 15000000, false);

-- Update inventory balance for iPhone
UPDATE inventory_balances SET quantity = 10, total_cost = 150000000, average_cost = 15000000, last_transaction_date = '2024-01-05' WHERE id = '52050001-0000-0000-0000-000000000001';

-- Purchase 20 Samsung Galaxy S24 @ Rp 12,000,000 each from Samsung Indonesia
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400002-0000-0000-0000-000000000001', '52200002-0000-0000-0000-000000000001', 'PURCHASE', '2024-01-08', 20, 12000000, 240000000, 'PO-002', 'Purchase Samsung S24 dari Samsung Indonesia', 20, 240000000, NOW(), 'admin');

-- Create FIFO layer for Samsung purchase
INSERT INTO inventory_fifo_layers (id, id_product, id_inventory_transaction, layer_date, original_quantity, remaining_quantity, unit_cost, fully_consumed)
VALUES ('52600002-0000-0000-0000-000000000001', '52200002-0000-0000-0000-000000000001', '52400002-0000-0000-0000-000000000001', '2024-01-08', 20, 20, 12000000, false);

-- Update inventory balance for Samsung
UPDATE inventory_balances SET quantity = 20, total_cost = 240000000, average_cost = 12000000, last_transaction_date = '2024-01-08' WHERE id = '52050002-0000-0000-0000-000000000001';

-- Purchase 100 USB Cable @ Rp 25,000 each
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400003-0000-0000-0000-000000000001', '52200003-0000-0000-0000-000000000001', 'PURCHASE', '2024-01-10', 100, 25000, 2500000, 'PO-003', 'Purchase USB Cable dari supplier', 100, 2500000, NOW(), 'admin');

-- Update inventory balance for USB Cable (weighted average)
UPDATE inventory_balances SET quantity = 100, total_cost = 2500000, average_cost = 25000, last_transaction_date = '2024-01-10' WHERE id = '52050003-0000-0000-0000-000000000001';

-- Purchase 200 Phone Cases @ Rp 15,000 each
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400004-0000-0000-0000-000000000001', '52200004-0000-0000-0000-000000000001', 'PURCHASE', '2024-01-10', 200, 15000, 3000000, 'PO-004', 'Purchase Phone Case dari supplier', 200, 3000000, NOW(), 'admin');

-- Update inventory balance for Phone Case (weighted average)
UPDATE inventory_balances SET quantity = 200, total_cost = 3000000, average_cost = 15000, last_transaction_date = '2024-01-10' WHERE id = '52050004-0000-0000-0000-000000000001';

-- ============================================
-- Inventory Transactions - Sales
-- ============================================

-- Sale 5 iPhone 15 Pro via Tokopedia (FIFO COGS: 5 x 15M = 75M)
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, unit_price, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400005-0000-0000-0000-000000000001', '52200001-0000-0000-0000-000000000001', 'SALE', '2024-01-15', 5, 15000000, 75000000, 19000000, 'TOPED-001', 'Sale iPhone via Tokopedia', 5, 75000000, NOW(), 'admin');

-- Update FIFO layer (reduce remaining qty)
UPDATE inventory_fifo_layers SET remaining_quantity = 5 WHERE id = '52600001-0000-0000-0000-000000000001';

-- Update inventory balance for iPhone
UPDATE inventory_balances SET quantity = 5, total_cost = 75000000, average_cost = 15000000, last_transaction_date = '2024-01-15' WHERE id = '52050001-0000-0000-0000-000000000001';

-- Sale 8 Samsung S24 via Shopee (FIFO COGS: 8 x 12M = 96M)
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, unit_price, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400006-0000-0000-0000-000000000001', '52200002-0000-0000-0000-000000000001', 'SALE', '2024-01-20', 8, 12000000, 96000000, 14000000, 'SHOPEE-001', 'Sale Samsung via Shopee', 12, 144000000, NOW(), 'admin');

-- Update FIFO layer (reduce remaining qty)
UPDATE inventory_fifo_layers SET remaining_quantity = 12 WHERE id = '52600002-0000-0000-0000-000000000001';

-- Update inventory balance for Samsung
UPDATE inventory_balances SET quantity = 12, total_cost = 144000000, average_cost = 12000000, last_transaction_date = '2024-01-20' WHERE id = '52050002-0000-0000-0000-000000000001';

-- Sale 30 USB Cables via Tokopedia (WA COGS: 30 x 25K = 750K)
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, unit_price, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400007-0000-0000-0000-000000000001', '52200003-0000-0000-0000-000000000001', 'SALE', '2024-01-22', 30, 25000, 750000, 50000, 'TOPED-002', 'Sale USB Cable via Tokopedia', 70, 1750000, NOW(), 'admin');

-- Update inventory balance for USB Cable
UPDATE inventory_balances SET quantity = 70, total_cost = 1750000, average_cost = 25000, last_transaction_date = '2024-01-22' WHERE id = '52050003-0000-0000-0000-000000000001';

-- Sale 50 Phone Cases via Shopee (WA COGS: 50 x 15K = 750K)
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, unit_price, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400008-0000-0000-0000-000000000001', '52200004-0000-0000-0000-000000000001', 'SALE', '2024-01-25', 50, 15000, 750000, 35000, 'SHOPEE-002', 'Sale Phone Case via Shopee', 150, 2250000, NOW(), 'admin');

-- Update inventory balance for Phone Case
UPDATE inventory_balances SET quantity = 150, total_cost = 2250000, average_cost = 15000, last_transaction_date = '2024-01-25' WHERE id = '52050004-0000-0000-0000-000000000001';

-- ============================================
-- Inventory Adjustment (Stock Opname)
-- ============================================
INSERT INTO inventory_transactions (id, id_product, transaction_type, transaction_date, quantity, unit_cost, total_cost, reference_number, notes, balance_after, total_cost_after, created_at, created_by)
VALUES ('52400009-0000-0000-0000-000000000001', '52200003-0000-0000-0000-000000000001', 'ADJUSTMENT_IN', '2024-01-28', 5, 25000, 125000, 'SO-001', 'Stock opname adjustment - found 5 extra USB cables', 75, 1875000, NOW(), 'admin');

-- Update inventory balance for USB Cable after adjustment
UPDATE inventory_balances SET quantity = 75, total_cost = 1875000, average_cost = 25000, last_transaction_date = '2024-01-28' WHERE id = '52050003-0000-0000-0000-000000000001';
