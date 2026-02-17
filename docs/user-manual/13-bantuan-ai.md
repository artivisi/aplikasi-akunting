# Bantuan AI untuk Pencatatan Transaksi

Aplikasi ini mendukung pencatatan transaksi dengan bantuan AI assistant seperti Claude Code, Gemini CLI, atau asisten AI lainnya. AI assistant dapat membaca struk, invoice, atau dokumen keuangan lainnya, kemudian secara otomatis membuat transaksi akuntansi yang sesuai.

> **Fitur Utama:**
> - Autentikasi aman via OAuth 2.0 Device Flow
> - AI membaca dan menganalisis dokumen (struk, invoice, bank statement)
> - Template matching otomatis berdasarkan metadata
> - User approval sebelum transaksi diposting
> - Transaksi langsung tercatat (tanpa draft)
> - Analisis keuangan: AI membaca laporan keuangan dan memberikan insight

---

## Cara Kerja

### Alur Umum

```
1. User mengirim struk/dokumen ke AI assistant
   ↓
2. AI menganalisis dokumen (merchant, jumlah, tanggal)
   ↓
3. AI mencocokkan template journal yang sesuai
   ↓
4. AI menampilkan preview dan meminta persetujuan user
   ↓
5. User menyetujui → AI posting transaksi ke aplikasi
   ↓
6. Transaksi tercatat di aplikasi
```

### Keuntungan

- **Cepat**: Tidak perlu input manual di web
- **Akurat**: AI membaca struk dengan akurasi tinggi
- **Konsisten**: Template journal dipilih otomatis
- **Audit Trail**: Semua API call tercatat

---

## Setup Autentikasi

AI assistant memerlukan autentikasi untuk mengakses aplikasi Anda. Proses ini menggunakan **OAuth 2.0 Device Flow** yang aman.

### Langkah 1: AI Meminta Kode

AI assistant akan meminta device code dari aplikasi:

```bash
POST /api/device/code
{
  "clientId": "claude-code"
}
```

Response:
```json
{
  "deviceCode": "a79d766e4972e61d...",
  "userCode": "MBJN-KRFJ",
  "verificationUri": "http://localhost:8080/device",
  "interval": 5,
  "expiresIn": 900
}
```

### Langkah 2: User Otorisasi di Browser

AI akan menampilkan URL dan kode:

```
Please authorize this device:
  URL: http://localhost:8080/device
  Code: MBJN-KRFJ
```

Buka URL tersebut di browser, login, dan masukkan kode:

![Halaman Otorisasi Device](screenshots/ai-transaction/00-device-authorization.png)

**Langkah otorisasi:**
1. Login dengan username dan password Anda
2. Verifikasi kode perangkat yang ditampilkan
3. (Opsional) Beri nama perangkat, misalnya: "Claude Code di MacBook"
4. Klik **"Otorisasi Perangkat"**

> **Keamanan**: Kode device hanya berlaku 15 menit dan hanya bisa digunakan sekali.

### Langkah 3: AI Mendapat Access Token

Setelah Anda otorisasi, AI akan mendapat access token:

```json
{
  "accessToken": "15d07ef9030cba7b...",
  "tokenType": "Bearer",
  "expiresIn": 2592000
}
```

Token ini berlaku 30 hari. AI akan menyimpannya untuk digunakan di request berikutnya.

---

## Template Journal dengan Metadata

AI memilih template journal berdasarkan **metadata semantik** yang terdapat pada setiap template.

### Contoh Response Template API

```bash
GET /api/templates
Authorization: Bearer {accessToken}
```

Response (contoh 2 template dari 38 total):

```json
[
  {
    "id": "1bbc7ccc-4e8f-44ef-87d3-0cd011fbc56d",
    "name": "Bayar Beban Listrik",
    "category": "EXPENSE",
    "description": "Pembayaran listrik kantor",
    "semanticDescription": "Gunakan template ini untuk mencatat pembayaran tagihan listrik bulanan ke PLN atau penyedia listrik lainnya. Termasuk token listrik prabayar.",
    "keywords": [
      "listrik",
      "electricity",
      "pln",
      "token",
      "utility",
      "utilitas"
    ],
    "exampleMerchants": [
      "PLN",
      "PLN Mobile",
      "Tokopedia PLN",
      "Bukalapak Token Listrik"
    ],
    "typicalAmountMin": 50000,
    "typicalAmountMax": 5000000,
    "merchantPatterns": [
      ".*pln.*",
      ".*listrik.*",
      ".*electricity.*"
    ]
  },
  {
    "id": "cfb2a55c-4626-4ec6-a719-a243cee8dbf9",
    "name": "Pendapatan Jasa Konsultasi",
    "category": "INCOME",
    "description": "Mencatat pendapatan dari jasa konsultasi/development",
    "semanticDescription": "Template untuk mencatat pendapatan dari layanan konsultasi IT, software development, system integration, atau jasa profesional lainnya (untuk non-PKP atau tanpa PPN).",
    "keywords": [
      "pendapatan",
      "income",
      "revenue",
      "konsultasi",
      "consulting",
      "development",
      "jasa",
      "service"
    ],
    "exampleMerchants": [
      "Client A",
      "PT Client B",
      "Government Agency",
      "Startup C"
    ],
    "typicalAmountMin": 5000000,
    "typicalAmountMax": 500000000,
    "merchantPatterns": []
  }
]
```

> **Catatan**: Data di atas adalah contoh template yang sudah dilengkapi metadata. Template dari seed pack IT Service saat ini belum memiliki metadata lengkap (akan dilengkapi di versi berikutnya).

### Cara AI Matching Template

AI menggunakan metadata untuk mencocokkan template:

1. **Keyword matching**: Cocokkan kategori/deskripsi dengan array `keywords`
2. **Merchant matching**: Cocokkan nama merchant dengan `exampleMerchants`
3. **Regex pattern**: Cocokkan dengan `merchantPatterns`
4. **Amount range**: Validasi jumlah dalam rentang `typicalAmountMin` - `typicalAmountMax`
5. **Semantic similarity**: Cocokkan deskripsi dengan `semanticDescription`

**Contoh matching:**
- User: "Bayar listrik PLN 350 ribu"
- AI matching:
  - Keyword: "listrik" ✓
  - Merchant: "PLN" ✓
  - Amount: 350000 (dalam range 50k-5jt) ✓
  - **Result**: Template "Bayar Beban Listrik"

---

## Posting Transaksi Langsung

Setelah AI matching template dan user approve, AI langsung posting transaksi (tanpa draft).

### Request

```bash
POST /api/transactions
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Body:**
```json
{
  "templateId": "9aa9c785-bb32-4e1e-80db-1c30a10aa3e3",
  "merchant": "Starbucks Grand Indonesia",
  "amount": 85000,
  "transactionDate": "2026-02-12",
  "currency": "IDR",
  "description": "Team coffee break at Starbucks",
  "category": "Food & Beverage",
  "items": ["Caffe Latte Grande", "Blueberry Muffin"],
  "source": "claude-code",
  "userApproved": true
}
```

### Response

```json
{
  "transactionId": "83da31e4-acdc-4ca9-898e-df2cefbe95c7",
  "transactionNumber": "TRX-2026-0001",
  "status": "POSTED",
  "merchant": "Team coffee break at Starbucks",
  "amount": 85000,
  "transactionDate": "2026-02-12",
  "description": "Team coffee break at Starbucks",
  "journalEntries": [
    {
      "journalNumber": "JE-2026-0001-01",
      "accountCode": "5.1.01",
      "accountName": "Beban Gaji",
      "debitAmount": 85000,
      "creditAmount": 0
    },
    {
      "journalNumber": "JE-2026-0001-02",
      "accountCode": "2.1.10",
      "accountName": "Hutang Gaji",
      "debitAmount": 0,
      "creditAmount": 85000
    }
  ]
}
```

### Verifikasi di Web UI

Setelah transaksi diposting, Anda dapat melihatnya di halaman Transaksi:

![Daftar Transaksi](screenshots/ai-transaction/04-transactions-list.png)

Transaksi yang dibuat via AI akan memiliki:
- **Transaction Number**: TRX-2026-0001 (auto-generated)
- **Status**: POSTED (langsung posted, bukan draft)
- **Created By**: Nama user yang otorisasi device

---

## Analisis Keuangan via AI

Selain mencatat transaksi, AI assistant juga dapat **menganalisis data keuangan** perusahaan Anda. Aplikasi menyediakan 10 endpoint read-only di bawah `/api/analysis` yang mengembalikan data terstruktur — AI yang menginterpretasikan datanya.

### Endpoint Analisis

| Endpoint | Deskripsi | Parameter |
|----------|-----------|-----------|
| `GET /api/analysis/snapshot` | KPI bulanan (revenue, expense, profit, cash) | `month` (yyyy-MM) |
| `GET /api/analysis/trial-balance` | Neraca saldo | `asOfDate` (yyyy-MM-dd) |
| `GET /api/analysis/income-statement` | Laporan laba rugi | `startDate`, `endDate` |
| `GET /api/analysis/balance-sheet` | Neraca | `asOfDate` |
| `GET /api/analysis/cash-flow` | Laporan arus kas | `startDate`, `endDate` |
| `GET /api/analysis/tax-summary` | Ringkasan pajak (PPN, PPh) | `startDate`, `endDate` |
| `GET /api/analysis/receivables` | Piutang usaha | `asOfDate` |
| `GET /api/analysis/payables` | Hutang usaha | `asOfDate` |
| `GET /api/analysis/accounts` | Daftar akun (COA) | — |
| `GET /api/analysis/drafts` | Draft transaksi pending | — |

Semua parameter **wajib** (tidak ada nilai default). Request tanpa parameter yang diperlukan akan mengembalikan HTTP 400.

### Format Response

Semua endpoint mengembalikan format `AnalysisResponse` yang konsisten:

```json
{
  "reportType": "trial-balance",
  "generatedAt": "2026-02-17T14:30:00",
  "parameters": {
    "asOfDate": "2026-01-31"
  },
  "data": { ... },
  "metadata": {
    "currency": "IDR",
    "accountingBasis": "accrual",
    "description": "Trial balance as of 2026-01-31..."
  }
}
```

Field `metadata` memberikan konteks yang membantu AI memahami data (mata uang, basis akuntansi, penjelasan arti debit/credit, dll).

### Contoh: KPI Snapshot

```bash
GET /api/analysis/snapshot?month=2026-01
Authorization: Bearer {accessToken}
```

Response:
```json
{
  "reportType": "snapshot",
  "generatedAt": "2026-02-17T14:30:00",
  "parameters": { "month": "2026-01" },
  "data": {
    "month": "2026-01",
    "revenue": 50000000,
    "revenueChange": 12.5,
    "expense": 35000000,
    "expenseChange": -3.2,
    "netProfit": 15000000,
    "profitChange": 8.7,
    "profitMargin": 30.0,
    "marginChange": 2.1,
    "cashBalance": 125000000,
    "receivablesBalance": 20000000,
    "payablesBalance": 15000000,
    "transactionCount": 47,
    "cashBankItems": [
      { "accountName": "Kas", "balance": 25000000 },
      { "accountName": "Bank BCA", "balance": 100000000 }
    ]
  },
  "metadata": {
    "currency": "IDR",
    "accountingBasis": "accrual",
    "description": "Financial KPI snapshot for 2026-01. Change percentages are vs previous month."
  }
}
```

### Contoh: Neraca Saldo

```bash
GET /api/analysis/trial-balance?asOfDate=2026-01-31
Authorization: Bearer {accessToken}
```

Response (ringkasan):
```json
{
  "reportType": "trial-balance",
  "data": {
    "items": [
      {
        "accountCode": "1.1.01",
        "accountName": "Kas",
        "accountType": "ASSET",
        "normalBalance": "DEBIT",
        "debitBalance": 25000000,
        "creditBalance": 0
      },
      {
        "accountCode": "4.1.01",
        "accountName": "Pendapatan Jasa",
        "accountType": "REVENUE",
        "normalBalance": "CREDIT",
        "debitBalance": 0,
        "creditBalance": 50000000
      }
    ],
    "totalDebit": 85000000,
    "totalCredit": 85000000
  }
}
```

### Contoh: Draft Transaksi Pending

```bash
GET /api/analysis/drafts
Authorization: Bearer {accessToken}
```

Response:
```json
{
  "reportType": "drafts",
  "data": {
    "items": [
      {
        "id": "a1b2c3d4-...",
        "status": "PENDING",
        "merchantName": "Starbucks Grand Indonesia",
        "amount": 85000,
        "currency": "IDR",
        "transactionDate": "2026-02-12",
        "source": "API",
        "apiSource": "claude-code",
        "overallConfidence": 0.95,
        "suggestedTemplateName": "Konsumsi Karyawan",
        "createdBy": "admin",
        "createdAt": "2026-02-12T14:30:00"
      }
    ],
    "pendingCount": 1
  }
}
```

AI dapat menggunakan data ini untuk merekomendasikan approval draft dengan confidence tinggi, atau menandai draft yang perlu review manual.

### Contoh Interaksi Analisis

**User:**
```
Bagaimana kondisi keuangan bulan Januari?
```

**AI:**
```
Berdasarkan data keuangan Januari 2026:

Revenue:     Rp 50.000.000 (+12.5% vs Desember)
Expense:     Rp 35.000.000 (-3.2% vs Desember)
Net Profit:  Rp 15.000.000 (+8.7%)
Margin:      30.0%

Kas & Bank:
  - Kas:      Rp 25.000.000
  - Bank BCA: Rp 100.000.000

Piutang:     Rp 20.000.000
Hutang:      Rp 15.000.000

Bulan ini cukup baik — revenue naik, expense turun, dan
profit margin meningkat 2.1 poin.
```

**User:**
```
Ada pajak yang perlu dibayar?
```

**AI:**
```
Berdasarkan ringkasan pajak Januari 2026:

PPN Keluaran:  Rp 5.000.000
PPN Masukan:   Rp 2.000.000
PPN Kurang Bayar: Rp 3.000.000

PPh 23:        Rp 1.500.000 (sudah dipotong)
PPh 21:        Rp 2.800.000 (gaji karyawan)

Total kewajiban pajak bulan ini: Rp 7.300.000
Batas setor PPN: 31 Februari 2026
```

### Otorisasi Scope

Token yang diterbitkan melalui Device Flow secara otomatis memiliki scope `analysis:read`. Scope ini dibutuhkan untuk mengakses semua endpoint `/api/analysis/*`. Tanpa scope ini, request akan ditolak dengan HTTP 403.

Scope yang diberikan pada token:
- `drafts:create` — membuat draft transaksi
- `drafts:approve` — approve/reject draft
- `drafts:read` — membaca draft
- `analysis:read` — membaca laporan keuangan

---

## Contoh Interaksi dengan AI

### Skenario 1: Struk Kopi Starbucks

**User:**
```
[Kirim foto struk Starbucks]
```

**AI:**
```
📄 Saya menganalisis struk:
   • Merchant: Starbucks Grand Indonesia
   • Jumlah: Rp 85.000
   • Tanggal: 12 Feb 2026
   • Item: Caffe Latte Grande, Blueberry Muffin

   Template yang cocok: "Konsumsi Karyawan" (confidence: 0.95)

   Posting transaksi ini?
   ✓ Ya
   ✏️ Edit dulu
   🔄 Pilih template lain
   ✗ Batal
```

**User:**
```
Ya
```

**AI:**
```
✓ Transaksi berhasil diposting!
  Nomor: TRX-2026-0001
  Status: POSTED

  Jurnal:
  [DEBIT]  Beban Konsumsi Karyawan: Rp 85.000
  [CREDIT] Kas: Rp 85.000
```

### Skenario 2: Text Input Listrik

**User:**
```
Bayar listrik PLN 350 ribu
```

**AI:**
```
📄 Saya memahami:
   • Merchant: PLN
   • Jumlah: Rp 350.000
   • Kategori: Utilities

   Template yang cocok: "Bayar Beban Listrik"

   Posting?
```

**User:**
```
Ya, tapi edit deskripsi dulu
```

**AI:**
```
Deskripsi apa yang Anda inginkan?
```

**User:**
```
Listrik bulan Januari 2026
```

**AI:**
```
✓ Siap posting:
  • Merchant: PLN
  • Jumlah: Rp 350.000
  • Deskripsi: "Listrik bulan Januari 2026"
  • Template: Bayar Beban Listrik

  Posting sekarang? (ya/tidak)
```

**User:**
```
ya
```

**AI:**
```
✓ Posted TRX-2026-0002
```

---

## Monitoring & Audit

### Audit Log

Semua API call tercatat di audit log dengan detail:

- **Event Type**: `API_CALL`
- **User**: Username yang otorisasi device
- **IP Address**: IP address request
- **Details**: Source (claude-code), merchant, amount, confidence

### Token Management

User dapat melihat dan mencabut device token di halaman Settings:

**Lokasi:** Dashboard → Settings → Device Tokens

**Informasi yang ditampilkan:**
- Device Name: "Claude Code di MacBook"
- Client ID: claude-code
- Created: 12 Feb 2026 14:56
- Last Used: 12 Feb 2026 15:01
- Expires: 13 Mar 2026
- Status: Active

**Aksi:**
- **Revoke**: Mencabut token (AI tidak bisa akses lagi)

---

## API Reference

### Endpoint Summary

**Autentikasi:**

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| POST | `/api/device/code` | Request device code (public) |
| POST | `/api/device/token` | Poll for access token (public) |

**Transaksi (scope: `drafts:*`):**

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/templates` | List templates dengan metadata |
| GET | `/api/templates/{id}` | Get single template |
| POST | `/api/transactions` | Post transaction langsung |
| POST | `/api/drafts/from-receipt` | Buat draft dari struk |
| GET | `/api/drafts/{id}` | Get draft by ID |
| POST | `/api/drafts/{id}/approve` | Approve draft |
| POST | `/api/drafts/{id}/reject` | Reject draft |

**Analisis Keuangan (scope: `analysis:read`):**

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/analysis/snapshot` | KPI bulanan |
| GET | `/api/analysis/trial-balance` | Neraca saldo |
| GET | `/api/analysis/income-statement` | Laporan laba rugi |
| GET | `/api/analysis/balance-sheet` | Neraca |
| GET | `/api/analysis/cash-flow` | Laporan arus kas |
| GET | `/api/analysis/tax-summary` | Ringkasan pajak |
| GET | `/api/analysis/receivables` | Piutang usaha |
| GET | `/api/analysis/payables` | Hutang usaha |
| GET | `/api/analysis/accounts` | Daftar akun (COA) |
| GET | `/api/analysis/drafts` | Draft transaksi pending |

### Authentication

Semua endpoint kecuali `/api/device/**` memerlukan Bearer token:

```
Authorization: Bearer {accessToken}
```

### Rate Limiting

- **Draft creation**: 10 requests/menit per IP
- **Transaction posting**: 30 requests/menit per IP

### Error Codes

| Code | Error | Deskripsi |
|------|-------|-----------|
| 400 | `MISSING_PARAMETER` | Parameter wajib tidak dikirim (contoh: `asOfDate`) |
| 400 | `INVALID_REQUEST` | Request tidak valid (field required kosong, dll) |
| 401 | `unauthorized` | Token tidak valid atau expired |
| 403 | — | Scope tidak memadai (contoh: token tanpa `analysis:read`) |
| 404 | `NOT_FOUND` | Template/resource tidak ditemukan |
| 429 | `RATE_LIMIT_EXCEEDED` | Terlalu banyak request |
| 500 | `INTERNAL_ERROR` | Server error |

---

## FAQ

### Apakah data saya aman?

Ya. Autentikasi menggunakan OAuth 2.0 Device Flow yang merupakan standard industri. Access token:
- Tersimpan hanya di AI assistant Anda (tidak di server pihak ketiga)
- Berlaku maksimal 30 hari
- Dapat dicabut kapan saja di Settings

### AI mana yang didukung?

API ini generik dan dapat digunakan oleh AI assistant apapun yang mendukung HTTP API:
- ✅ Claude Code
- ✅ Gemini CLI
- ✅ ChatGPT dengan plugin
- ✅ Custom script (curl, Python, dll)

### Bagaimana jika AI salah pilih template?

Anda bisa:
1. **Edit di chat**: Minta AI ganti template sebelum posting
2. **Edit di web**: Setelah posted, edit transaksi di web UI
3. **Void dan re-create**: Void transaksi dan buat ulang

### Apakah bisa batch import banyak transaksi?

Saat ini belum. Fitur batch import via CSV/Excel akan ditambahkan di versi berikutnya.

### Bagaimana cara menambah metadata ke template?

Administrator dapat menambah metadata via:
1. **API**: PUT /api/templates/{id}
2. **Web UI**: (akan ditambahkan di versi berikutnya)

---

## Troubleshooting

### Error: "Authentication required" (401)

**Penyebab**: Token tidak valid atau expired

**Solusi**:
1. Lakukan device flow authentication ulang
2. Pastikan token disimpan dengan benar di AI assistant
3. Cek apakah token sudah expired (30 hari)

### Error: "Template not found" (404)

**Penyebab**: Template ID tidak valid

**Solusi**:
1. Refresh template list: GET /api/templates
2. Pastikan template ID yang dipilih ada di list
3. Jangan hardcode template ID, selalu fetch dari API

### AI matching template yang salah

**Penyebab**: Metadata template belum lengkap atau keyword tidak match

**Solusi**:
1. Lengkapi metadata template via API atau admin UI
2. Tambahkan keyword yang lebih spesifik
3. Tambahkan merchant patterns (regex)
4. User bisa override template saat approve

### Transaction failed to post

**Penyebab**:
- Template tidak balanced (debit ≠ credit)
- Account tidak valid
- Tanggal transaksi invalid

**Solusi**:
1. Cek template lines (harus ada debit dan credit)
2. Cek account mapping
3. Pastikan tanggal tidak di masa depan

---

## Lihat Juga

- [Setup Awal](01-setup-awal.md) - Setup aplikasi pertama kali
- [Tutorial Akuntansi](02-tutorial-akuntansi.md) - Dasar-dasar akuntansi
- [Rekonsiliasi Bank](14-rekonsiliasi-bank.md) - Bank reconciliation (data tersedia via `/api/analysis`)
- [API Documentation](../ai-transaction-api-v2.md) - Technical API reference

---

## Pengembangan Selanjutnya

Fitur yang akan ditambahkan:

- [ ] Batch transaction import (CSV/Excel)
- [ ] Template metadata management UI
- [ ] Webhook notifications (transaksi berhasil/gagal)
- [ ] AI confidence feedback loop
- [ ] Multi-currency support dengan auto-conversion
- [ ] PDF invoice parsing
- [ ] Template suggestion improvement (machine learning)

---

> **Info**: Dokumentasi ini ditulis berdasarkan test scenario yang sebenarnya. Semua screenshot dan JSON response diambil dari functional test yang berjalan pada aplikasi versi 2026.01-SNAPSHOT.
