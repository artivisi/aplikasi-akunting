# Laporan Pajak

Aplikasi menyediakan laporan pajak untuk membantu perhitungan dan pelaporan PPN serta PPh 23.

## Jenis Laporan Pajak

| Laporan | Fungsi |
|---------|--------|
| **Ringkasan PPN** | Perhitungan PPN Keluaran vs PPN Masukan |
| **Pemotongan PPh 23** | Tracking PPh 23 yang dipotong dari vendor |
| **Ringkasan Pajak** | Overview semua akun pajak |

## Ringkasan PPN

Laporan ringkasan PPN menampilkan perhitungan PPN bulanan untuk mengetahui status PPN kurang bayar atau lebih bayar.

### Komponen PPN

| Komponen | Keterangan |
|----------|------------|
| **PPN Keluaran** | PPN yang dipungut dari penjualan (Hutang PPN) |
| **PPN Masukan** | PPN yang dibayar pada pembelian (dapat dikreditkan) |
| **Net PPN** | Selisih PPN Keluaran - PPN Masukan |

### Mengakses Ringkasan PPN

1. Klik menu **Laporan** di sidebar
2. Pilih **Ringkasan PPN** di bagian Laporan Pajak
3. Pilih periode:
   - **Tanggal Awal** - Awal periode laporan
   - **Tanggal Akhir** - Akhir periode laporan
4. Klik **Tampilkan**

### Memahami Hasil

| Status | Kondisi | Aksi |
|--------|---------|------|
| **Kurang Bayar** | PPN Keluaran > PPN Masukan | Setor selisih ke negara |
| **Lebih Bayar** | PPN Keluaran < PPN Masukan | Kompensasi atau restitusi |

### Akun yang Terlibat

| Kode Akun | Nama | Saldo Normal |
|-----------|------|--------------|
| 1.1.25 | PPN Masukan | Debit (Aset) |
| 2.1.03 | Hutang PPN | Kredit (Kewajiban) |

## Pemotongan PPh 23

Laporan pemotongan PPh 23 membantu tracking pajak yang dipotong dari pembayaran ke vendor jasa.

### Komponen PPh 23

| Komponen | Keterangan |
|----------|------------|
| **Total Dipotong** | PPh 23 yang dipotong dari pembayaran ke vendor |
| **Total Disetor** | PPh 23 yang sudah disetor ke negara |
| **Saldo** | Selisih yang masih harus disetor |

### Mengakses Laporan PPh 23

1. Klik menu **Laporan** di sidebar
2. Pilih **Pemotongan PPh 23** di bagian Laporan Pajak
3. Pilih periode laporan
4. Klik **Tampilkan**

### Tarif PPh 23

| Jenis Jasa | Tarif |
|------------|-------|
| Jasa teknik, manajemen, konsultan | 2% |
| Sewa peralatan | 2% |
| Jasa lainnya | 2% |

### Kewajiban Penyetoran

| Kewajiban | Batas Waktu |
|-----------|-------------|
| Setor PPh 23 | Tanggal 10 bulan berikutnya |
| Lapor SPT Masa | Tanggal 20 bulan berikutnya |

### Akun yang Terlibat

| Kode Akun | Nama | Saldo Normal |
|-----------|------|--------------|
| 2.1.21 | Hutang PPh 23 | Kredit (Kewajiban) |

## Ringkasan Pajak

Laporan ringkasan pajak menampilkan overview semua akun pajak dalam satu tampilan.

### Mengakses Ringkasan Pajak

1. Klik menu **Laporan** di sidebar
2. Pilih **Ringkasan Pajak**
3. Pilih periode laporan
4. Klik **Tampilkan**

### Akun Pajak

| Kode | Nama | Jenis |
|------|------|-------|
| 1.1.25 | PPN Masukan | Aset (dapat dikreditkan) |
| 2.1.03 | Hutang PPN | Kewajiban (harus disetor) |
| 2.1.20 | Hutang PPh 21 | Kewajiban (gaji karyawan) |
| 2.1.21 | Hutang PPh 23 | Kewajiban (jasa vendor) |
| 2.1.22 | Hutang PPh 4(2) | Kewajiban (sewa, final) |
| 2.1.23 | Hutang PPh 25 | Kewajiban (angsuran bulanan) |
| 2.1.24 | Hutang PPh 29 | Kewajiban (kurang bayar tahunan) |

## Cetak Laporan

Semua laporan pajak dapat dicetak dalam format print-friendly.

### Cara Cetak

1. Tampilkan laporan yang diinginkan
2. Klik tombol **Cetak**
3. Halaman cetak akan terbuka di tab baru
4. Gunakan fitur cetak browser (Ctrl+P)

## Integrasi dengan Template

Laporan pajak membaca data dari jurnal yang dibuat menggunakan template pajak:

| Template | Akun Pajak |
|----------|------------|
| Penjualan dengan PPN | Hutang PPN (Cr) |
| Pembelian dengan PPN | PPN Masukan (Dr) |
| Pembayaran Jasa (PPh 23) | Hutang PPh 23 (Cr) |
| Setor PPN | Hutang PPN (Dr) |
| Setor PPh 23 | Hutang PPh 23 (Dr) |

## Tips Penggunaan

1. Jalankan laporan PPN di akhir bulan untuk persiapan penyetoran
2. Rekonsiliasi saldo hutang pajak dengan bukti potong/faktur
3. Gunakan template pajak yang sesuai untuk memastikan jurnal benar
4. Arsipkan laporan cetak sebagai dokumentasi pajak
