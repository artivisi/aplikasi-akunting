# Perpajakan

Panduan pencatatan transaksi pajak dan laporan untuk kepatuhan perpajakan Indonesia.

## Jenis Pajak di Indonesia

### Pajak yang Dikelola Aplikasi

| Pajak | Tarif | Kewajiban |
|-------|-------|-----------|
| **PPN** | 11% | PKP yang menyerahkan BKP/JKP |
| **PPh 21** | Progresif | Pemberi kerja (pemotongan gaji) |
| **PPh 23** | 2% (jasa), 15% (dividen) | Pemberi penghasilan |
| **PPh 25** | Angsuran | Wajib pajak badan |
| **PPh 4(2)** | Final (varies) | Transaksi tertentu |

---

## Transaksi PPN

### Konsep PPN (DPP Nilai Lain — PMK 131/2024)

**PPN Keluaran** - PPN yang Anda pungut dari pembeli
**PPN Masukan** - PPN yang Anda bayar ke penjual

```
Net PPN = PPN Keluaran - PPN Masukan
```

Sejak 1 Januari 2025, perhitungan PPN menggunakan DPP Nilai Lain (PMK 131/2024):
- **DPP** = Harga Jual × 11/12
- **PPN** = DPP × 12% = **Harga Jual × 11%**
- Field `amount` di template = **Harga Jual** (sebelum PPN)

### Mencatat Pendapatan dengan PPN

1. Buka menu **Transaksi** > **Transaksi Baru**

![Form Transaksi](screenshots/transactions-form.png)

2. Pilih template **Pendapatan Jasa + PPN**
3. Isi Harga Jual (sebelum PPN), contoh: Rp 10.000.000
4. Preview jurnal:
   ```
   Dr. Bank                    11.100.000  (amount * 1.11)
       Cr. Hutang PPN              1.100.000  (amount * 0.11)
       Cr. Pendapatan             10.000.000  (amount)
   ```
5. Klik **Simpan & Posting**

### Mencatat Pembelian dengan PPN Masukan

1. Pilih template **Pembelian dengan PPN**
2. Isi Harga Jual (sebelum PPN), contoh: Rp 10.000.000
3. Preview jurnal:
   ```
   Dr. Beban/Aset             10.000.000  (amount)
   Dr. PPN Masukan             1.100.000  (amount * 0.11)
       Cr. Bank                   11.100.000  (amount * 1.11)
   ```

### Pendapatan Jasa BUMN (FP 03)

Untuk klien BUMN/Pemerintah yang menggunakan Faktur Pajak kode 03 (PPN dipungut pembeli):

1. Pilih template **Pendapatan Jasa BUMN (FP 03)**
2. Isi Harga Jual, contoh: Rp 10.000.000
3. Preview jurnal:
   ```
   Dr. Bank                     9.800.000  (amount * 0.98)
   Dr. Kredit PPh 23              200.000  (amount * 0.02)
       Cr. Pendapatan            10.000.000  (amount)
   ```
4. PPN tidak masuk jurnal karena dipungut dan disetor oleh pembeli (BUMN)

### Laporan Ringkasan PPN

Buka menu **Laporan** > **Ringkasan PPN**.

![Ringkasan PPN](screenshots/reports-ppn-summary.png)

---

## Transaksi PPh

### PPh 23 - Pemotongan atas Jasa

Saat membayar jasa ke vendor (selain pegawai):

1. Pilih template **Bayar Jasa + PPh 23**
2. Isi nilai bruto
3. Sistem menghitung PPh 23 (2%)
4. Preview jurnal:
   ```
   Dr. Beban Jasa             10.000.000
       Cr. Hutang PPh 23           200.000
       Cr. Bank                  9.800.000
   ```

### PPh 21 - Pemotongan Gaji

Lihat bagian [Penggajian](05-penggajian.md) untuk detail PPh 21 karyawan.

### Laporan PPh

Buka menu **Laporan** > **Bukti Potong PPh 23**.

![Bukti Potong PPh 23](screenshots/reports-pph23-withholding.png)

### Ringkasan Pajak

Buka menu **Laporan** > **Ringkasan Pajak**.

![Ringkasan Pajak](screenshots/reports-tax-summary.png)

---

## Periode Fiskal

Periode fiskal mengontrol kapan transaksi dapat diposting. Setiap bulan memiliki status yang menentukan apakah jurnal dapat dibuat di bulan tersebut.

### Melihat Periode Fiskal

Buka menu **Pengaturan** > **Periode Fiskal** di sidebar.

![Daftar Periode Fiskal](screenshots/fiscal-periods-list.png)

Fitur daftar periode:
- **Filter tahun** — pilih tahun untuk melihat 12 periode
- **Filter status** — tampilkan semua atau filter berdasarkan status tertentu

### Generate Periode Satu Tahun

Untuk membuat 12 periode sekaligus:

1. Isi tahun pada kolom **Tahun** di bagian atas halaman
2. Klik **Generate**
3. Sistem membuat periode Januari–Desember untuk tahun tersebut (periode yang sudah ada dilewati)

Atau buat periode satuan via **Periode Baru** (pilih tahun dan bulan manual).

### Status Periode

| Status | Label | Arti |
|--------|-------|------|
| OPEN | Terbuka | Transaksi dapat diposting ke periode ini |
| MONTH_CLOSED | Tutup Bulan | Transaksi tidak dapat diposting; periode masih bisa dibuka kembali |
| TAX_FILED | SPT Dilaporkan | SPT sudah dilaporkan ke DJP; periode tidak bisa dibuka kembali |

Alur status:

```
OPEN → MONTH_CLOSED → TAX_FILED
```

### Detail Periode

Klik periode di daftar untuk melihat detail:
- Tanggal mulai dan berakhir
- Siapa dan kapan menutup bulan
- Siapa dan kapan melaporkan SPT
- Visualisasi alur status (3 tahap)

### Menutup Periode

1. Buka detail periode yang berstatus **Terbuka**
2. Klik **Tutup Bulan**
3. Konfirmasi pada dialog

Setelah ditutup, sistem memblokir posting transaksi ke periode tersebut. Jika ada transaksi draf yang belum diposting di periode tersebut, penutupan akan ditolak.

### Melaporkan SPT

Setelah SPT Masa sudah dilaporkan ke DJP:

1. Buka detail periode yang berstatus **Tutup Bulan**
2. Klik **Lapor SPT**
3. Konfirmasi pada dialog

Periode dengan status **SPT Dilaporkan** tidak bisa dibuka kembali.

### Membuka Kembali Periode

Periode dengan status **Tutup Bulan** dapat dibuka kembali:

1. Buka detail periode
2. Klik **Buka Kembali**
3. Konfirmasi pada dialog

Periode dengan status **SPT Dilaporkan** tidak bisa dibuka kembali.

### Posting Guard

Saat pengguna mencoba memposting transaksi ke bulan yang sudah ditutup, sistem menampilkan pesan error dan transaksi tidak akan diposting. Pastikan periode dalam status **Terbuka** sebelum memposting transaksi.

---

## Kalender Pajak

### Melihat Kalender Pajak

Buka menu **Pajak** > **Kalender Pajak**.

![Kalender Pajak](screenshots/tax-calendar.png)

### Deadline Pajak Standar

| Pajak | Setor | Lapor |
|-------|-------|-------|
| PPN | Akhir bulan berikutnya | Akhir bulan berikutnya |
| PPh 21 | Tgl 15 bulan berikutnya | Tgl 20 bulan berikutnya |
| PPh 23 | Tgl 15 bulan berikutnya | Tgl 20 bulan berikutnya |
| PPh 4(2) | Tgl 15 bulan berikutnya | Tgl 20 bulan berikutnya |
| PPh 25 | Tgl 15 bulan berikutnya | Tgl 20 bulan berikutnya |

> **Catatan:** Deadline penyetoran PPh 21, PPh 23, dan PPh 4(2) berubah dari tanggal 10 menjadi tanggal 15 sesuai PMK 81/2024.

### Kalender Tahunan

![Kalender Pajak Tahunan](screenshots/tax-calendar-yearly.png)

### Menandai Selesai

1. Klik deadline yang sudah diselesaikan
2. Isi nomor bukti setor/lapor
3. Klik **Selesai**

---

## Referensi Regulasi

### Tarif PPh 21 (TER 2024)

| PKP Tahunan | Tarif |
|-------------|-------|
| s.d. Rp 60.000.000 | 5% |
| > Rp 60.000.000 - Rp 250.000.000 | 15% |
| > Rp 250.000.000 - Rp 500.000.000 | 25% |
| > Rp 500.000.000 - Rp 5.000.000.000 | 30% |
| > Rp 5.000.000.000 | 35% |

### PTKP (Penghasilan Tidak Kena Pajak)

| Status | PTKP/Tahun |
|--------|------------|
| TK/0 | Rp 54.000.000 |
| K/0 | Rp 58.500.000 |
| K/1 | Rp 63.000.000 |
| K/2 | Rp 67.500.000 |
| K/3 | Rp 72.000.000 |

### Tarif PPh 23

| Objek | Tarif |
|-------|-------|
| Dividen | 15% |
| Bunga | 15% |
| Royalti | 15% |
| Jasa (umum) | 2% |
| Sewa (selain tanah/bangunan) | 2% |

---

## Tips Kepatuhan

1. **Catat tepat waktu** - Jangan menunda pencatatan transaksi pajak
2. **Simpan bukti** - Faktur pajak, bukti potong, NTPN
3. **Rekonsiliasi bulanan** - Cocokkan saldo akun pajak
4. **Gunakan kalender** - Set reminder untuk deadline
5. **Konsultasi** - Hubungi konsultan pajak untuk kasus kompleks

---

## Lihat Juga

- [Penggajian](05-penggajian.md) - PPh 21 karyawan
- [Tutorial Akuntansi](02-tutorial-akuntansi.md) - Jurnal pajak
- [Referensi Template](12-lampiran-template.md) - Template transaksi pajak
