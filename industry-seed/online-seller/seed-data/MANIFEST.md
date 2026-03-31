# Online Seller Seed Data Package

## Industry
Online Seller / E-commerce (Tokopedia, Shopee, Lazada, TikTok Shop)

## Contents

| File | Records | Description |
|------|---------|-------------|
| 02_chart_of_accounts.csv | 84 | COA with marketplace saldo, inventory, fee accounts |
| 03_salary_components.csv | 17 | Indonesian payroll (BPJS, PPh 21) |
| 04_journal_templates.csv | 39 | Marketplace sales, withdraw, payroll, asset, inventory templates |
| 05_journal_template_lines.csv | 89 | Template line items with formulas |
| 06_journal_template_tags.csv | 0 | Template tags (header only) |
| 12_tax_deadlines.csv | 8 | Indonesian tax calendar |
| 34_asset_categories.csv | 3 | Gudang, Komputer, Kendaraan |
| 35_product_categories.csv | 5 | Phone, Accessory, Cable, Case, Audio |
| 36_products.csv | 18 | Smartphones, accessories, cables, cases, audio |

## Key Features

### Marketplace Support
- Saldo accounts for 4 major marketplaces (Tokopedia, Shopee, Lazada, TikTok Shop)
- Sales templates with automatic admin fee deduction
- Withdraw templates for each marketplace
- Marketplace-specific fee accounts

### Payroll
- Post Gaji Bulanan system template for automated payroll posting
- Pembelian Aset Tetap template for fixed asset purchases

### Inventory
- Single inventory account (Persediaan Barang Dagangan)
- 18 products across 5 categories (Smartphone, Aksesoris, Kabel, Case, Audio)
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
