# Produksi & Bill of Materials (BOM)

Fitur produksi memungkinkan pengelolaan Bill of Materials (BOM) dan pencatatan proses produksi dengan kalkulasi biaya otomatis.

## Konsep BOM

Bill of Materials (BOM) adalah resep produksi yang mendefinisikan:
- **Produk Jadi**: Barang hasil produksi
- **Komponen**: Bahan baku yang diperlukan
- **Kuantitas**: Jumlah masing-masing komponen

### Contoh BOM

```
Produk Jadi: Kue Cokelat (1 loyang)
Komponen:
- Tepung Terigu: 500 gram
- Gula Pasir: 200 gram
- Cokelat Bubuk: 100 gram
- Telur: 3 butir
- Mentega: 150 gram
```

## Membuat BOM

### Langkah-langkah

1. Klik menu **Inventori** > **Bill of Materials**

![Daftar BOM](screenshots/bom-list.png)

2. Klik tombol **Tambah BOM**
3. Isi informasi BOM:
   - **Kode BOM**: Kode unik (contoh: BOM-KUECOKLAT)
   - **Nama BOM**: Nama deskriptif
   - **Produk Jadi**: Pilih produk hasil produksi
   - **Jumlah Output**: Jumlah yang dihasilkan per produksi
   - **Deskripsi**: Keterangan tambahan
4. Tambah komponen:
   - Klik **Tambah Komponen**
   - Pilih produk komponen
   - Isi jumlah yang diperlukan
   - Isi catatan (opsional)

![Form BOM](screenshots/bom-form.png)

5. Klik **Simpan**

### Tips Membuat BOM

- Pastikan semua komponen sudah terdaftar sebagai produk
- Gunakan satuan yang konsisten
- Set jumlah output sesuai batch produksi standar

## Daftar BOM

### Filter dan Pencarian

1. Gunakan **Search** untuk mencari berdasarkan kode atau nama
2. Gunakan filter **Status** untuk melihat BOM aktif/nonaktif

![Filter BOM](screenshots/bom-list.png)

### Detail BOM

Klik kode BOM untuk melihat:
- Informasi dasar BOM
- Produk jadi
- Daftar komponen dengan kuantitas

## Production Order

Production Order adalah perintah produksi yang menggunakan BOM sebagai template.

### Alur Kerja Produksi

```
Buat Order → Mulai Produksi → Selesaikan Produksi
  (Draft)     (In Progress)      (Completed)
```

Status production order:
- **Draft**: Order baru dibuat
- **In Progress**: Produksi sedang berjalan
- **Completed**: Produksi selesai, stok terupdate
- **Cancelled**: Order dibatalkan

### Membuat Production Order

1. Klik menu **Inventori** > **Production Order**

![Daftar Production Order](screenshots/production-list.png)

2. Klik **Buat Order Baru**
3. Isi form:
   - **BOM**: Pilih BOM yang akan digunakan
   - **Kuantitas**: Jumlah batch yang akan diproduksi
   - **Tanggal Order**: Tanggal perintah produksi
   - **Catatan**: Keterangan tambahan

![Form Production Order](screenshots/production-form.png)

4. Klik **Simpan**

Sistem menghasilkan nomor order otomatis (PO-YYYY-NNNN).

### Memulai Produksi

1. Buka halaman detail production order

![Detail Production Order](screenshots/production-form.png)

2. Klik tombol **Mulai Produksi**
3. Konfirmasi

Sistem memvalidasi:
- Stok komponen mencukupi
- BOM masih aktif

Status berubah menjadi **In Progress**.

### Menyelesaikan Produksi

1. Dari halaman detail order yang sedang berjalan

![Production Order In Progress](screenshots/production-form.png)

2. Klik tombol **Selesaikan Produksi**
3. Konfirmasi

Sistem otomatis:
- Mengurangi stok semua komponen (PRODUCTION_OUT)
- Menambah stok produk jadi (PRODUCTION_IN)
- Menghitung total biaya produksi
- Menghitung biaya per unit

### Kalkulasi Biaya

Biaya produksi dihitung berdasarkan:

1. **Total Biaya Komponen**: Jumlah (qty komponen × biaya rata-rata komponen)
2. **Biaya Per Unit**: Total biaya / jumlah produksi

Contoh:
```
Produksi 10 unit Kue Cokelat
Komponen:
- Tepung 5 kg × Rp 20.000 = Rp 100.000
- Gula 2 kg × Rp 15.000 = Rp 30.000
- Cokelat 1 kg × Rp 50.000 = Rp 50.000

Total Biaya: Rp 180.000
Biaya Per Unit: Rp 18.000
```

### Membatalkan Order

Order dapat dibatalkan selama belum selesai:

1. Buka halaman detail order
2. Klik tombol **Batalkan**
3. Konfirmasi

Order yang sudah **Completed** tidak dapat dibatalkan.

## Daftar Production Order

### Filter

![Filter Production Order](screenshots/production-list.png)

1. Filter berdasarkan **Status**
2. Filter berdasarkan **BOM**
3. Filter berdasarkan **Periode**

### Informasi yang Ditampilkan

- Nomor order
- BOM yang digunakan
- Kuantitas
- Tanggal order
- Status
- Biaya total (jika completed)

## Detail Production Order

Halaman detail menampilkan:

### Informasi Order
- Nomor order
- Status
- BOM dan produk jadi
- Kuantitas produksi
- Tanggal order dan catatan

### Daftar Komponen
- Komponen yang diperlukan
- Kuantitas per komponen
- Stok tersedia (jika Draft/In Progress)

### Ringkasan Biaya (Jika Completed)
- Total biaya komponen
- Biaya per unit produk jadi
- Tanggal selesai

## Integrasi Stok

### Saat Produksi Selesai

Transaksi inventori otomatis:

| Transaksi | Tipe | Produk | Qty |
|-----------|------|--------|-----|
| Konsumsi Komponen A | PRODUCTION_OUT | Komponen A | -qty |
| Konsumsi Komponen B | PRODUCTION_OUT | Komponen B | -qty |
| Hasil Produksi | PRODUCTION_IN | Produk Jadi | +qty |

### Kartu Stok

Semua transaksi produksi tercatat di kartu stok masing-masing produk.

## Tips

### Perencanaan Produksi

1. Cek stok komponen sebelum membuat order
2. Buat order sesuai kapasitas produksi
3. Monitor stok minimum komponen

### Akurasi Biaya

1. Pastikan pembelian komponen tercatat
2. Update harga pembelian secara berkala
3. Review biaya produksi untuk penetapan harga jual

### Efisiensi

1. Gunakan BOM standar untuk produk rutin
2. Batch produksi sesuai permintaan
3. Minimalisir sisa komponen

## Lihat Juga

- [Kelola Produk](75-kelola-produk.md) - Setup produk dan komponen
- [Transaksi Inventori](76-transaksi-inventori.md) - Pembelian komponen
- [Kartu Stok](77-kartu-stok.md) - Monitoring stok
