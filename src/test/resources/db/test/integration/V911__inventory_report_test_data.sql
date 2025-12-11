-- V911: Inventory Report Test Data
-- Test data for inventory report functional tests

-- Product Categories for testing
INSERT INTO product_categories (id, code, name, active, created_at, updated_at)
VALUES
    ('d0911001-0000-0000-0000-000000000001', 'CAT-BAHAN', 'Bahan Baku', true, NOW(), NOW()),
    ('d0911001-0000-0000-0000-000000000002', 'CAT-PRODUK', 'Produk Jadi', true, NOW(), NOW());

-- Products for testing
INSERT INTO products (id, code, name, unit, costing_method, id_category, minimum_stock, active, created_at, updated_at)
VALUES
    ('d0911002-0000-0000-0000-000000000001', 'PRD-TEST-001', 'Tepung Terigu', 'kg', 'WEIGHTED_AVERAGE', 'd0911001-0000-0000-0000-000000000001', 10, true, NOW(), NOW()),
    ('d0911002-0000-0000-0000-000000000002', 'PRD-TEST-002', 'Gula Pasir', 'kg', 'WEIGHTED_AVERAGE', 'd0911001-0000-0000-0000-000000000001', 5, true, NOW(), NOW()),
    ('d0911002-0000-0000-0000-000000000003', 'PRD-TEST-003', 'Kue Bolu', 'pcs', 'WEIGHTED_AVERAGE', 'd0911001-0000-0000-0000-000000000002', 0, true, NOW(), NOW());

-- Inventory Balances
INSERT INTO inventory_balances (id, id_product, quantity, total_cost, average_cost, created_at, updated_at)
VALUES
    ('d0911003-0000-0000-0000-000000000001', 'd0911002-0000-0000-0000-000000000001', 50.00, 500000.00, 10000.00, NOW(), NOW()),
    ('d0911003-0000-0000-0000-000000000002', 'd0911002-0000-0000-0000-000000000002', 25.00, 375000.00, 15000.00, NOW(), NOW()),
    ('d0911003-0000-0000-0000-000000000003', 'd0911002-0000-0000-0000-000000000003', 20.00, 600000.00, 30000.00, NOW(), NOW());

-- Inventory Transactions (for stock movement report)
-- Note: balance_after and total_cost_after are running totals after each transaction
INSERT INTO inventory_transactions (id, id_product, transaction_date, transaction_type, quantity, unit_cost, total_cost, balance_after, total_cost_after, reference_number, created_at, updated_at)
VALUES
    -- Tepung Terigu purchases
    ('d0911004-0000-0000-0000-000000000001', 'd0911002-0000-0000-0000-000000000001', '2024-01-05', 'PURCHASE', 30.00, 10000.00, 300000.00, 30.00, 300000.00, 'PO-001', NOW(), NOW()),
    ('d0911004-0000-0000-0000-000000000002', 'd0911002-0000-0000-0000-000000000001', '2024-01-15', 'PURCHASE', 40.00, 10000.00, 400000.00, 70.00, 700000.00, 'PO-002', NOW(), NOW()),
    ('d0911004-0000-0000-0000-000000000003', 'd0911002-0000-0000-0000-000000000001', '2024-01-20', 'PRODUCTION_OUT', 20.00, 10000.00, 200000.00, 50.00, 500000.00, 'PROD-001', NOW(), NOW()),
    -- Gula Pasir purchases
    ('d0911004-0000-0000-0000-000000000004', 'd0911002-0000-0000-0000-000000000002', '2024-01-06', 'PURCHASE', 20.00, 15000.00, 300000.00, 20.00, 300000.00, 'PO-003', NOW(), NOW()),
    ('d0911004-0000-0000-0000-000000000005', 'd0911002-0000-0000-0000-000000000002', '2024-01-18', 'PURCHASE', 15.00, 15000.00, 225000.00, 35.00, 525000.00, 'PO-004', NOW(), NOW()),
    ('d0911004-0000-0000-0000-000000000006', 'd0911002-0000-0000-0000-000000000002', '2024-01-20', 'PRODUCTION_OUT', 10.00, 15000.00, 150000.00, 25.00, 375000.00, 'PROD-001', NOW(), NOW()),
    -- Kue Bolu production and sales
    ('d0911004-0000-0000-0000-000000000007', 'd0911002-0000-0000-0000-000000000003', '2024-01-20', 'PRODUCTION_IN', 30.00, 30000.00, 900000.00, 30.00, 900000.00, 'PROD-001', NOW(), NOW()),
    ('d0911004-0000-0000-0000-000000000008', 'd0911002-0000-0000-0000-000000000003', '2024-01-25', 'SALE', 10.00, 30000.00, 300000.00, 20.00, 600000.00, 'INV-001', NOW(), NOW());

-- Update the sale transaction with unit_price for profitability report
UPDATE inventory_transactions
SET unit_price = 50000.00
WHERE id = 'd0911004-0000-0000-0000-000000000008';
