# Online Seller Seed Data Package

## Industry
Online Seller / E-commerce (Tokopedia, Shopee, Lazada, Bukalapak, TikTok Shop)

## Contents

| File | Records | Description |
|------|---------|-------------|
| 02_chart_of_accounts.csv | 87 | COA with marketplace saldo, inventory, fee accounts |
| 03_salary_components.csv | 17 | Indonesian payroll (BPJS, PPh 21) |
| 04_journal_templates.csv | 37 | Marketplace sales, withdraw, inventory templates |
| 05_journal_template_lines.csv | 81 | Template line items with formulas |
| 12_tax_deadlines.csv | 8 | Indonesian tax calendar |
| 34_asset_categories.csv | 3 | Gudang, Komputer, Kendaraan |

## Key Features

### Marketplace Support
- Saldo accounts for 5 major marketplaces (Tokopedia, Shopee, Lazada, Bukalapak, TikTok Shop)
- Sales templates with automatic admin fee deduction
- Withdraw templates for each marketplace
- Marketplace-specific fee accounts

### Inventory
- Single inventory account (Persediaan Barang Dagangan)
- Stock adjustment templates (masuk/keluar)
- HPP tracking

### Tax Compliance
- PPh Final UMKM 0.5% (PP 55/2022)
- PPN support for PKP sellers
- Standard Indonesian tax deadlines

## Usage

1. Download and extract this package
2. ZIP the seed-data folder
3. Go to Settings > Import Data
4. Upload the ZIP file
5. Confirm the import

## Notes

- This package does NOT include automated marketplace reconciliation
- Transaction data must be entered manually or via template execution
- For high-volume sellers, consider waiting for Phase 8 (Marketplace Integration)
