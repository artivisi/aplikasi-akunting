-- V906: Invoice Test Data for Print Preview
-- Test data for Invoice print preview testing
-- This migration runs only in test profile

-- =============================================================================
-- UPDATE COMPANY CONFIG for better preview
-- =============================================================================
UPDATE company_config SET
    company_name = 'PT Artivisi Intermedia',
    company_address = 'Jl. Margonda Raya No. 123, Depok, Jawa Barat 16424',
    company_phone = '021-7712345',
    company_email = 'info@artivisi.com',
    tax_id = '01.234.567.8-012.000',
    signing_officer_name = 'Endy Muhardin',
    signing_officer_title = 'Direktur'
WHERE id IS NOT NULL;

-- =============================================================================
-- COMPANY BANK ACCOUNT
-- =============================================================================
INSERT INTO company_bank_accounts (id, bank_name, account_number, account_name, bank_branch, is_default, active, created_at, updated_at)
VALUES
('b0600000-0000-0000-0000-000000000001', 'Bank Central Asia (BCA)', '1234567890', 'PT Artivisi Intermedia', 'KCP Margonda', true, true, NOW(), NOW());

-- =============================================================================
-- INVOICES
-- =============================================================================
INSERT INTO invoices (id, invoice_number, id_client, id_project, invoice_date, due_date, amount, status, notes, created_at, updated_at)
VALUES
-- Invoice 1: Development payment for PT ABC
('f0600000-0000-0000-0000-000000000001', 'INV-2024-001',
 'c0500000-0000-0000-0000-000000000001',
 'a0500000-0000-0000-0000-000000000001',
 '2024-11-01', '2024-11-30',
 15000000.00, 'SENT',
 'Pembayaran milestone 1 - Website Development
Termasuk:
- Analisis kebutuhan
- Desain UI/UX
- Development frontend',
 NOW(), NOW()),

-- Invoice 2: Consulting payment for PT XYZ
('f0600000-0000-0000-0000-000000000002', 'INV-2024-002',
 'c0500000-0000-0000-0000-000000000002',
 'a0500000-0000-0000-0000-000000000003',
 '2024-11-15', '2024-12-15',
 18000000.00, 'DRAFT',
 'IT Strategy Consulting - Phase 1
Deliverables:
- IT Assessment Report
- Technology Roadmap
- Implementation Plan',
 NOW(), NOW()),

-- Invoice 3: Paid invoice
('f0600000-0000-0000-0000-000000000003', 'INV-2024-003',
 'c0500000-0000-0000-0000-000000000001',
 'a0500000-0000-0000-0000-000000000002',
 '2024-10-01', '2024-10-31',
 12000000.00, 'PAID',
 'Mobile App Development - Final Payment',
 NOW(), NOW());
