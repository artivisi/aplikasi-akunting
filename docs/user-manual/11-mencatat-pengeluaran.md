# Mencatat Pengeluaran

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini setiap kali Anda **mengeluarkan uang** untuk keperluan bisnis, misalnya:
- Membayar tagihan listrik, air, internet, telepon
- Membayar vendor atau supplier
- Membayar gaji karyawan
- Membayar sewa kantor
- Membeli perlengkapan kantor (kertas, tinta, dll)
- Membayar biaya transportasi, makan, atau operasional lainnya

## Konsep Sederhana

**Pengeluaran = Uang yang keluar dari bisnis Anda.**

Setiap kali Anda mengeluarkan uang, ada dua hal yang terjadi:
1. Saldo bank/kas Anda **berkurang** (uang keluar dari rekening)
2. Beban bisnis Anda **bertambah** (biaya operasional bertambah)

Aplikasi akan mencatat kedua hal ini secara otomatis ketika Anda mengisi form transaksi.

---

## Skenario 1: Bayar Tagihan Listrik (Paling Umum)

**Situasi**: Anda membayar tagihan listrik PLN bulan November sebesar Rp 850.000 via mobile banking.

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**. Anda akan melihat daftar semua transaksi yang pernah dicatat.

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik Tombol "Transaksi Baru"

Di bagian atas halaman, klik tombol biru **Transaksi Baru**. Form pencatatan transaksi akan muncul.

![Form Transaksi Baru](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Beban Listrik"

Di bagian atas form, ada dropdown **Template**. Klik dropdown tersebut dan pilih **Beban Listrik**.

![Daftar Template](screenshots/templates-list.png)

> **Tidak ada template yang cocok?** Pilih template yang paling mirip, atau gunakan template "Beban Operasional" yang lebih umum. Anda juga bisa membuat template baru di menu Template.

### Langkah 4: Isi Form Transaksi

Setelah memilih template, isi field-field berikut:

![Form dengan Template](screenshots/transactions-form.png)

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal Anda membayar | `20 November 2025` |
| **Jumlah** | Berapa rupiah yang dibayar (tanpa titik/koma) | `850000` |
| **Akun Sumber** | Dari rekening/kas mana uang keluar | `Bank BCA` atau `Kas Kecil` |
| **Keterangan** | Catatan untuk referensi nanti | `Tagihan listrik PLN November 2025` |
| **No. Referensi** | Nomor tagihan atau bukti bayar | `PLN-12345678` |

> **Tips Akun Sumber**:
> - Pilih **Bank BCA/Mandiri/dll** jika bayar via transfer/mobile banking
> - Pilih **Kas Kecil** jika bayar cash/tunai

### Langkah 5: Periksa Preview Jurnal

Di bagian bawah form, ada **Preview Jurnal**:

```
Debit  : Beban Listrik   Rp 850.000  (biaya listrik bertambah)
Kredit : Bank BCA        Rp 850.000  (saldo bank berkurang)
```

> **Cara Membaca Preview**:
> - **Debit Beban Listrik** = Biaya listrik bulan ini bertambah Rp 850 ribu
> - **Kredit Bank BCA** = Uang di rekening BCA Anda berkurang Rp 850 ribu

### Langkah 6: Simpan dan Posting

Klik tombol **Simpan & Posting** untuk menyimpan transaksi.

### Langkah 7: Verifikasi Transaksi

Setelah berhasil, Anda akan melihat halaman detail transaksi.

![Detail Transaksi](screenshots/transactions-detail.png)

### Langkah 8: Cek di Dashboard

Buka **Dashboard** untuk memastikan saldo sudah ter-update.

![Dashboard](screenshots/dashboard.png)

Periksa kartu **Kas & Bank** - saldo seharusnya sudah berkurang Rp 850.000.

---

## Skenario 2: Bayar Internet Bulanan

**Situasi**: Membayar tagihan internet kantor Rp 500.000.

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Beban Internet"

![Pilih Template](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembayaran | `20 November 2025` |
| **Jumlah** | Nilai tagihan | `500000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Keterangan** | Catatan | `Internet kantor November 2025` |

### Langkah 5: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 6: Verifikasi

![Detail Transaksi](screenshots/transactions-detail.png)

---

## Skenario 3: Bayar Vendor/Supplier dengan Potongan Pajak (PPh 23)

**Situasi**: Anda menggunakan jasa desainer freelance untuk membuat logo seharga Rp 5.000.000. Menurut aturan pajak, Anda wajib memotong PPh 23 sebesar 2%.

**Perhitungan**:
- Nilai jasa: Rp 5.000.000
- PPh 23 (2%): Rp 100.000
- Yang dibayar ke desainer: Rp 4.900.000
- Yang harus disetor ke negara: Rp 100.000

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Pembayaran Jasa (PPh 23)"

![Pilih Template PPh 23](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembayaran | `20 November 2025` |
| **Jumlah** | Nilai bruto (sebelum potong) | `5000000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Keterangan** | Catatan | `Jasa desain logo - CV Kreatif` |

### Langkah 5: Periksa Preview Jurnal

```
Debit  : Beban Jasa Profesional   Rp 5.000.000  (biaya jasa)
Kredit : Bank BCA                 Rp 4.900.000  (uang yang dibayar ke vendor)
Kredit : Hutang PPh 23            Rp   100.000  (pajak yang harus disetor)
```

### Langkah 6: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 7: Verifikasi

![Detail Transaksi PPh 23](screenshots/transactions-detail.png)

> **Ingat**: PPh 23 yang Anda potong dari vendor harus disetor ke negara paling lambat tanggal 10 bulan berikutnya. Lihat [Kalender Pajak](33-kalender-pajak.md).

---

## Skenario 4: Bayar Gaji Karyawan

**Situasi**: Membayar gaji karyawan Budi sebesar Rp 8.000.000.

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Beban Gaji"

![Pilih Template Gaji](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal gaji dibayar | `25 November 2025` |
| **Jumlah** | Nilai gaji | `8000000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Keterangan** | Catatan | `Gaji November 2025 - Budi Santoso` |

### Langkah 5: Periksa Preview Jurnal

```
Debit  : Beban Gaji      Rp 8.000.000  (biaya gaji)
Kredit : Bank BCA        Rp 8.000.000  (uang keluar)
```

### Langkah 6: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 7: Verifikasi

![Detail Transaksi Gaji](screenshots/transactions-detail.png)

> **Untuk gaji dengan potongan pajak/BPJS**: Gunakan fitur Payroll yang lebih lengkap. Lihat [Proses Payroll](64-payroll-processing.md).

---

## Skenario 5: Bayar Sewa Kantor Setahun Dimuka

**Situasi**: Anda membayar sewa kantor Rp 24.000.000 untuk 12 bulan kedepan (Januari-Desember 2025).

> **Catatan Penting**: Sewa yang dibayar dimuka tidak langsung dicatat sebagai biaya. Ini dicatat sebagai "Aset" (uang yang masih ada nilainya), lalu dialokasikan ke biaya setiap bulan.

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Bayar Sewa Dimuka"

![Pilih Template Sewa](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembayaran | `1 Januari 2025` |
| **Jumlah** | Total sewa setahun | `24000000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Keterangan** | Catatan | `Sewa kantor Jan-Des 2025` |

### Langkah 5: Periksa Preview Jurnal

```
Debit  : Sewa Dibayar Dimuka   Rp 24.000.000  (aset/uang muka)
Kredit : Bank BCA              Rp 24.000.000  (uang keluar)
```

### Langkah 6: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 7: Verifikasi

![Detail Transaksi Sewa](screenshots/transactions-detail.png)

### Langkah 8: Buat Jadwal Amortisasi

Buka menu **Jadwal Amortisasi** untuk membuat amortisasi otomatis setiap bulan.

![Jadwal Amortisasi](screenshots/amortization-list.png)

> **Langkah Tambahan**: Buat jadwal amortisasi agar setiap bulan otomatis dicatat biaya sewa Rp 2.000.000. Lihat [Jadwal Amortisasi](53-jadwal-amortisasi.md).

---

## Skenario 6: Beli Perlengkapan Kantor (ATK)

**Situasi**: Anda membeli kertas, tinta printer, dan alat tulis senilai Rp 350.000 secara tunai.

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Beban ATK"

![Pilih Template ATK](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembelian | `15 November 2025` |
| **Jumlah** | Nilai pembelian | `350000` |
| **Akun Sumber** | **Kas Kecil** (karena bayar tunai) | `Kas Kecil` |
| **Keterangan** | Catatan | `Beli kertas HVS, tinta printer, dan pulpen` |

### Langkah 5: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 6: Verifikasi

![Detail Transaksi ATK](screenshots/transactions-detail.png)

> **Tips**: Jangan lupa foto struk dan lampirkan ke transaksi. Lihat [Telegram Receipt](13-telegram-receipt.md).

---

## Skenario 7: Bayar dengan PPN (Pembelian dari Vendor PKP)

**Situasi**: Anda membeli software subscription seharga Rp 1.110.000 (sudah termasuk PPN 11%) dari vendor yang PKP (Pengusaha Kena Pajak).

**Perhitungan**:
- Total bayar: Rp 1.110.000
- PPN (11%): Rp 110.000
- Harga barang: Rp 1.000.000

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Pembelian dengan PPN"

![Pilih Template PPN](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembayaran | `20 November 2025` |
| **Jumlah** | Total termasuk PPN | `1110000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Keterangan** | Catatan | `Subscription Adobe Creative Cloud` |

### Langkah 5: Periksa Preview Jurnal

```
Debit  : Beban Software      Rp 1.000.000  (biaya software)
Debit  : PPN Masukan         Rp   110.000  (PPN yang bisa diklaim)
Kredit : Bank BCA            Rp 1.110.000  (uang keluar)
```

### Langkah 6: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 7: Verifikasi

![Detail Transaksi PPN](screenshots/transactions-detail.png)

### Langkah 8: Cek Laporan PPN

Buka **Laporan PPN** untuk melihat PPN Masukan yang bisa dikreditkan.

![Laporan PPN](screenshots/reports-ppn-summary.png)

> **Apa itu PPN Masukan?** Ini adalah PPN yang Anda bayar saat membeli barang/jasa. PPN ini bisa dikurangkan dari PPN yang Anda tagihkan ke klien, sehingga Anda tidak perlu setor penuh ke negara.

---

## Skenario 8: Pengeluaran untuk Proyek Tertentu

**Situasi**: Anda membayar biaya hosting Rp 500.000 khusus untuk proyek website klien PT ABC.

### Langkah 1: Buka Menu Transaksi

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik "Transaksi Baru"

![Form Transaksi](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Beban Proyek"

![Pilih Template Proyek](screenshots/templates-list.png)

### Langkah 4: Isi Form

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal pembayaran | `20 November 2025` |
| **Jumlah** | Nilai pembayaran | `500000` |
| **Akun Sumber** | Rekening pembayaran | `Bank BCA` |
| **Proyek** | Pilih proyek dari dropdown | `PRJ-2025-001 - Website PT ABC` |
| **Keterangan** | Catatan | `Biaya hosting untuk website PT ABC` |

### Langkah 5: Simpan dan Posting

Klik **Simpan & Posting**.

### Langkah 6: Verifikasi

![Detail Transaksi Proyek](screenshots/transactions-detail.png)

### Langkah 7: Cek Profitabilitas Proyek

Buka laporan **Profitabilitas Proyek** untuk melihat biaya proyek ini.

![Laporan Profitabilitas Proyek](screenshots/reports-project-profitability.png)

> **Kenapa pilih proyek?** Agar biaya ini masuk ke perhitungan profitabilitas proyek. Anda bisa lihat apakah proyek ini untung atau rugi di [Analisis Profitabilitas](43-analisis-profitabilitas.md).

---

## Tips Praktis

1. **Catat segera** - Jangan menumpuk struk. Catat pengeluaran segera setelah membayar.

2. **Foto dan simpan struk** - Gunakan fitur [Telegram Receipt](13-telegram-receipt.md) untuk foto struk dari HP dan langsung masuk ke sistem.

3. **Pilih template yang tepat** - Template menentukan akun mana yang digunakan. Pilih yang paling sesuai dengan jenis pengeluaran.

4. **Hubungkan ke proyek** - Jika pengeluaran terkait proyek klien tertentu, selalu pilih proyeknya agar bisa tracking profitabilitas.

5. **Pisahkan pribadi dan bisnis** - Jangan mencampur pengeluaran pribadi dengan pengeluaran bisnis.

---

## Troubleshooting

| Masalah | Penyebab | Solusi |
|---------|----------|--------|
| Template tidak ada | Belum dibuat | Buat template baru di menu Template |
| Saldo tidak berubah | Transaksi masih Draft | Buka transaksi dan klik Posting |
| Lupa pilih proyek | Sudah Posted | Void transaksi, buat ulang dengan proyek yang benar |
| PPh tidak terpotong otomatis | Menggunakan template biasa | Gunakan template dengan PPh 23 |
| Akun sumber tidak muncul | Akun tidak aktif atau bukan tipe Kas/Bank | Cek di menu Akun, pastikan aktif dan tipe benar |

---

## Lihat Juga

- [Transaksi PPN](30-transaksi-ppn.md) - Detail pembelian dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Pemotongan PPh 23/21
- [Jadwal Amortisasi](53-jadwal-amortisasi.md) - Untuk sewa atau asuransi dibayar dimuka
- [Telegram Receipt](13-telegram-receipt.md) - Kirim foto struk dari HP
- [Tracking Proyek](41-tracking-proyek.md) - Menghubungkan biaya ke proyek
