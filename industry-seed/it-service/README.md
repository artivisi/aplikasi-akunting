# IT Services Industry Seed Data

Seed data package for IT Services companies (PKP status) in Indonesia.

## Target Business Profile

| Attribute | Value |
|-----------|-------|
| Business Type | IT Services (B2B) |
| Typical Services | Training, Consulting, Software Development |
| Tax Status | PKP (Pengusaha Kena Pajak) |
| Employees | 1-50 |
| Accounting Standard | SAK EMKM |

## Package Contents

| Data Type | Count | Description |
|-----------|-------|-------------|
| Chart of Accounts | ~50 | SAK EMKM compliant, simplified for services |
| Journal Templates | ~40 | Income, expense, tax (PPN/PPh), payroll |
| Salary Components | 17 | BPJS, PPh 21, standard deductions |
| Asset Categories | 4 | Computer, Vehicle, Office Equipment, Machinery |
| Tax Deadlines | 8 | Indonesian tax calendar |

## Chart of Accounts Structure

```
1. ASET
   1.1 Aset Lancar
       - Kas, Bank BCA, Bank Mandiri
       - Piutang Usaha, Piutang Karyawan
       - PPN Masukan, Kredit PPh 23
   1.2 Aset Tetap
       - Peralatan Komputer, Kendaraan, Peralatan Kantor
       - Akumulasi Penyusutan
   1.3 Aset Tak Berwujud
       - Website & Software, Akumulasi Amortisasi

2. LIABILITAS
   2.1 Liabilitas Jangka Pendek
       - Hutang Usaha, Hutang Pajak
       - Hutang PPN, Hutang PPh 21/23/4(2)/25/29
       - Hutang Gaji, Hutang BPJS
       - Pendapatan Diterima Dimuka

3. EKUITAS
   3.1 Modal Disetor
   3.2 Laba Ditahan, Laba Berjalan

4. PENDAPATAN
   4.1 Pendapatan Usaha
       - Jasa Training, Jasa Konsultasi, Jasa Development
   4.2 Pendapatan Lain-lain

5. BEBAN
   5.1 Beban Operasional
       - Gaji, BPJS, Sewa, Cloud/Server, Software
   5.2 Beban Lain-lain
   5.9 Beban Pajak
```

## Key Templates

### Revenue (PKP)
- Pendapatan Jasa (tanpa PPN) - for non-PKP clients
- Pendapatan Jasa + PPN Keluaran - standard PKP invoice
- Pendapatan Jasa + PPN + PPh 23 Dipotong - when client withholds tax

### Expenses
- Bayar Beban Gaji
- Bayar Beban Cloud & Server
- Bayar Beban Software & Lisensi
- Pembelian dengan PPN Masukan

### Tax Payments
- Setor PPh 21, 23, 4(2), 25, 29
- Setor PPN (Keluaran - Masukan)

### Payroll
- Post Gaji Bulanan (system template)
- Bayar Hutang Gaji
- Bayar BPJS

## Usage

1. Create ZIP file:
   ```bash
   cd industry-seed/it-service/seed-data
   zip -r ../it-service-seed.zip .
   ```

2. Login as admin
3. Go to Settings > Import Data
4. Upload `it-service-seed.zip`

## Customization

After import, you may want to:

1. **Add bank accounts** - Edit COA to add your specific banks
2. **Adjust revenue types** - Add/modify based on your services
3. **Configure company info** - Settings > Company Configuration
4. **Add employees** - HR > Employees
5. **Set up clients** - Clients menu

## Version History

### v1.0 (2024-12)
- Initial release
- Simplified COA (~50 accounts vs ~100 in full version)
- Essential templates for PKP IT Services
- Standard Indonesian payroll components
