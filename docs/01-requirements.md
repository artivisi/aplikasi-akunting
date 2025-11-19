# Requirements & Feature Specification

## Target Market

### Primary Users
- Small businesses in Indonesia
- Sole proprietors
- Freelancers

### User Characteristics
- Minimal accounting/finance/tax knowledge
- May employ junior accounting staff (fresh graduates)
- One bookkeeper may serve multiple clients
- 100% Indonesian users

### Key Constraints
- Limited budget for accounting software
- Need simple, guided workflows
- Require extensive reporting and analysis
- Must handle Indonesian tax compliance seamlessly

## Core Features

### 1. Simplified Transaction Entry
- Template-based transactions for common business scenarios
- Smart categorization with suggestions
- Receipt/invoice photo upload with OCR
- Bulk import from Excel/CSV
- Recurring transaction automation
- Business language interface (not accounting jargon)

### 2. Indonesian Tax Compliance

#### PPh (Pajak Penghasilan)
- PPh 21 calculation (employee withholding)
- PPh 23 tracking (services received)
- PPh 4(2) final tax
- PPh Pasal 25 (monthly installments)
- Form 1770/1770S generation for sole proprietors

#### PPN (VAT)
- PPN input/output tracking
- SPT Masa PPN (monthly VAT return)
- e-Faktur integration preparation
- PKP vs non-PKP handling

#### Other Tax Features
- NPWP management
- Tax calendar with reminders (monthly, quarterly, annual deadlines)
- Automated tax calculations from transactions

### 3. Multi-Client Dashboard
- Easy switching between client companies
- Consolidated view across multiple clients
- Task management per client
- Permission levels: Owner, Operator, Viewer

### 4. Reports & Analysis

#### Financial Reports (Indonesian Format)
- Laporan Laba Rugi (Income Statement)
- Neraca (Balance Sheet)
- Arus Kas (Cash Flow Statement)
- Buku Besar (General Ledger)
- All reports in Indonesian terminology

#### Analysis Tools
- Revenue trends
- Expense breakdown by category
- Profit margin analysis
- Budget vs actual comparison
- Tax burden analysis

#### Export Capabilities
- PDF, Excel, CSV formats
- Tax consultant review-ready exports

### 5. Guided Workflows
- Onboarding wizard (company setup)
- Chart of accounts templates by industry
- Monthly closing checklist
- Tax filing reminders with step-by-step guides
- Data validation warnings (non-blocking)
- Undo/edit capabilities

### 6. Indonesian-Specific Features
- Rupiah currency with proper formatting
- Indonesian fiscal year (January-December)
- Faktur Pajak (tax invoice) numbering
- Chart of accounts templates for common business types:
  - Warung (small shop)
  - Toko (retail store)
  - Jasa (services)
  - And others
- Bahasa Indonesia throughout the interface

## Non-Features (Not Planned Near-Term)
- Bank integration / automatic bank feeds
- Payroll processing (may record as expenses only)
- Inventory management (Phase 3 consideration)
- Mobile app (Phase 3 consideration)
- Multi-currency support

## Competitive Differentiators

1. **Tax Automation** - Automatic calculation of all Indonesian taxes from transactions
2. **Junior-Friendly** - Guided workflows, validation, intelligent suggestions
3. **Multi-Client Efficiency** - One bookkeeper can efficiently serve many clients
4. **Analysis Focus** - Not just compliance, but actionable business insights
5. **Indonesian-Native** - Built specifically for Indonesian regulations, not translated

## Success Criteria

### For End Users (Business Owners)
- Can complete monthly bookkeeping without accounting knowledge
- Tax reports generated automatically and accurately
- Clear visibility into business financial health
- Confidence in compliance with Indonesian tax regulations

### For Operators (Bookkeepers)
- Can manage 5-10 clients efficiently
- Fast data entry with templates
- Clear task lists and reminders
- Easy switching between client contexts

### For Business (SaaS)
- Scalable multi-tenant architecture
- Low support burden (self-service capable)
- High user retention
- Clear upgrade path from personal use to team/multi-client
