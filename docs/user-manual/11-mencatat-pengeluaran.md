# Mencatat Pengeluaran

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Membayar tagihan utilitas (listrik, air, internet)
- Membayar vendor atau supplier
- Membayar gaji karyawan
- Membayar sewa kantor
- Membeli perlengkapan atau peralatan kantor
- Pengeluaran operasional lainnya

## Konsep yang Perlu Dipahami

**Beban/Pengeluaran** adalah biaya yang dikeluarkan untuk operasional bisnis. Dalam akuntansi:
- Beban dicatat di sisi **debit**
- Kas/Bank yang dibayarkan dicatat di sisi **kredit**
- Jika ada PPN atau pemotongan PPh, lihat [Transaksi PPN](30-transaksi-ppn.md) dan [Transaksi PPh](31-transaksi-pph.md)

---

## Skenario 1: Bayar Tagihan Listrik

**Situasi**: Membayar tagihan listrik PLN sebesar Rp 850.000.

### Langkah 1: Buka Menu Transaksi

Klik menu **Transaksi** di sidebar kiri untuk melihat daftar transaksi.

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Buat Transaksi Baru

Klik tombol **Transaksi Baru** di bagian atas halaman.

![Form Transaksi Baru](screenshots/transactions-form.png)

### Langkah 3: Pilih Template dan Isi Form

1. Pilih template **Beban Listrik**
2. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `850000`
   - **Akun Sumber**: Pilih rekening yang digunakan (contoh: Bank BCA atau Kas Kecil)
   - **Keterangan**: `Tagihan listrik November 2025`
   - **No. Referensi**: Nomor tagihan PLN

### Langkah 4: Periksa Preview Jurnal

Periksa **Preview Jurnal**:

```
Debit  : Beban Listrik   Rp 850.000
Kredit : Bank BCA        Rp 850.000
```

### Langkah 5: Simpan Transaksi

Klik **Simpan & Posting**.

**Hasil**: Beban Listrik bertambah, saldo Bank berkurang.

---

## Skenario 2: Bayar Tagihan Internet

**Situasi**: Membayar tagihan internet bulanan Rp 500.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Beban Internet**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `500000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Internet kantor November 2025`
5. Klik **Simpan & Posting**

---

## Skenario 3: Bayar Vendor dengan PPh 23

**Situasi**: Membayar vendor jasa desain Rp 5.000.000, dipotong PPh 23 (2%).

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pembayaran Jasa (PPh 23)**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `5000000` (nilai bruto sebelum potong)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pembayaran jasa desain logo - CV Kreatif`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Jasa Profesional   Rp 5.000.000
   Kredit : Bank BCA                 Rp 4.900.000 (nett setelah potong)
   Kredit : Hutang PPh 23            Rp   100.000 (2% x 5.000.000)
   ```
6. Klik **Simpan & Posting**

**Hasil**: PPh 23 yang dipotong akan muncul di [Laporan Pajak](32-laporan-pajak.md) untuk disetor ke negara.

> Catatan: Untuk detail pemotongan PPh 23, lihat [Transaksi PPh](31-transaksi-pph.md).

---

## Skenario 4: Bayar Gaji Karyawan

**Situasi**: Membayar gaji karyawan Rp 8.000.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Beban Gaji**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran gaji
   - **Jumlah**: `8000000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Gaji November 2025 - Budi Santoso`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Gaji      Rp 8.000.000
   Kredit : Bank BCA        Rp 8.000.000
   ```
6. Klik **Simpan & Posting**

> Catatan: Jika ada pemotongan PPh 21, gunakan template yang sesuai. Lihat [Transaksi PPh](31-transaksi-pph.md).

---

## Skenario 5: Bayar Sewa Kantor (Setahun Dimuka)

**Situasi**: Membayar sewa kantor Rp 24.000.000 untuk 12 bulan kedepan.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Bayar Sewa Dimuka**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `24000000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Sewa kantor Jan-Des 2025`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Sewa Dibayar Dimuka   Rp 24.000.000
   Kredit : Bank BCA              Rp 24.000.000
   ```
6. Klik **Simpan & Posting**
7. Buat **Jadwal Amortisasi** untuk mengalokasikan beban ke setiap bulan. Lihat [Jadwal Amortisasi](53-jadwal-amortisasi.md).

**Hasil**: Sewa dicatat sebagai aset (dibayar dimuka), lalu diamortisasi Rp 2.000.000/bulan ke Beban Sewa.

---

## Skenario 6: Beli ATK/Perlengkapan Kantor

**Situasi**: Membeli perlengkapan kantor (kertas, tinta printer, dll) Rp 350.000 tunai.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Beban ATK**
4. Isi form:
   - **Tanggal**: Tanggal pembelian
   - **Jumlah**: `350000`
   - **Akun Sumber**: Kas Kecil
   - **Keterangan**: `Beli kertas HVS dan tinta printer`
5. Klik **Simpan & Posting**

---

## Skenario 7: Bayar Vendor dengan PPN

**Situasi**: Membayar pembelian software subscription Rp 1.110.000 (sudah termasuk PPN 11%).

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pembelian dengan PPN**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `1110000` (nilai inklusif PPN)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Subscription Adobe Creative Cloud`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Software      Rp 1.000.000 (DPP)
   Debit  : PPN Masukan         Rp   110.000 (dapat dikreditkan)
   Kredit : Bank BCA            Rp 1.110.000
   ```
6. Klik **Simpan & Posting**

**Hasil**: PPN Masukan dapat dikreditkan dengan PPN Keluaran. Lihat [Laporan Pajak](32-laporan-pajak.md).

---

## Skenario 8: Pengeluaran Terkait Proyek

**Situasi**: Membayar biaya hosting Rp 500.000 untuk proyek klien tertentu.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Beban Proyek**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `500000`
   - **Akun Sumber**: Bank BCA
   - **Proyek**: Pilih proyek terkait (contoh: PRJ-2025-001)
   - **Keterangan**: `Biaya hosting untuk Proyek Website PT ABC`
5. Klik **Simpan & Posting**

**Hasil**: Pengeluaran akan masuk ke analisis profitabilitas proyek. Lihat [Analisis Profitabilitas](43-analisis-profitabilitas.md).

---

## Tips

1. **Kategorikan dengan tepat** - Pilih template yang sesuai agar laporan akurat
2. **Simpan bukti** - Foto struk dan lampirkan ke transaksi. Lihat [Telegram Receipt](13-telegram-receipt.md)
3. **Gunakan proyek** - Hubungkan pengeluaran ke proyek untuk tracking biaya
4. **Amortisasi** - Untuk pembayaran dimuka, buat jadwal amortisasi

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Template tidak sesuai | Buat template baru atau duplikat yang ada. Lihat [Kelola Template](51-kelola-template.md) |
| Lupa hubungkan ke proyek | Edit transaksi draft, atau void dan buat ulang |
| PPh tidak terpotong | Gunakan template dengan PPh, atau edit formula template |

## Lihat Juga

- [Transaksi PPN](30-transaksi-ppn.md) - Pembelian dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Pemotongan PPh 23/21
- [Jadwal Amortisasi](53-jadwal-amortisasi.md) - Beban dibayar dimuka
- [Telegram Receipt](13-telegram-receipt.md) - Lampirkan bukti pembayaran
- [Tracking Proyek](41-tracking-proyek.md) - Hubungkan ke proyek
