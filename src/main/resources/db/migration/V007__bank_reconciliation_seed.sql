-- V007: Bank Reconciliation Seed Data
-- Preloaded parser configs for major Indonesian banks

-- BCA (Bank Central Asia)
-- CSV format: Date, Description, Branch, Amount (negative=debit), Balance
INSERT INTO bank_statement_parser_configs (id, bank_type, config_name, description, date_column, description_column, debit_column, credit_column, balance_column, date_format, delimiter, skip_header_rows, encoding, decimal_separator, thousand_separator, is_system, active)
VALUES (gen_random_uuid(), 'BCA', 'BCA - CSV Standar', 'Format CSV standar dari BCA KlikBCA/myBCA', 0, 1, 3, 4, 5, 'dd/MM/yyyy', ',', 1, 'UTF-8', '.', ',', TRUE, TRUE);

-- Mandiri
-- CSV format: Date, Description, Debit, Credit, Balance
INSERT INTO bank_statement_parser_configs (id, bank_type, config_name, description, date_column, description_column, debit_column, credit_column, balance_column, date_format, delimiter, skip_header_rows, encoding, decimal_separator, thousand_separator, is_system, active)
VALUES (gen_random_uuid(), 'MANDIRI', 'Mandiri - CSV Standar', 'Format CSV standar dari Mandiri Online/Livin', 0, 1, 2, 3, 4, 'dd/MM/yyyy', ',', 1, 'UTF-8', '.', ',', TRUE, TRUE);

-- BNI (Bank Negara Indonesia)
-- CSV format: Date, Description, Branch, Debit, Credit, Balance
INSERT INTO bank_statement_parser_configs (id, bank_type, config_name, description, date_column, description_column, debit_column, credit_column, balance_column, date_format, delimiter, skip_header_rows, encoding, decimal_separator, thousand_separator, is_system, active)
VALUES (gen_random_uuid(), 'BNI', 'BNI - CSV Standar', 'Format CSV standar dari BNI Internet Banking', 0, 1, 3, 4, 5, 'dd/MM/yyyy', ',', 1, 'UTF-8', '.', ',', TRUE, TRUE);

-- BSI (Bank Syariah Indonesia)
-- CSV format: Date, Description, Debit, Credit, Balance
INSERT INTO bank_statement_parser_configs (id, bank_type, config_name, description, date_column, description_column, debit_column, credit_column, balance_column, date_format, delimiter, skip_header_rows, encoding, decimal_separator, thousand_separator, is_system, active)
VALUES (gen_random_uuid(), 'BSI', 'BSI - CSV Standar', 'Format CSV standar dari BSI Mobile/Net Banking', 0, 1, 2, 3, 4, 'dd/MM/yyyy', ',', 1, 'UTF-8', '.', ',', TRUE, TRUE);

-- CIMB Niaga
-- CSV format: Date, Description, Debit, Credit, Balance
INSERT INTO bank_statement_parser_configs (id, bank_type, config_name, description, date_column, description_column, debit_column, credit_column, balance_column, date_format, delimiter, skip_header_rows, encoding, decimal_separator, thousand_separator, is_system, active)
VALUES (gen_random_uuid(), 'CIMB', 'CIMB Niaga - CSV Standar', 'Format CSV standar dari CIMB Niaga OCTO', 0, 1, 2, 3, 4, 'dd/MM/yyyy', ',', 1, 'UTF-8', '.', ',', TRUE, TRUE);

-- Analysis Reports table
-- Stores structured AI-generated financial analysis reports
CREATE TABLE analysis_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    row_version BIGINT NOT NULL DEFAULT 0,
    title VARCHAR(255) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    industry VARCHAR(50),
    period_start DATE,
    period_end DATE,
    ai_source VARCHAR(50),
    ai_model VARCHAR(100),
    executive_summary TEXT,
    metrics JSONB,
    findings JSONB,
    recommendations JSONB,
    risks JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);
