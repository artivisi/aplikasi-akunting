# Invoice & Penagihan

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Perlu membuat invoice untuk menagih klien
- Ingin melacak status pembayaran invoice
- Menagih termin pembayaran berdasarkan milestone
- Perlu melihat daftar piutang yang belum dibayar

## Konsep yang Perlu Dipahami

### Status Invoice

| Status | Arti |
|--------|------|
| **Draft** | Invoice dibuat tapi belum dikirim ke klien |
| **Sent** | Invoice sudah dikirim, menunggu pembayaran |
| **Paid** | Invoice sudah dibayar penuh |
| **Partial** | Invoice dibayar sebagian |
| **Overdue** | Sudah melewati jatuh tempo |
| **Cancelled** | Invoice dibatalkan |

### Hubungan Invoice dengan Proyek

- Invoice dapat dibuat dari termin pembayaran proyek
- Invoice juga bisa dibuat standalone tanpa proyek
- Pembayaran invoice otomatis tercatat sebagai pendapatan proyek

## Skenario 1: Buat Invoice dari Termin Proyek

**Situasi**: Milestone kedua proyek sudah selesai, saatnya menagih termin 40%.

**Langkah-langkah**:

1. Klik menu **Proyek** di sidebar
2. Buka detail proyek
3. Scroll ke bagian **Termin Pembayaran**
4. Pada termin **Progress Payment** (40%), klik **Buat Invoice**
5. Form invoice akan terisi otomatis:
   - **Klien**: PT ABC (dari proyek)
   - **Jumlah**: Rp 20.000.000 (40% x 50.000.000)
   - **Proyek**: PRJ-2025-001
   - **Keterangan**: Progress Payment - Website E-commerce
6. Lengkapi informasi:
   - **Nomor Invoice**: `INV-2025-001` (atau auto-generate)
   - **Tanggal Invoice**: Tanggal hari ini
   - **Jatuh Tempo**: 14 hari dari tanggal invoice
   - **Item/Deskripsi**: Detail pekerjaan yang selesai
7. Klik **Simpan**

**Hasil**: Invoice dibuat dengan status Draft.

## Skenario 2: Buat Invoice Manual (Tanpa Proyek)

**Situasi**: Anda menagih klien untuk jasa konsultasi one-time Rp 5.000.000.

**Langkah-langkah**:

1. Klik menu **Invoice** di sidebar
2. Klik tombol **Invoice Baru**
3. Isi form:
   - **Nomor Invoice**: `INV-2025-002`
   - **Klien**: Pilih klien dari dropdown
   - **Tanggal Invoice**: Tanggal hari ini
   - **Jatuh Tempo**: 30 hari
4. Tambah item:
   - **Deskripsi**: Jasa Konsultasi IT - November 2025
   - **Kuantitas**: 1
   - **Harga Satuan**: 5.000.000
   - **PPN**: Centang jika dikenakan PPN
5. Klik **Simpan**

## Skenario 3: Kirim Invoice ke Klien

**Situasi**: Invoice sudah dibuat dan siap dikirim ke klien.

**Langkah-langkah**:

1. Buka detail invoice
2. Review kembali semua informasi
3. Klik tombol **Cetak PDF** untuk download
4. Kirim PDF ke klien via email atau media lain
5. Klik tombol **Tandai Terkirim**
6. Status berubah menjadi **Sent**

**Isi Invoice yang Tercetak**:
```
INVOICE
─────────────────────────────────────────

No. Invoice: INV-2025-001
Tanggal: 15 Januari 2026
Jatuh Tempo: 29 Januari 2026

Kepada:
PT ABC
Jl. Contoh No. 123
Jakarta

DESKRIPSI                          JUMLAH
─────────────────────────────────────────
Progress Payment 40%
Website E-commerce PT ABC      Rp 20.000.000

                               ─────────────
Subtotal                       Rp 20.000.000
PPN 11%                        Rp  2.200.000
                               ─────────────
TOTAL                          Rp 22.200.000
```

## Skenario 4: Catat Pembayaran Invoice

**Situasi**: Klien sudah membayar invoice INV-2025-001.

**Langkah-langkah**:

**Cara 1: Dari Halaman Invoice**

1. Buka detail invoice
2. Klik tombol **Catat Pembayaran**
3. Isi form:
   - **Tanggal Bayar**: Tanggal uang diterima
   - **Jumlah**: `22200000` (atau partial jika tidak penuh)
   - **Akun**: Bank BCA
   - **Keterangan**: Pembayaran INV-2025-001
4. Klik **Simpan**
5. Status invoice berubah menjadi **Paid**

**Cara 2: Dari Menu Transaksi**

1. Klik menu **Transaksi**
2. Buat transaksi baru dengan template **Terima Pembayaran Invoice**
3. Isi jumlah dan referensi nomor invoice
4. Posting transaksi
5. Kembali ke invoice dan tandai sebagai Paid

## Skenario 5: Handle Pembayaran Partial

**Situasi**: Klien membayar sebagian dari invoice (Rp 10.000.000 dari Rp 22.200.000).

**Langkah-langkah**:

1. Buka detail invoice
2. Klik **Catat Pembayaran**
3. Masukkan jumlah partial: `10000000`
4. Klik **Simpan**
5. Status berubah menjadi **Partial**
6. Sisa yang harus dibayar: Rp 12.200.000

**Tracking Pembayaran**:
```
RIWAYAT PEMBAYARAN

Tanggal      Jumlah         Metode
15/01/2026   Rp 10.000.000  Transfer Bank BCA

Total Invoice:     Rp 22.200.000
Sudah Dibayar:     Rp 10.000.000
Sisa:              Rp 12.200.000
```

## Skenario 6: Lihat Invoice Overdue

**Situasi**: Anda ingin mengetahui invoice mana yang sudah jatuh tempo.

**Langkah-langkah**:

1. Klik menu **Invoice** di sidebar
2. Di filter **Status**, pilih **Overdue**
3. Klik **Tampilkan**
4. Daftar invoice yang lewat jatuh tempo akan muncul
5. Untuk setiap invoice overdue:
   - Hubungi klien untuk follow up
   - Kirim reminder pembayaran

**Tips Follow Up**:
- Kirim reminder 3 hari sebelum jatuh tempo
- Kirim reminder kedua di hari jatuh tempo
- Hubungi via telepon jika sudah lebih dari 7 hari overdue

## Skenario 7: Batalkan Invoice

**Situasi**: Invoice perlu dibatalkan karena ada kesalahan atau proyek dibatalkan.

**Langkah-langkah**:

1. Buka detail invoice
2. Pastikan invoice belum ada pembayaran
3. Klik tombol **Batalkan**
4. Pilih alasan:
   - Kesalahan data
   - Proyek dibatalkan
   - Klien membatalkan
   - Lainnya
5. Masukkan catatan
6. Klik **Konfirmasi**

**Catatan**: Invoice yang sudah ada pembayaran tidak bisa dibatalkan langsung.

## Skenario 8: Lihat Ringkasan Piutang

**Situasi**: Anda ingin melihat total piutang yang belum dibayar.

**Langkah-langkah**:

1. Buka **Dashboard**
2. Lihat kartu **Piutang** untuk total outstanding
3. Untuk detail:
   - Klik menu **Invoice**
   - Filter status: **Sent** + **Partial** + **Overdue**
   - Total yang muncul adalah piutang

**Atau melalui Laporan**:
1. Klik menu **Laporan** > **Buku Besar**
2. Pilih akun **Piutang Usaha**
3. Lihat saldo akhir

## Tips

1. **Nomor berurutan** - Gunakan penomoran yang konsisten dan berurutan
2. **Jatuh tempo wajar** - Standar 14-30 hari tergantung kesepakatan
3. **Detail jelas** - Cantumkan detail pekerjaan yang bisa dipahami klien
4. **Follow up proaktif** - Jangan tunggu overdue untuk follow up

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Tidak bisa batalkan invoice | Cek apakah ada pembayaran, void pembayaran dulu |
| Jumlah termin salah | Edit nilai kontrak atau termin di proyek |
| PPN tidak muncul | Pastikan centang opsi PPN saat buat invoice |

## Lihat Juga

- [Setup Proyek](40-setup-proyek.md) - Konfigurasi termin pembayaran
- [Tracking Proyek](41-tracking-proyek.md) - Update milestone untuk trigger termin
- [Mencatat Pendapatan](10-mencatat-pendapatan.md) - Catat pembayaran manual
- [Kelola Klien](52-kelola-klien.md) - Data klien untuk invoice
