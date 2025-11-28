# Laporan Pajak

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Perlu menghitung PPN yang harus disetor
- Ingin tracking PPh 23 yang sudah dipotong
- Menyiapkan data untuk SPT Masa PPN dan PPh
- Melakukan rekonsiliasi akun-akun pajak

## Konsep yang Perlu Dipahami

### Jenis Laporan Pajak

| Laporan | Fungsi | Periode |
|---------|--------|---------|
| **Ringkasan PPN** | Hitung PPN Keluaran vs Masukan | Bulanan |
| **Pemotongan PPh 23** | Tracking PPh yang dipotong | Bulanan |
| **Ringkasan Pajak** | Overview semua akun pajak | Periodik |

### Kalender Pajak Bulanan

| Kewajiban | Batas Waktu |
|-----------|-------------|
| Setor PPN | Tanggal 15 bulan berikutnya |
| Lapor SPT Masa PPN | Tanggal 20 bulan berikutnya |
| Setor PPh 21/23 | Tanggal 10 bulan berikutnya |
| Lapor SPT Masa PPh 21/23 | Tanggal 20 bulan berikutnya |

## Skenario 1: Cetak Ringkasan PPN Bulanan

**Situasi**: Akhir bulan, Anda perlu mengetahui status PPN untuk penyetoran.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Scroll ke bagian **Laporan Pajak**
3. Klik **Ringkasan PPN**
4. Pilih periode:
   - **Tanggal Awal**: 1 November 2025
   - **Tanggal Akhir**: 30 November 2025
5. Klik **Tampilkan**

**Hasil yang Ditampilkan**:

```
RINGKASAN PPN
Periode: 1 November 2025 - 30 November 2025

PPN KELUARAN
────────────────────────────────────────────
Penjualan Jasa           Rp  5.500.000
Penjualan Lainnya        Rp    550.000
────────────────────────────────────────────
Total PPN Keluaran       Rp  6.050.000

PPN MASUKAN
────────────────────────────────────────────
Pembelian Aset           Rp  1.100.000
Beban Operasional        Rp    550.000
────────────────────────────────────────────
Total PPN Masukan        Rp  1.650.000

PERHITUNGAN
────────────────────────────────────────────
PPN Keluaran             Rp  6.050.000
PPN Masukan             (Rp  1.650.000)
────────────────────────────────────────────
Net PPN                  Rp  4.400.000

Status: KURANG BAYAR
Jumlah yang harus disetor: Rp 4.400.000
Batas setor: 15 Desember 2025
```

**Cara Ekspor**:
- Klik **Cetak** untuk versi print-friendly
- Gunakan fitur print browser (Ctrl+P)

## Skenario 2: Cetak Laporan PPh 23

**Situasi**: Anda perlu data PPh 23 yang sudah dipotong untuk SPT Masa.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Scroll ke bagian **Laporan Pajak**
3. Klik **Pemotongan PPh 23**
4. Pilih periode November 2025
5. Klik **Tampilkan**

**Hasil yang Ditampilkan**:

```
PEMOTONGAN PPh 23
Periode: November 2025

RINCIAN PEMOTONGAN
────────────────────────────────────────────
Tanggal     Vendor              DPP           PPh 23
05/11/2025  CV Kreatif Design   5.000.000     100.000
12/11/2025  PT Konsultan ABC    10.000.000    200.000
20/11/2025  Freelancer XYZ      3.000.000      60.000
────────────────────────────────────────────
TOTAL                           18.000.000    360.000

PENYETORAN
────────────────────────────────────────────
Total Dipotong                   Rp   360.000
Total Disetor                    Rp         0
────────────────────────────────────────────
Saldo Hutang PPh 23              Rp   360.000

Batas setor: 10 Desember 2025
Batas lapor: 20 Desember 2025
```

## Skenario 3: Cetak Ringkasan Semua Akun Pajak

**Situasi**: Anda ingin overview semua kewajiban pajak dalam satu tampilan.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Scroll ke bagian **Laporan Pajak**
3. Klik **Ringkasan Pajak**
4. Pilih periode
5. Klik **Tampilkan**

**Hasil yang Ditampilkan**:

```
RINGKASAN PAJAK
Per 30 November 2025

AKUN PAJAK              SALDO         STATUS
────────────────────────────────────────────────────
PPN Masukan (1.1.25)    1.650.000 Dr  Dapat dikreditkan
Hutang PPN (2.1.03)     6.050.000 Cr  Harus disetor
Hutang PPh 21 (2.1.20)    500.000 Cr  Harus disetor
Hutang PPh 23 (2.1.21)    360.000 Cr  Harus disetor
Hutang PPh 4(2) (2.1.22)        0     -
Hutang PPh 25 (2.1.23)  1.000.000 Cr  Angsuran bulanan
────────────────────────────────────────────────────

RINGKASAN KEWAJIBAN
────────────────────────────────────────────────────
Net PPN (6.050.000 - 1.650.000)    Rp 4.400.000
PPh 21                             Rp   500.000
PPh 23                             Rp   360.000
PPh 25                             Rp 1.000.000
────────────────────────────────────────────────────
Total Kewajiban Pajak              Rp 6.260.000
```

## Skenario 4: Rekonsiliasi Akun Pajak

**Situasi**: Anda ingin memverifikasi saldo akun pajak dengan detail transaksi.

**Langkah-langkah**:

1. Klik menu **Buku Besar** di sidebar
2. Pilih akun pajak (contoh: Hutang PPN - 2.1.03)
3. Pilih periode November 2025
4. Klik **Tampilkan**
5. Review setiap transaksi:
   - **Kredit** = PPN Keluaran dari penjualan
   - **Debit** = Penyetoran PPN ke negara

**Contoh Mutasi Hutang PPN**:

```
Tanggal     Keterangan                    Debit      Kredit     Saldo
01/11/2025  Saldo Awal                                         2.000.000
05/11/2025  Penjualan INV-001                       1.100.000  3.100.000
10/11/2025  Setor PPN Oktober          2.000.000               1.100.000
15/11/2025  Penjualan INV-002                       2.200.000  3.300.000
25/11/2025  Penjualan INV-003                       2.750.000  6.050.000
```

## Skenario 5: Persiapan SPT Masa PPN

**Situasi**: Anda perlu menyiapkan data untuk mengisi SPT Masa PPN di DJP Online.

**Data yang Dibutuhkan**:

| Form SPT | Sumber Data |
|----------|-------------|
| Penyerahan BKP/JKP | Laporan Laba Rugi - Pendapatan |
| PPN Keluaran | Ringkasan PPN - Total PPN Keluaran |
| Pajak Masukan | Ringkasan PPN - Total PPN Masukan |
| Kurang/Lebih Bayar | Ringkasan PPN - Net PPN |

**Langkah-langkah**:

1. Cetak Ringkasan PPN untuk periode masa pajak
2. Catat total PPN Keluaran
3. Catat total PPN Masukan
4. Hitung Net PPN
5. Isi SPT Masa PPN di DJP Online
6. Setor jika kurang bayar
7. Catat penyetoran di aplikasi (lihat [Transaksi PPN](30-transaksi-ppn.md))

## Skenario 6: Persiapan SPT Masa PPh 23

**Situasi**: Anda perlu membuat bukti potong dan melaporkan SPT Masa PPh 23.

**Data yang Dibutuhkan**:

| Keperluan | Sumber Data |
|-----------|-------------|
| Daftar bukti potong | Laporan Pemotongan PPh 23 |
| Total PPh 23 | Ringkasan Pajak |
| Detail per vendor | Mutasi Buku Besar Hutang PPh 23 |

**Langkah-langkah**:

1. Cetak Laporan Pemotongan PPh 23
2. Buat bukti potong untuk setiap vendor di e-Bupot
3. Setor PPh 23 jika belum
4. Laporkan SPT Masa PPh 23
5. Catat penyetoran di aplikasi (lihat [Transaksi PPh](31-transaksi-pph.md))

## Tips

1. **Cetak rutin** - Cetak laporan pajak di awal bulan untuk periode sebelumnya
2. **Arsipkan** - Simpan semua laporan pajak sebagai dokumentasi
3. **Rekonsiliasi** - Cocokkan saldo di aplikasi dengan bukti setor
4. **Deadline reminder** - Catat batas waktu setor dan lapor di kalender

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Saldo PPN tidak cocok | Cek apakah ada transaksi yang belum diposting |
| PPh 23 tidak muncul di laporan | Pastikan menggunakan template dengan PPh |
| Laporan kosong | Periksa filter periode yang dipilih |

## Lihat Juga

- [Kalender Pajak](33-kalender-pajak.md) - Tracking deadline pajak bulanan
- [Transaksi PPN](30-transaksi-ppn.md) - Pencatatan transaksi dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Pemotongan dan penyetoran PPh
- [Laporan Bulanan](21-laporan-bulanan.md) - Laporan keuangan pendukung
