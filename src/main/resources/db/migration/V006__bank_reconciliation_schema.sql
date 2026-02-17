-- V006: Bank Reconciliation Schema
-- Add GL account link to company_bank_accounts and create bank reconciliation tables

-- ============================================
-- Modify company_bank_accounts: add GL account link
-- ============================================

ALTER TABLE company_bank_accounts ADD COLUMN IF NOT EXISTS id_account UUID REFERENCES chart_of_accounts(id);

CREATE INDEX IF NOT EXISTS idx_company_bank_account ON company_bank_accounts(id_account);

COMMENT ON COLUMN company_bank_accounts.id_account IS 'Link to chart of accounts GL entry for book balance computation';

-- ============================================
-- Bank Statement Parser Configs
-- ============================================

CREATE TABLE bank_statement_parser_configs (
    id UUID PRIMARY KEY,
    row_version BIGINT NOT NULL DEFAULT 0,
    bank_type VARCHAR(20) NOT NULL,
    config_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    date_column INTEGER NOT NULL,
    description_column INTEGER NOT NULL,
    debit_column INTEGER,
    credit_column INTEGER,
    balance_column INTEGER,
    date_format VARCHAR(50) NOT NULL,
    delimiter VARCHAR(5) NOT NULL DEFAULT ',',
    skip_header_rows INTEGER NOT NULL DEFAULT 1,
    encoding VARCHAR(20) DEFAULT 'UTF-8',
    decimal_separator VARCHAR(5) DEFAULT '.',
    thousand_separator VARCHAR(5) DEFAULT ',',
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,

    CONSTRAINT chk_parser_bank_type CHECK (bank_type IN ('BCA', 'MANDIRI', 'BNI', 'BSI', 'CIMB', 'CUSTOM'))
);

CREATE INDEX idx_parser_config_bank_type ON bank_statement_parser_configs(bank_type);
CREATE INDEX idx_parser_config_active ON bank_statement_parser_configs(active);

-- ============================================
-- Bank Statements
-- ============================================

CREATE TABLE bank_statements (
    id UUID PRIMARY KEY,
    row_version BIGINT NOT NULL DEFAULT 0,
    id_bank_account UUID NOT NULL REFERENCES company_bank_accounts(id),
    id_parser_config UUID NOT NULL REFERENCES bank_statement_parser_configs(id),
    statement_period_start DATE NOT NULL,
    statement_period_end DATE NOT NULL,
    opening_balance DECIMAL(19, 2),
    closing_balance DECIMAL(19, 2),
    original_filename VARCHAR(500) NOT NULL,
    total_items INTEGER,
    total_debit DECIMAL(19, 2),
    total_credit DECIMAL(19, 2),
    imported_at TIMESTAMP,
    imported_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP
);

CREATE INDEX idx_bs_bank_account ON bank_statements(id_bank_account);
CREATE INDEX idx_bs_period ON bank_statements(statement_period_start, statement_period_end);

-- ============================================
-- Bank Statement Items
-- ============================================

CREATE TABLE bank_statement_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_bank_statement UUID NOT NULL REFERENCES bank_statements(id) ON DELETE CASCADE,
    line_number INTEGER NOT NULL,
    transaction_date DATE NOT NULL,
    description TEXT,
    debit_amount DECIMAL(19, 2),
    credit_amount DECIMAL(19, 2),
    balance DECIMAL(19, 2),
    raw_line TEXT,
    match_status VARCHAR(20) NOT NULL DEFAULT 'UNMATCHED',
    match_type VARCHAR(20),
    id_matched_transaction UUID REFERENCES transactions(id),
    matched_at TIMESTAMP,
    matched_by VARCHAR(100),

    CONSTRAINT chk_bsi_match_status CHECK (match_status IN ('UNMATCHED', 'MATCHED', 'BANK_ONLY', 'BOOK_ONLY')),
    CONSTRAINT chk_bsi_match_type CHECK (match_type IS NULL OR match_type IN ('EXACT', 'FUZZY_DATE', 'KEYWORD', 'MANUAL'))
);

CREATE INDEX idx_bsi_statement ON bank_statement_items(id_bank_statement);
CREATE INDEX idx_bsi_match_status ON bank_statement_items(match_status);
CREATE INDEX idx_bsi_transaction_date ON bank_statement_items(transaction_date);
CREATE INDEX idx_bsi_matched_txn ON bank_statement_items(id_matched_transaction);

-- ============================================
-- Bank Reconciliations
-- ============================================

CREATE TABLE bank_reconciliations (
    id UUID PRIMARY KEY,
    row_version BIGINT NOT NULL DEFAULT 0,
    id_bank_account UUID NOT NULL REFERENCES company_bank_accounts(id),
    id_bank_statement UUID NOT NULL REFERENCES bank_statements(id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    book_balance DECIMAL(19, 2),
    bank_balance DECIMAL(19, 2),
    total_statement_items INTEGER,
    matched_count INTEGER,
    unmatched_bank_count INTEGER,
    unmatched_book_count INTEGER,
    completed_at TIMESTAMP,
    completed_by VARCHAR(100),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,

    CONSTRAINT chk_recon_status CHECK (status IN ('DRAFT', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX idx_recon_bank_account ON bank_reconciliations(id_bank_account);
CREATE INDEX idx_recon_statement ON bank_reconciliations(id_bank_statement);
CREATE INDEX idx_recon_status ON bank_reconciliations(status);
CREATE INDEX idx_recon_period ON bank_reconciliations(period_start, period_end);

-- ============================================
-- Reconciliation Items
-- ============================================

CREATE TABLE reconciliation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_reconciliation UUID NOT NULL REFERENCES bank_reconciliations(id) ON DELETE CASCADE,
    id_statement_item UUID REFERENCES bank_statement_items(id),
    id_transaction UUID REFERENCES transactions(id),
    match_status VARCHAR(20) NOT NULL,
    match_type VARCHAR(20),
    match_confidence DECIMAL(5, 2),
    notes VARCHAR(500),

    CONSTRAINT chk_ri_match_status CHECK (match_status IN ('UNMATCHED', 'MATCHED', 'BANK_ONLY', 'BOOK_ONLY')),
    CONSTRAINT chk_ri_match_type CHECK (match_type IS NULL OR match_type IN ('EXACT', 'FUZZY_DATE', 'KEYWORD', 'MANUAL'))
);

CREATE INDEX idx_ri_reconciliation ON reconciliation_items(id_reconciliation);
CREATE INDEX idx_ri_statement_item ON reconciliation_items(id_statement_item);
CREATE INDEX idx_ri_transaction ON reconciliation_items(id_transaction);
CREATE INDEX idx_ri_match_status ON reconciliation_items(match_status);
