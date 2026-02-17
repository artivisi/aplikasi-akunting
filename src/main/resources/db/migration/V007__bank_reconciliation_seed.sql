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
