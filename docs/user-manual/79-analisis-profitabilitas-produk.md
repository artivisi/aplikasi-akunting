# Analisis Profitabilitas Produk

Laporan profitabilitas produk membantu menganalisis margin dan keuntungan per produk berdasarkan transaksi penjualan.

## Mengakses Laporan

1. Klik menu **Inventori** > **Laporan** > **Profitabilitas Produk**

![Menu Laporan Inventori](screenshots/inventory-reports.png)

2. Atau dari halaman **Laporan Persediaan**, klik card **Profitabilitas Produk**

## Filter Laporan

![Laporan Profitabilitas Produk](screenshots/inventory-reports-profitability.png)

### Periode

1. Pilih **Tanggal Mulai** dan **Tanggal Akhir**
2. Default: bulan berjalan

### Kategori

- Pilih kategori untuk melihat profitabilitas produk dalam kategori tertentu
- Pilih "Semua Kategori" untuk semua produk

### Produk

- Pilih produk spesifik untuk analisis detail
- Pilih "Semua Produk" untuk perbandingan antar produk

## Informasi Laporan

![Detail Laporan Profitabilitas](screenshots/inventory-reports-profitability.png)

### Summary

Bagian atas menampilkan ringkasan:

- **Total Pendapatan**: Total nilai penjualan dalam periode
- **Total HPP**: Total Harga Pokok Penjualan
- **Total Margin**: Selisih pendapatan dan HPP
- **Margin %**: Persentase margin keseluruhan

### Tabel Detail

Setiap baris menampilkan:

| Kolom | Deskripsi |
|-------|-----------|
| Kode | Kode produk |
| Produk | Nama produk |
| Kategori | Kategori produk |
| Qty Terjual | Jumlah unit yang terjual |
| Pendapatan | Total nilai penjualan |
| HPP | Harga Pokok Penjualan |
| Margin | Keuntungan kotor (Pendapatan - HPP) |
| Margin % | Persentase margin |
| # Transaksi | Jumlah transaksi penjualan |

### Sorting

Laporan diurutkan berdasarkan margin tertinggi, sehingga produk paling profitable tampil di atas.

## Interpretasi Hasil

### Margin Positif (Hijau)

Produk menghasilkan keuntungan:
- Margin tinggi (>30%): Produk sangat profitable
- Margin sedang (15-30%): Produk profitable standar
- Margin rendah (<15%): Perlu evaluasi pricing

### Margin Negatif (Merah)

Produk menyebabkan kerugian:
- Evaluasi harga jual
- Review biaya pembelian
- Pertimbangkan untuk dihentikan

## Cetak Laporan

1. Klik tombol **Cetak** di kanan atas
2. Laporan akan terbuka di tab baru dalam format print-friendly
3. Gunakan fungsi print browser (Ctrl+P / Cmd+P)

## Tips Analisis

### Identifikasi Top Performers

1. Lihat produk dengan margin tertinggi
2. Fokus promosi pada produk ini
3. Pertahankan ketersediaan stok

### Analisis Low Performers

1. Produk dengan margin < 10% perlu perhatian
2. Evaluasi apakah volume tinggi mengompensasi margin rendah
3. Pertimbangkan penyesuaian harga

### Perbandingan Periode

1. Jalankan laporan untuk periode berbeda
2. Bandingkan margin antar periode
3. Identifikasi tren profitabilitas

## Lihat Juga

- [Transaksi Inventori](76-transaksi-inventori.md) - Pencatatan penjualan
- [Kartu Stok](77-kartu-stok.md) - Monitoring stok per produk
- [Kelola Produk](75-kelola-produk.md) - Setup produk
