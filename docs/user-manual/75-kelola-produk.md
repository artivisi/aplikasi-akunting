# Kelola Produk & Kategori

Fitur produk memungkinkan pencatatan barang dagangan dan bahan baku untuk inventori.

## Master Produk

### Membuat Produk Baru

1. Klik menu **Inventori** > **Produk**

![Daftar Produk](screenshots/products-list.png)

2. Klik tombol **Tambah Produk**
3. Isi form:
   - **Kode**: Kode unik produk (contoh: PRD001)
   - **Nama**: Nama produk
   - **Satuan**: Unit pengukuran (pcs, kg, liter, dll)
   - **Kategori**: Pilih kategori (opsional)
   - **Metode Perhitungan Biaya**: FIFO atau Weighted Average
   - **Stok Minimum**: Batas minimum untuk peringatan
   - **Deskripsi**: Keterangan tambahan

![Form Produk](screenshots/products-form.png)

4. Klik **Simpan**

### Metode Perhitungan Biaya

| Metode | Perhitungan | Cocok Untuk |
|--------|-------------|-------------|
| FIFO | First In First Out - barang masuk pertama keluar pertama | Barang dengan masa simpan terbatas |
| Weighted Average | Biaya rata-rata tertimbang | Barang homogen, umum digunakan |

### Mengedit Produk

1. Di daftar produk, klik nama produk
2. Klik tombol **Edit**
3. Ubah data yang diperlukan

![Form Edit Produk](screenshots/products-form.png)

4. Klik **Simpan**

**Catatan**: Metode perhitungan biaya tidak dapat diubah jika sudah ada transaksi.

### Mengaktifkan/Menonaktifkan Produk

1. Di halaman detail produk, klik **Edit**
2. Centang/hapus centang **Aktif**
3. Klik **Simpan**

Produk nonaktif tidak muncul di dropdown transaksi.

## Kategori Produk

### Membuat Kategori

1. Klik menu **Inventori** > **Kategori**

![Daftar Kategori Produk](screenshots/product-categories-list.png)

2. Klik tombol **Tambah Kategori**
3. Isi form:
   - **Kode**: Kode unik kategori
   - **Nama**: Nama kategori
   - **Parent**: Kategori induk (opsional, untuk hierarki)
4. Klik **Simpan**

### Hierarki Kategori

Kategori dapat disusun bertingkat:

```
Bahan Baku
├── Tepung
├── Gula
└── Mentega
Barang Jadi
├── Kue Kering
└── Kue Basah
```

## Filter dan Pencarian

Di halaman daftar produk:

1. Gunakan **Search** untuk mencari berdasarkan kode atau nama
2. Gunakan dropdown **Kategori** untuk filter berdasarkan kategori
3. Klik kolom header untuk sorting

![Filter dan Pencarian Produk](screenshots/products-list.png)

## Lihat Juga

- [Transaksi Inventori](76-transaksi-inventori.md) - Pembelian, penjualan, penyesuaian stok
- [Kartu Stok](77-kartu-stok.md) - Riwayat transaksi per produk
- [Produksi BOM](78-produksi-bom.md) - Bill of Materials dan produksi
