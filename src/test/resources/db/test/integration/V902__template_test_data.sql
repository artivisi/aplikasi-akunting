-- V902: Test data for Journal Templates (non-system templates for testing CRUD)

-- Test Template 1: Non-system template for edit testing
-- This template can be edited and deleted in tests
INSERT INTO journal_templates (id, template_name, category, cash_flow_category, template_type, description, is_system, active, version) VALUES
('f0000000-0000-0000-0000-000000000001', 'Test Template - Editable', 'TRANSFER', 'OPERATING', 'SIMPLE', 'Template untuk testing edit dan delete', FALSE, TRUE, 1);

INSERT INTO journal_template_lines (id, id_journal_template, id_account, position, formula, line_order) VALUES
('f1000000-0000-0000-0000-000000000001', 'f0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000102', 'DEBIT', 'amount', 1),
('f1000000-0000-0000-0000-000000000002', 'f0000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000103', 'CREDIT', 'amount', 2);
