# Online Seller / E-commerce Seed Data

Paket data awal untuk bisnis online seller yang berjualan di marketplace Indonesia.

## Target Pengguna

- Penjual di Tokopedia, Shopee, Lazada, Bukalapak, TikTok Shop
- Dropshipper dan reseller
- Toko online dengan gudang sendiri

## Fitur Utama

### Akun Marketplace
- Saldo terpisah untuk setiap marketplace (Tokopedia, Shopee, Lazada, Bukalapak, TikTok Shop)
- Akun pendapatan per marketplace untuk analisis performa
- Akun biaya admin per marketplace

### Template Transaksi

**Penjualan Marketplace:**
```
Penjualan Tokopedia
  Saldo Tokopedia    DEBIT   grossSales - adminFee
  Biaya Admin        DEBIT   adminFee
  Penjualan          CREDIT  grossSales
```

**Withdraw Saldo:**
```
Withdraw Saldo Tokopedia
  Bank BCA           DEBIT   amount
  Saldo Tokopedia    CREDIT  amount
```

### Persediaan
- Akun Persediaan Barang Dagangan
- Template pembelian barang
- Template penyesuaian stock (stock opname)

### Pajak
- PPh Final UMKM 0.5% (untuk omzet < 4.8M/tahun)
- PPN untuk seller PKP
- Kalender pajak lengkap

## Cara Penggunaan

### 1. Download Package
Download folder `seed-data` dari repository ini.

### 2. Buat ZIP File
```bash
cd industry-seed/online-seller/seed-data
zip -r ../online-seller-seed.zip .
```

### 3. Import via UI
1. Login sebagai admin
2. Buka menu **Settings > Import Data**
3. Upload file `online-seller-seed.zip`
4. Klik **Import** dan konfirmasi

### 4. Verifikasi
- Cek Chart of Accounts - harus ada akun Saldo Tokopedia, Saldo Shopee, dll
- Cek Templates - harus ada template Penjualan Tokopedia, Withdraw Saldo, dll

## Isi Package

| File | Jumlah | Keterangan |
|------|--------|------------|
| Chart of Accounts | 87 akun | Marketplace, inventory, biaya |
| Journal Templates | 37 template | Sales, withdraw, inventory |
| Salary Components | 17 komponen | BPJS, PPh 21 |
| Tax Deadlines | 8 deadline | PPh 21/23, PPN |
| Asset Categories | 3 kategori | Gudang, Komputer, Kendaraan |

## Workflow Harian

### Penjualan
1. Pilih template sesuai marketplace (Penjualan Tokopedia/Shopee/dll)
2. Input `grossSales` (harga jual) dan `adminFee` (potongan marketplace)
3. Sistem otomatis hitung netto ke saldo marketplace

### Withdraw
1. Saat withdraw dari marketplace, pilih template "Withdraw Saldo [Marketplace]"
2. Input jumlah yang ditarik
3. Saldo marketplace berkurang, saldo bank bertambah

### Stock Opname
1. Gunakan "Penyesuaian Persediaan Masuk" jika stock fisik > sistem
2. Gunakan "Penyesuaian Persediaan Keluar" jika stock fisik < sistem

## Batasan

- **Tidak ada auto-import** dari marketplace - semua transaksi diinput manual
- **Tidak ada rekonsiliasi otomatis** - cocokkan manual dengan laporan marketplace
- Untuk high-volume seller (>100 transaksi/hari), tunggu Phase 8 (Marketplace Integration)

## Tips

1. **Rekap harian** - Input penjualan sekali sehari dengan total, bukan per transaksi
2. **Pisahkan fee** - Catat admin fee terpisah untuk analisis profitabilitas per marketplace
3. **Stock opname rutin** - Lakukan minimal 1x/minggu untuk akurasi persediaan
