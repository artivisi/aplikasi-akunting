# Template Packages

Kumpulan template Chart of Accounts (COA) dan Journal Templates untuk berbagai jenis industri.

## Structure

```
templates/
├── README.md              # Schema documentation (this file)
└── [industry]/            # Industry-specific templates
    ├── coa.json
    └── journal-templates.json

clients/
└── [client-name]/         # Client-specific templates
    └── templates/
        ├── coa.json
        └── journal-templates.json
```

## Available Templates

### Industry Templates (Generic)

| Industry | Version | Description |
|----------|---------|-------------|
| (none yet) | - | Coming soon: photography, online-seller |

### Client Templates

| Client | Location | Description |
|--------|----------|-------------|
| Artivisi | `clients/artivisi/templates/` | IT services (v2.1) |

## File Schemas

### coa.json

```json
{
  "name": "Package Name",
  "version": "1.0",
  "description": "Package description",
  "accounts": [
    {
      "code": "1",
      "name": "ASET",
      "type": "ASSET",
      "normalBalance": "DEBIT",
      "parentCode": null,
      "isHeader": true,
      "isPermanent": true,
      "description": "Optional description"
    }
  ]
}
```

#### Account Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `code` | Yes | String | Kode akun unik (e.g., "1.1.01") |
| `name` | Yes | String | Nama akun |
| `type` | Yes | Enum | `ASSET`, `LIABILITY`, `EQUITY`, `REVENUE`, `EXPENSE` |
| `normalBalance` | Yes | Enum | `DEBIT`, `CREDIT` |
| `parentCode` | No | String | Kode akun parent (untuk hierarki) |
| `isHeader` | No | Boolean | `true` jika akun ini adalah header/group (default: `false`) |
| `isPermanent` | No | Boolean | `true` untuk akun permanen, `false` untuk nominal (default: `true`) |
| `description` | No | String | Keterangan tambahan |

### journal-templates.json

```json
{
  "name": "Package Name",
  "version": "1.0",
  "description": "Package description",
  "templates": [
    {
      "name": "Template Name",
      "category": "EXPENSE",
      "cashFlowCategory": "OPERATING",
      "templateType": "SIMPLE",
      "description": "Template description",
      "tags": ["tag1", "tag2"],
      "lines": [
        {
          "accountCode": "5.1.01",
          "position": "DEBIT",
          "formula": "amount",
          "description": "Line description"
        }
      ]
    }
  ]
}
```

#### Template Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `name` | Yes | String | Nama template unik |
| `category` | Yes | Enum | `INCOME`, `EXPENSE`, `PAYMENT`, `RECEIPT`, `TRANSFER` |
| `cashFlowCategory` | Yes | Enum | `OPERATING`, `INVESTING`, `FINANCING` |
| `templateType` | No | Enum | `SIMPLE`, `DETAILED` (default: `SIMPLE`) |
| `description` | No | String | Keterangan template |
| `tags` | No | Array | Tags untuk pencarian (lowercase) |
| `lines` | Yes | Array | Minimal 2 baris (debit dan credit) |

#### Template Line Fields

| Field | Required | Type | Description |
|-------|----------|------|-------------|
| `accountCode` | Yes | String | Kode akun (harus ada di COA) |
| `position` | Yes | Enum | `DEBIT`, `CREDIT` |
| `formula` | Yes | String | Formula perhitungan (default: `amount`) |
| `description` | No | String | Keterangan baris |

## Formula Variables

Formula mendukung variabel dan operasi matematika dasar.

### Standard Variables

| Variable | Description |
|----------|-------------|
| `amount` | Nilai transaksi utama |
| `fee` | Biaya/fee (untuk template escrow) |

### Payroll Variables

Untuk template payroll (`Post Gaji Bulanan`), variabel berikut tersedia dari sistem:

| Variable | Description |
|----------|-------------|
| `grossSalary` | Total gaji bruto seluruh karyawan |
| `companyBpjs` | Total kontribusi BPJS perusahaan |
| `netPay` | Total gaji bersih (take-home pay) |
| `totalBpjs` | Total BPJS (perusahaan + karyawan) |
| `pph21` | Total PPh 21 yang dipotong |

### Formula Examples

```
amount                    # Nilai langsung
amount * 0.11             # 11% dari amount (PPN)
amount / 1.11             # DPP dari nilai termasuk PPN
amount - fee              # Amount dikurangi fee
companyBpjs * 0.8         # 80% dari BPJS perusahaan (Kesehatan)
companyBpjs * 0.2         # 20% dari BPJS perusahaan (Ketenagakerjaan)
```

## Creating New Package

1. Buat folder baru di `templates/[company-name]/`

2. Buat `coa.json` dengan struktur akun:
   - Mulai dari akun level 1 (header utama: ASET, LIABILITAS, EKUITAS, PENDAPATAN, BEBAN)
   - Gunakan kode akun yang konsisten (e.g., `1.1.01` untuk Kas)
   - Pastikan `parentCode` mereferensi akun yang sudah didefinisikan sebelumnya

3. Buat `journal-templates.json` dengan template:
   - Pastikan semua `accountCode` di lines mereferensi akun yang ada di `coa.json`
   - Setiap template harus memiliki minimal 1 baris DEBIT dan 1 baris CREDIT
   - Gunakan tags untuk memudahkan pencarian

4. Validasi JSON:
   ```bash
   python3 -m json.tool templates/[company-name]/coa.json > /dev/null
   python3 -m json.tool templates/[company-name]/journal-templates.json > /dev/null
   ```

5. Update tabel "Available Packages" di README ini

## Import via UI

1. Buka menu **Pengaturan > Import Data**
2. Pilih tab **Chart of Accounts** atau **Journal Templates**
3. Upload file JSON
4. Preview data yang akan diimport
5. Pilih opsi "Hapus data existing" jika ingin replace semua data
6. Klik **Import**

## Version Conventions

- Major version (1.0 → 2.0): Perubahan struktur atau breaking changes
- Minor version (2.0 → 2.1): Penambahan akun/template baru tanpa mengubah existing

## Account Code Conventions (Artivisi)

| Prefix | Type | Example |
|--------|------|---------|
| `1.x` | ASET | 1.1.01 Kas |
| `2.x` | LIABILITAS | 2.1.01 Hutang Usaha |
| `3.x` | EKUITAS | 3.1.01 Modal Disetor |
| `4.x` | PENDAPATAN | 4.1.01 Pendapatan Jasa |
| `5.x` | BEBAN | 5.1.01 Beban Gaji |

## Notes

- Client-specific changelogs are in their respective `clients/[client]/README.md` files
- Industry templates will have changelogs in this file when added
