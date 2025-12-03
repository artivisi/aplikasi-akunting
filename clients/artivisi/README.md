# PT Artivisi Intermedia

Client-specific configuration for PT Artivisi Intermedia - IT services company.

## Business Profile

| Attribute | Value |
|-----------|-------|
| Business Type | IT Services (B2B) |
| Services | Training, Consulting, Development, Remittance |
| Employees | <10 |
| Active Clients | ≤20 |
| Location | Jakarta, Indonesia |

## Files

| File | Description |
|------|-------------|
| `capacity-planning.md` | Infrastructure sizing and cost estimates |
| `seed-data/` | Expanded CSV seed data (v3.0) |

## Seed Data Contents (v3.0)

| Data Type | Count | Description |
|-----------|-------|-------------|
| Chart of Accounts | 91 | SAK EMKM compliant, includes fixed assets |
| Journal Templates | 64 | Income, expense, tax, payroll, fixed assets |
| Salary Components | 17 | BPJS, PPh 21, deductions |
| Asset Categories | 4 | Computer, Vehicle, Office Equipment, Machinery |

## Version History

### v3.0 (2024-12)
- Migrated to expanded CSV format for easier diff/version control
- Added asset categories with depreciation settings
- Added fixed asset templates (purchase, depreciation, disposal)
- Added salary components for payroll
- Added BBM (fuel) expense account and templates
- Added 6 new fixed asset accounts (Kendaraan, Peralatan Kantor, Mesin)

### v2.1 (2024-11)
- Added separate accounts for Deposito, Logam Mulia, Dinar/Dirham
- Added gain/loss accounts for asset sales
- Added PPh 23 credit account
- Added 14 new journal templates (investments, assets, bonus)

### v2.0 (2024-10)
- Added payroll-related accounts and templates
- Tax accounts (PPh 21, 23, 4(2), PPN)

### v1.0 (2024-09)
- Initial COA for IT services
- Basic income/expense templates

## Deployment

Domain: `akunting.artivisi.id`

See:
- `capacity-planning.md` for VPS sizing
- `docs/deployment-guide.md` for deployment steps

## Import Seed Data

The seed data is in expanded CSV format in `seed-data/` directory.

### Create ZIP for Import

```bash
cd clients/artivisi/seed-data
zip -r ../artivisi-seed-data.zip .
```

This creates `artivisi-seed-data.zip` containing:
- 91 chart of accounts
- 64 journal templates
- 17 salary components
- 4 asset categories

### Import via UI

1. Login as admin
2. Go to Settings > Import Data
3. Upload `artivisi-seed-data.zip`

### Import via curl

```bash
cd clients/artivisi/seed-data && zip -r ../artivisi-seed-data.zip .
curl -X POST http://localhost:10000/settings/import \
  -u admin:admin \
  -F "file=@clients/artivisi/artivisi-seed-data.zip"
```

### Seed Data Structure

```
seed-data/
├── MANIFEST.md                      # Export metadata
├── 01_company_config.csv            # Company settings (empty)
├── 02_chart_of_accounts.csv         # 91 accounts
├── 03_salary_components.csv         # 17 components
├── 04_journal_templates.csv         # 64 templates
├── 05_journal_template_lines.csv    # Template line items
├── 06_journal_template_tags.csv     # Template tags
├── 07_clients.csv - 33_*.csv        # Empty placeholders
├── 34_asset_categories.csv          # 4 categories
└── documents/
    └── index.csv                    # Empty
```
