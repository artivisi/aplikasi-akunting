# Rekonsiliasi Bank

Panduan lengkap untuk fitur rekonsiliasi bank: import mutasi, pencocokan otomatis, dan pelaporan.

## Konsep Rekonsiliasi Bank

### Apa Itu Rekonsiliasi Bank

Rekonsiliasi bank adalah proses mencocokkan catatan transaksi di **buku besar (book)** dengan **mutasi rekening koran dari bank**. Tujuannya memastikan bahwa saldo kas di pembukuan sesuai dengan saldo aktual di bank.

### Mengapa Diperlukan

Saldo buku dan saldo bank sering berbeda karena:

| Penyebab | Contoh | Ada di |
|----------|--------|--------|
| Biaya administrasi bank | Biaya admin bulanan Rp 15.000 | Bank saja |
| Pendapatan bunga | Bunga deposito yang belum dicatat | Bank saja |
| Cek beredar (outstanding check) | Cek yang sudah dicatat tapi belum dicairkan | Buku saja |
| Transfer belum dicatat | Pembayaran masuk yang belum di-input | Bank saja |

Rekonsiliasi mengidentifikasi selisih ini sehingga pembukuan dapat dikoreksi.

### Alur Kerja

```
1. Import CSV     2. Buat Rekonsiliasi     3. Pencocokan     4. Laporan
   dari bank         (pilih statement)        (auto/manual)      (print/export)
```

---

## Konfigurasi Parser Bank

### Parser Bawaan

Aplikasi menyediakan parser bawaan untuk bank-bank utama Indonesia. Buka menu **Rekonsiliasi Bank** > **Konfigurasi Parser**.

![Daftar Konfigurasi Parser](screenshots/bank-recon/parser-configs.png)

Setiap parser mendefinisikan posisi kolom dalam file CSV: kolom tanggal, keterangan, debit, kredit, dan saldo.

**Parser bawaan:**

| Bank | Format Tanggal | Kolom |
|------|---------------|-------|
| BCA | dd/MM/yyyy | Tanggal, Keterangan, Cabang, Debit, Credit, Saldo |
| Mandiri | dd/MM/yyyy | Tanggal, Keterangan, Debit, Kredit, Saldo |
| BNI | dd-MMM-yyyy | Tanggal, Keterangan, Cabang, Debit, Kredit, Saldo |
| BSI | dd/MM/yyyy | Tanggal, Keterangan, Debit, Kredit, Saldo |
| CIMB | dd/MM/yyyy | Tanggal, Keterangan, Debit, Kredit, Saldo |

### Custom Parser

Jika bank tidak tersedia dalam daftar, buat parser kustom. Buka **Konfigurasi Parser** > **Parser Baru**.

![Form Custom Parser](screenshots/bank-recon/parser-config-form.png)

Field yang perlu diisi:
- **Tipe Bank:** CUSTOM
- **Nama Config:** Nama deskriptif (contoh: "Bank Jatim CSV")
- **Kolom Tanggal:** Index kolom tanggal (dimulai dari 0)
- **Kolom Keterangan:** Index kolom deskripsi transaksi
- **Format Tanggal:** Pattern Java DateTimeFormatter (contoh: `dd/MM/yyyy`)

---

## Import Mutasi Bank

### Persiapan File CSV

Download mutasi rekening dari internet banking dalam format CSV. Pastikan file memiliki header dan format sesuai dengan parser yang dipilih.

**Contoh CSV BCA (`bca-statement-202401.csv`):**

```csv
Tanggal,Keterangan,Cabang,Debit,Credit,Saldo
01/01/2024,"Setoran Modal Awal 2024",0316,,,500000000.00
15/01/2024,"Transfer Masuk - Konsultasi Core Banking",0316,,196200000.00,696200000.00
15/01/2024,"Transfer Keluar - JetBrains License",0316,3330000.00,,692870000.00
31/01/2024,"Transfer Keluar - AWS Cloud",0316,5550000.00,,687320000.00
31/01/2024,"Biaya Admin Bank BCA",0316,15000.00,,687305000.00
```

### Form Import

Buka menu **Rekonsiliasi Bank** > **Import Mutasi**.

![Form Import Mutasi Bank](screenshots/bank-recon/import-form.png)

Isi data import:
- **Rekening Bank:** Pilih rekening (contoh: BCA - 1234567890)
- **Parser Config:** Pilih parser sesuai bank
- **Periode Mulai:** Tanggal awal mutasi (01/01/2024)
- **Periode Akhir:** Tanggal akhir mutasi (31/01/2024)
- **File CSV:** Upload file yang sudah didownload

Klik **Import** untuk memproses.

### Daftar Mutasi

Setelah import, mutasi tampil di menu **Rekonsiliasi Bank** > **Daftar Mutasi**.

![Daftar Mutasi Bank](screenshots/bank-recon/statements-list.png)

### Detail Mutasi

Klik pada mutasi untuk melihat detail item yang berhasil di-parse.

![Detail Mutasi Bank](screenshots/bank-recon/statement-detail.png)

Informasi yang ditampilkan:
- **Bank:** BCA
- **Total Item:** 5 transaksi
- **Tabel Item:** Tanggal, keterangan, debit/kredit, saldo

---

## Proses Rekonsiliasi

### Membuat Rekonsiliasi Baru

Buka menu **Rekonsiliasi Bank** > **Rekonsiliasi Baru**.

![Form Rekonsiliasi Baru](screenshots/bank-recon/recon-form.png)

Pilih mutasi bank yang akan direkonsiliasi dari dropdown, lalu klik **Buat Rekonsiliasi**.

### Detail Rekonsiliasi

Setelah dibuat, halaman detail menampilkan ringkasan:

![Detail Rekonsiliasi](screenshots/bank-recon/recon-detail.png)

Informasi yang ditampilkan:
- **Status:** IN_PROGRESS / COMPLETED
- **Saldo Bank:** Saldo akhir menurut bank (Rp 687.305.000)
- **Saldo Buku:** Saldo akhir menurut pembukuan
- **Item Cocok:** Jumlah transaksi yang sudah dicocokkan
- **Selisih:** Perbedaan antara saldo bank dan buku

### Auto-Match (Pencocokan Otomatis)

Klik tombol **Auto-Match** untuk menjalankan pencocokan otomatis 3 tahap:

| Tahap | Metode | Kriteria |
|-------|--------|----------|
| 1. Exact Match | Tanggal + jumlah persis sama | Tanggal dan nominal harus identik |
| 2. Fuzzy Date | Jumlah sama, tanggal berdekatan | Nominal identik, selisih tanggal maks 3 hari |
| 3. Keyword Match | Kata kunci di keterangan | Mencocokkan kata kunci keterangan bank vs buku |

![Setelah Auto-Match](screenshots/bank-recon/recon-auto-match.png)

**Contoh hasil auto-match dengan data PT ArtiVisi:**

| # | Item Bank | Jumlah | Match | Item Buku |
|---|-----------|--------|-------|-----------|
| 1 | Setoran Modal Awal 2024 | Rp 500.000.000 | Exact | Setoran Modal Awal 2024 |
| 2 | Transfer Masuk - Konsultasi Core Banking | Rp 196.200.000 | Exact | Konsultasi Core Banking - Milestone 1 |
| 3 | Transfer Keluar - JetBrains License | Rp 3.330.000 | Exact | JetBrains IntelliJ License 2024 |
| 4 | Transfer Keluar - AWS Cloud | Rp 5.550.000 | Exact | AWS Cloud Services Jan 2024 |
| 5 | Biaya Admin Bank BCA | Rp 15.000 | **Bank Only** | *(tidak ada di buku)* |

### Manual Match

Item yang tidak ter-match otomatis dapat dicocokkan secara manual:
1. Klik item bank yang belum cocok
2. Pilih transaksi buku yang sesuai
3. Konfirmasi pencocokan

### Mark Bank-Only / Book-Only

Untuk item yang memang hanya ada di satu sisi:

- **Bank Only:** Transaksi yang ada di bank tapi belum dicatat di buku. Contoh: Biaya Admin Bank BCA Rp 15.000 — perlu dibuat jurnal penyesuaian:
  ```
  Dr. 6.1.01 Biaya Administrasi Bank    Rp 15.000
      Cr. 1.1.02 Bank BCA                   Rp 15.000
  ```

- **Book Only:** Transaksi yang sudah dicatat di buku tapi belum muncul di bank. Contoh: cek yang sudah diterbitkan tapi belum dicairkan oleh penerima.

---

## Laporan Rekonsiliasi

Buka laporan dari halaman detail rekonsiliasi, klik **Laporan**.

![Laporan Rekonsiliasi](screenshots/bank-recon/recon-report.png)

Laporan menampilkan:
- **Header:** Periode, rekening bank, tanggal rekonsiliasi
- **Ringkasan saldo:** Saldo bank vs saldo buku
- **Item cocok:** Daftar transaksi yang berhasil dicocokkan
- **Item Bank Only:** Transaksi bank tanpa pasangan di buku
- **Item Book Only:** Transaksi buku tanpa pasangan di bank
- **Selisih:** Total selisih yang perlu ditindaklanjuti

Gunakan tombol **Print** untuk mencetak atau export laporan.

---

## Contoh Kasus Lengkap

Walk-through rekonsiliasi PT ArtiVisi Intermedia untuk periode Januari 2024.

### Data Awal

- **Rekening:** BCA 1234567890, KCU Sudirman
- **Akun GL:** 1.1.02 Bank BCA
- **Saldo awal:** Rp 500.000.000
- **Saldo akhir (bank):** Rp 687.305.000

### Langkah 1: Import Mutasi

Import file `bca-statement-202401.csv` yang berisi 5 item transaksi.

### Langkah 2: Buat Rekonsiliasi

Pilih mutasi Januari 2024 dan buat rekonsiliasi baru. Sistem menampilkan 5 item bank dan transaksi buku yang tersedia.

### Langkah 3: Jalankan Auto-Match

Klik **Auto-Match**. Hasil: 4 dari 5 item berhasil dicocokkan (Exact Match), 1 item tersisa (Biaya Admin Bank BCA).

### Langkah 4: Tandai Bank Only

Item "Biaya Admin Bank BCA" Rp 15.000 tidak ada di pembukuan. Tandai sebagai **Bank Only**.

Tindak lanjut: buat jurnal penyesuaian untuk mencatat biaya admin tersebut di buku besar.

### Langkah 5: Review Laporan

Buka laporan rekonsiliasi untuk verifikasi:

| Item | Bank | Buku | Status |
|------|------|------|--------|
| Setoran Modal Awal 2024 | Rp 500.000.000 | Rp 500.000.000 | Cocok |
| Konsultasi Core Banking | Rp 196.200.000 | Rp 196.200.000 | Cocok |
| JetBrains License | Rp 3.330.000 | Rp 3.330.000 | Cocok |
| AWS Cloud | Rp 5.550.000 | Rp 5.550.000 | Cocok |
| Biaya Admin Bank BCA | Rp 15.000 | — | Bank Only |

Setelah semua item diproses, rekonsiliasi dapat di-complete.

---

## Tips

1. **Import mutasi setiap bulan** — Lakukan rekonsiliasi minimal 1 bulan sekali agar selisih mudah dilacak.
2. **Jalankan auto-match terlebih dahulu** — Auto-match menangani sebagian besar item, sisakan manual match hanya untuk kasus khusus.
3. **Buat jurnal penyesuaian untuk item Bank Only** — Biaya admin, bunga, dan potongan bank lainnya perlu dicatat di buku besar.
4. **Periksa item Book Only** — Cek beredar (outstanding checks) dan transfer yang belum efektif adalah hal normal, tetapi jika sudah lama harus diinvestigasi.
5. **Gunakan keterangan yang konsisten** — Keterangan transaksi yang mirip antara buku dan bank meningkatkan akurasi auto-match (terutama keyword match).
6. **Simpan file CSV asli** — Arsipkan file CSV dari bank sebagai bukti pendukung audit.

---

## Lihat Juga

- [Setup Awal & Import Seed Data](01-setup-awal.md)
- [Tutorial Akuntansi Dasar](02-tutorial-akuntansi.md) — Jurnal penyesuaian untuk item Bank Only
- [Industri Jasa IT](07-industri-jasa.md) — Contoh transaksi PT ArtiVisi
- [Lampiran: Referensi Akun](12-lampiran-akun.md) — Akun 1.1.02 Bank BCA, 6.1.01 Biaya Admin Bank
