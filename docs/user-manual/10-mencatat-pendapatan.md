# Mencatat Pendapatan

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Menerima pembayaran dari klien
- Mencatat penjualan jasa atau produk
- Menerima pendapatan bunga atau pendapatan lainnya

## Konsep yang Perlu Dipahami

**Pendapatan** adalah penerimaan dari aktivitas bisnis utama. Dalam akuntansi:
- Pendapatan dicatat di sisi **kredit**
- Kas/Bank yang diterima dicatat di sisi **debit**
- Jika ada PPN, lihat [Transaksi PPN](30-transaksi-ppn.md)

---

## Skenario 1: Terima Pembayaran Jasa (Tanpa PPN)

**Situasi**: Klien membayar invoice jasa konsultasi sebesar Rp 5.000.000 via transfer bank.

### Langkah 1: Buka Menu Transaksi

Klik menu **Transaksi** di sidebar kiri untuk melihat daftar transaksi.

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Buat Transaksi Baru

Klik tombol **Transaksi Baru** di bagian atas halaman. Form transaksi akan muncul.

![Form Transaksi Baru](screenshots/transactions-form.png)

### Langkah 3: Pilih Template dan Isi Form

1. Pilih template **Pendapatan Jasa** dari dropdown
2. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `5000000`
   - **Akun Sumber**: Pilih rekening bank penerima (contoh: Bank BCA)
   - **Keterangan**: `Pembayaran invoice INV-2025-001 - Konsultasi IT`
   - **No. Referensi**: `INV-2025-001`

### Langkah 4: Periksa Preview Jurnal

Periksa **Preview Jurnal** untuk memastikan entri sudah benar:

```
Debit  : Bank BCA           Rp 5.000.000
Kredit : Pendapatan Jasa    Rp 5.000.000
```

### Langkah 5: Simpan Transaksi

Klik **Simpan & Posting** untuk menyimpan dan memposting transaksi. Detail transaksi akan ditampilkan.

![Detail Transaksi](screenshots/transactions-detail.png)

**Hasil**: Saldo Bank BCA bertambah Rp 5.000.000, Pendapatan Jasa bertambah Rp 5.000.000.

---

## Skenario 2: Terima Pembayaran Jasa dengan PPN

**Situasi**: Klien membayar invoice jasa sebesar Rp 11.100.000 (sudah termasuk PPN 11%).

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pendapatan Jasa dengan PPN**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `11100000` (nilai inklusif PPN)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pembayaran invoice INV-2025-002 - Jasa Pengembangan Website`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA           Rp 11.100.000
   Kredit : Hutang PPN         Rp  1.100.000 (PPN yang harus disetor)
   Kredit : Pendapatan Jasa    Rp 10.000.000 (DPP)
   ```
6. Klik **Simpan & Posting**

**Hasil**: PPN Keluaran sebesar Rp 1.100.000 akan muncul di [Laporan Pajak](32-laporan-pajak.md).

> Catatan: Untuk detail perhitungan PPN, lihat [Transaksi PPN](30-transaksi-ppn.md).

---

## Skenario 3: Terima DP (Down Payment) Proyek

**Situasi**: Klien membayar DP 30% untuk proyek senilai Rp 50.000.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Terima DP Proyek**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `15000000` (30% x 50.000.000)
   - **Akun Sumber**: Bank BCA
   - **Proyek**: Pilih proyek terkait
   - **Keterangan**: `DP 30% Proyek Redesign Website`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA                    Rp 15.000.000
   Kredit : Pendapatan Diterima Dimuka  Rp 15.000.000
   ```
6. Klik **Simpan & Posting**

**Hasil**: DP dicatat sebagai kewajiban (pendapatan diterima dimuka). Pendapatan akan diakui saat milestone selesai. Lihat [Tracking Proyek](41-tracking-proyek.md).

---

## Skenario 4: Terima Pendapatan Bunga

**Situasi**: Bank memberikan bunga deposito Rp 250.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pendapatan Bunga**
4. Isi form:
   - **Tanggal**: Tanggal bunga dikreditkan
   - **Jumlah**: `250000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Bunga deposito November 2025`
5. Klik **Simpan & Posting**

---

## Skenario 5: Terima Pelunasan Piutang

**Situasi**: Klien yang sebelumnya berutang melunasi piutang Rp 8.000.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Terima Pelunasan Piutang**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `8000000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pelunasan piutang PT ABC`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA        Rp 8.000.000
   Kredit : Piutang Usaha   Rp 8.000.000
   ```
6. Klik **Simpan & Posting**

**Hasil**: Saldo Piutang Usaha berkurang, Kas bertambah.

---

## Tips

1. **Catat segera** - Catat pendapatan segera setelah uang diterima untuk akurasi laporan
2. **Gunakan referensi** - Selalu isi nomor invoice/referensi untuk memudahkan rekonsiliasi
3. **Pilih proyek** - Jika pendapatan terkait proyek, selalu pilih proyeknya untuk tracking profitabilitas
4. **Periksa preview** - Selalu periksa Preview Jurnal sebelum posting

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Template tidak muncul | Periksa apakah template aktif di menu Template |
| Saldo tidak berubah | Pastikan status transaksi sudah "Posted" bukan "Draft" |
| Nilai PPN salah | Periksa formula di template, lihat [Kelola Template](51-kelola-template.md) |

## Lihat Juga

- [Transaksi PPN](30-transaksi-ppn.md) - Pendapatan dengan PPN
- [Tracking Proyek](41-tracking-proyek.md) - Menghubungkan pendapatan ke proyek
- [Invoice & Penagihan](42-invoice-penagihan.md) - Membuat invoice untuk klien
- [Laporan Harian](20-laporan-harian.md) - Cek transaksi yang sudah dicatat
