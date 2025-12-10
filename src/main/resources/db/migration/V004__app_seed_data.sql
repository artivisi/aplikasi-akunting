-- V004: Minimal Bootstrap Data
-- Transaction sequences for generating transaction and journal numbers
-- All industry-specific data (COA, templates, etc.) is loaded via seed import

-- ============================================
-- Transaction Sequences - Initialize for 2025
-- ============================================

INSERT INTO transaction_sequences (id, sequence_type, prefix, year, last_number) VALUES
('d0000000-0000-0000-0000-000000000001', 'TRANSACTION', 'TRX', 2025, 0),
('d0000000-0000-0000-0000-000000000002', 'JOURNAL', 'JE', 2025, 0);
