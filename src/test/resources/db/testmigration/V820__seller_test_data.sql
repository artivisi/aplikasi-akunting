-- V820: Online Seller Test Data
-- Company: Toko Gadget Murah (Electronics Reseller)
-- ID Convention: 52=Seller, 10=Client/Supplier, 20=Product, 30=Employee, etc.

-- ============================================
-- Product Categories
-- ============================================
INSERT INTO product_categories (id, code, name, description, active)
VALUES
    ('52010001-0000-0000-0000-000000000001', 'PHONE', 'Smartphone', 'Mobile phones and tablets', true),
    ('52010002-0000-0000-0000-000000000001', 'ACC', 'Accessories', 'Phone accessories and cables', true);

-- ============================================
-- Products
-- ============================================
INSERT INTO products (id, code, name, description, unit, id_category, costing_method, selling_price, minimum_stock, active)
VALUES
    ('52200001-0000-0000-0000-000000000001', 'IP15PRO', 'iPhone 15 Pro 256GB', 'Apple iPhone 15 Pro 256GB Natural Titanium', 'UNIT', '52010001-0000-0000-0000-000000000001', 'FIFO', 19500000, 5, true),
    ('52200002-0000-0000-0000-000000000001', 'SGS24', 'Samsung Galaxy S24 256GB', 'Samsung Galaxy S24 256GB Onyx Black', 'UNIT', '52010001-0000-0000-0000-000000000001', 'FIFO', 14500000, 5, true),
    ('52200003-0000-0000-0000-000000000001', 'USBC', 'USB Cable Type-C 1M', 'Fast charging USB-C cable 1 meter', 'PCS', '52010002-0000-0000-0000-000000000001', 'WEIGHTED_AVERAGE', 50000, 50, true),
    ('52200004-0000-0000-0000-000000000001', 'CASE', 'Phone Case Universal', 'Silicone phone case universal fit', 'PCS', '52010002-0000-0000-0000-000000000001', 'WEIGHTED_AVERAGE', 35000, 100, true);

-- ============================================
-- Inventory Balances (initial state)
-- ============================================
INSERT INTO inventory_balances (id, id_product, quantity, total_cost, average_cost)
VALUES
    ('52050001-0000-0000-0000-000000000001', '52200001-0000-0000-0000-000000000001', 0, 0, 0),
    ('52050002-0000-0000-0000-000000000001', '52200002-0000-0000-0000-000000000001', 0, 0, 0),
    ('52050003-0000-0000-0000-000000000001', '52200003-0000-0000-0000-000000000001', 0, 0, 0),
    ('52050004-0000-0000-0000-000000000001', '52200004-0000-0000-0000-000000000001', 0, 0, 0);

-- ============================================
-- Suppliers (stored as clients)
-- ============================================
INSERT INTO clients (id, code, name, address, phone, email, npwp, contact_person, active)
VALUES
    ('52100001-0000-0000-0000-000000000001', 'SUP-001', 'PT Erajaya Swasembada', 'Gedung Erajaya, Jakarta', '021-5551234', 'procurement@erajaya.com', '01.234.567.8-091.000', 'Budi Hartono', true),
    ('52100002-0000-0000-0000-000000000001', 'SUP-002', 'PT Samsung Electronics Indonesia', 'Samsung Electronics Building, Cikarang', '021-5552345', 'b2b@samsung.co.id', '02.345.678.9-012.000', 'Kim Min-jun', true);

-- ============================================
-- Marketplaces (as customers/channels)
-- ============================================
INSERT INTO clients (id, code, name, address, phone, email, npwp, contact_person, active)
VALUES
    ('52100003-0000-0000-0000-000000000001', 'MKT-001', 'Tokopedia', 'Tokopedia Tower, Jakarta', '021-5553456', 'seller@tokopedia.com', '03.456.789.0-123.000', 'Seller Support', true),
    ('52100004-0000-0000-0000-000000000001', 'MKT-002', 'Shopee Indonesia', 'Shopee Office, Jakarta', '021-5554567', 'seller@shopee.co.id', '04.567.890.1-234.000', 'Seller Support', true);

-- ============================================
-- Employees
-- ============================================
INSERT INTO employees (id, employee_id, name, email, phone, address, hire_date, npwp, nik_ktp, ptkp_status, employment_type, employment_status, job_title, department, bank_name, bank_account_number, bank_account_name)
VALUES
    ('52300001-0000-0000-0000-000000000001', 'SLR-001', 'Rina Susanti', 'rina.susanti@gadgetmurah.com', '081234567890', 'Jl. Raya Depok No. 100', '2023-01-15', '05.678.901.2-345.000', '3276012345670001', 'K_1', 'PERMANENT', 'ACTIVE', 'Store Manager', 'Operations', 'BCA', '1234567890', 'Rina Susanti'),
    ('52300002-0000-0000-0000-000000000001', 'SLR-002', 'Dian Pratama', 'dian.pratama@gadgetmurah.com', '081234567891', 'Jl. Margonda Raya No. 50', '2023-06-01', '06.789.012.3-456.000', '3276012345670002', 'TK_0', 'PERMANENT', 'ACTIVE', 'Sales Staff', 'Operations', 'BCA', '1234567891', 'Dian Pratama');

-- ============================================
-- Fiscal Periods 2024 - Shared with V810 (Service Industry)
-- Not creating duplicate fiscal periods since they're already created in V810
-- ============================================
