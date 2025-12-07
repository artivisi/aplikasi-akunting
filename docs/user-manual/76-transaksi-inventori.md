# Transaksi Inventori

Fitur transaksi inventori mencatat pergerakan stok masuk dan keluar.

## Jenis Transaksi

| Tipe | Arah | Deskripsi |
|------|------|-----------|
| PURCHASE | Masuk | Pembelian barang |
| SALE | Keluar | Penjualan barang |
| ADJUSTMENT_IN | Masuk | Penyesuaian tambah stok |
| ADJUSTMENT_OUT | Keluar | Penyesuaian kurang stok |
| PRODUCTION_IN | Masuk | Hasil produksi |
| PRODUCTION_OUT | Keluar | Bahan baku produksi |

## Pembelian (Purchase)

### Mencatat Pembelian Baru

1. Klik menu **Inventori** > **Pembelian**

![Form Pembelian](screenshots/inventory-purchase.png)

2. Isi form:
   - **Produk**: Pilih produk yang dibeli
   - **Tanggal**: Tanggal pembelian
   - **Kuantitas**: Jumlah yang dibeli
   - **Harga Satuan**: Harga per unit
   - **Referensi**: Nomor invoice supplier (opsional)
   - **Catatan**: Keterangan tambahan
3. Klik **Simpan**

Sistem otomatis:
- Menambah stok produk
- Menghitung biaya rata-rata (jika Weighted Average)
- Menambah layer FIFO baru (jika FIFO)

## Penjualan (Sale)

### Mencatat Penjualan

1. Klik menu **Inventori** > **Penjualan**

![Form Penjualan](screenshots/inventory-sale.png)

2. Isi form:
   - **Produk**: Pilih produk yang dijual
   - **Tanggal**: Tanggal penjualan
   - **Kuantitas**: Jumlah yang dijual
   - **Harga Jual**: Harga per unit (untuk margin)
   - **Referensi**: Nomor invoice penjualan (opsional)
3. Klik **Simpan**

Sistem otomatis:
- Mengurangi stok produk
- Menghitung HPP (Harga Pokok Penjualan)
- FIFO: mengonsumsi dari layer terlama
- Weighted Average: menggunakan biaya rata-rata

## Penyesuaian Stok (Adjustment)

### Kapan Menggunakan

- Stock opname (perhitungan fisik)
- Barang rusak/kedaluwarsa
- Selisih stok

### Mencatat Penyesuaian

1. Klik menu **Inventori** > **Penyesuaian**

![Form Penyesuaian](screenshots/inventory-adjustment.png)

2. Isi form:
   - **Produk**: Pilih produk
   - **Tanggal**: Tanggal penyesuaian
   - **Jenis**: Masuk atau Keluar
   - **Kuantitas**: Jumlah penyesuaian
   - **Alasan**: Keterangan penyesuaian
3. Klik **Simpan**

## Daftar Transaksi

### Filter Transaksi

Di halaman **Inventori** > **Transaksi**:

![Daftar Transaksi Inventori](screenshots/inventory-transactions.png)

1. **Produk**: Filter per produk
2. **Tipe**: Filter per jenis transaksi
3. **Tanggal**: Filter periode

### Detail Transaksi

Klik transaksi untuk melihat:
- Informasi lengkap transaksi
- Stok sebelum dan sesudah
- Biaya/HPP yang dihitung

### Analisis Margin (Penjualan)

Untuk transaksi penjualan, detail menampilkan:
- **Harga Jual**: Harga per unit saat dijual
- **Total Pendapatan**: Kuantitas × Harga Jual
- **Margin**: Selisih pendapatan dan HPP
- **Margin %**: Persentase margin terhadap pendapatan

## Integrasi Jurnal

Transaksi inventori dapat menghasilkan jurnal akuntansi otomatis:

### Pembelian
| Akun | Posisi |
|------|--------|
| Persediaan | Debit |
| Kas/Hutang | Kredit |

### Penjualan
| Akun | Posisi |
|------|--------|
| Kas/Piutang | Debit |
| Pendapatan | Kredit |
| HPP | Debit |
| Persediaan | Kredit |

## Lihat Juga

- [Kelola Produk](75-kelola-produk.md) - Setup master produk
- [Kartu Stok](77-kartu-stok.md) - Riwayat per produk
- [Produksi BOM](78-produksi-bom.md) - Transaksi produksi
