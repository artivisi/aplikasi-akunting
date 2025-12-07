# Kartu Stok

Kartu stok menampilkan saldo dan riwayat transaksi untuk setiap produk.

## Daftar Stok

### Melihat Saldo Stok

1. Klik menu **Inventori** > **Stok Barang**

![Daftar Saldo Stok](screenshots/stock-list.png)

2. Tampilan menampilkan:
   - **Total Nilai Persediaan**: Nilai rupiah seluruh stok
   - **Produk dengan Stok**: Jumlah produk yang memiliki saldo
   - **Stok Minimum**: Produk di bawah batas minimum

### Filter dan Pencarian

1. Gunakan **Search** untuk mencari produk
2. Gunakan dropdown **Kategori** untuk filter

![Filter Stok Barang](screenshots/stock-list.png)

### Peringatan Stok Minimum

Produk di bawah stok minimum ditampilkan:
- Panel peringatan merah di atas
- Daftar produk dengan stok aktual vs minimum

## Kartu Stok Per Produk

### Mengakses Kartu Stok

1. Di daftar stok, klik nama produk
2. Atau dari detail produk, klik **Lihat Kartu Stok**

### Informasi yang Ditampilkan

#### Header
- Kode dan nama produk
- Satuan
- Metode perhitungan biaya
- Kategori
- Stok minimum

#### Saldo Saat Ini
- **Stok Saat Ini**: Jumlah dalam satuan
- **Biaya Rata-rata**: Harga per unit (Weighted Average)
- **Total Nilai**: Nilai rupiah stok

#### Layer FIFO (Jika Metode FIFO)

Tabel layer menampilkan:
- Tanggal layer
- Qty awal dan sisa
- Harga satuan
- Nilai sisa
- Status (Tersedia/Habis)

#### Riwayat Transaksi

Tabel transaksi menampilkan:
- Tanggal
- Tipe transaksi
- Kuantitas (+/-)
- Harga satuan
- Total nilai
- Saldo setelah transaksi

## Laporan Stok

### Laporan Saldo Stok

1. Klik menu **Inventori** > **Laporan** > **Saldo Stok**

![Laporan Saldo Stok](screenshots/inventory-stock-balance.png)

2. Pilih tanggal
3. Lihat atau export ke PDF/Excel

### Laporan Pergerakan Stok

1. Klik menu **Inventori** > **Laporan** > **Pergerakan Stok**

![Laporan Pergerakan Stok](screenshots/inventory-stock-movement.png)

2. Pilih periode (dari - sampai)
3. Filter produk (opsional)
4. Lihat atau export

## Tips

### Monitoring Stok Minimum

- Set stok minimum berdasarkan lead time supplier
- Review stok minimum secara berkala
- Gunakan peringatan untuk reorder

### Rekonsiliasi Stok

1. Lakukan stock opname berkala
2. Bandingkan dengan kartu stok
3. Catat penyesuaian jika ada selisih
4. Dokumentasikan alasan penyesuaian

## Lihat Juga

- [Kelola Produk](75-kelola-produk.md) - Setup produk
- [Transaksi Inventori](76-transaksi-inventori.md) - Catat pergerakan stok
- [Produksi BOM](78-produksi-bom.md) - Produksi dan konsumsi bahan
