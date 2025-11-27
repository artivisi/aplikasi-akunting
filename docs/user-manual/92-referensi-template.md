# Referensi Template

Daftar template jurnal bawaan yang tersedia dalam aplikasi. Template dikelompokkan berdasarkan kategori.

## Cara Menggunakan Template

1. Klik menu **Transaksi** > **Transaksi Baru**
2. Pilih template yang sesuai
3. Isi form transaksi
4. Periksa Preview Jurnal
5. Simpan Draft atau Posting

## Pendapatan (Revenue)

### Pendapatan Jasa

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Pendapatan Jasa** | Dr. Bank / Cr. Pendapatan Jasa | Terima pembayaran jasa tanpa PPN |
| **Pendapatan Jasa dengan PPN** | Dr. Bank / Cr. Hutang PPN / Cr. Pendapatan Jasa | Terima pembayaran jasa dengan PPN |

**Formula Pendapatan dengan PPN**:
```
Bank (Dr)         = amount
Hutang PPN (Cr)   = amount * 0.11 / 1.11
Pendapatan (Cr)   = amount / 1.11
```

### Penerimaan Lainnya

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Pendapatan Bunga** | Dr. Bank / Cr. Pendapatan Bunga | Terima bunga deposito/tabungan |
| **Terima DP Proyek** | Dr. Bank / Cr. Pendapatan Diterima Dimuka | Terima down payment proyek |
| **Terima Pelunasan Piutang** | Dr. Bank / Cr. Piutang Usaha | Klien membayar piutang |

## Pengeluaran (Expenses)

### Beban Utilitas

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Beban Listrik** | Dr. Beban Listrik / Cr. Bank | Bayar tagihan listrik |
| **Beban Air** | Dr. Beban Air / Cr. Bank | Bayar tagihan air |
| **Beban Telepon** | Dr. Beban Telepon / Cr. Bank | Bayar tagihan telepon |
| **Beban Internet** | Dr. Beban Internet / Cr. Bank | Bayar tagihan internet |

### Beban Operasional

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Beban Gaji** | Dr. Beban Gaji / Cr. Bank | Bayar gaji karyawan |
| **Beban ATK** | Dr. Beban ATK / Cr. Kas | Beli alat tulis kantor |
| **Beban Transportasi** | Dr. Beban Transport / Cr. Kas | Biaya transport |
| **Beban Makan** | Dr. Beban Makan / Cr. Kas | Makan meeting/karyawan |

### Beban dengan PPN

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Pembelian dengan PPN** | Dr. Beban / Dr. PPN Masukan / Cr. Bank | Beli barang/jasa dengan faktur pajak |
| **Beban Software** | Dr. Beban Software / Dr. PPN Masukan / Cr. Bank | Subscription dengan PPN |

**Formula Pembelian dengan PPN**:
```
Beban (Dr)        = amount / 1.11
PPN Masukan (Dr)  = amount * 0.11 / 1.11
Bank (Cr)         = amount
```

### Beban dengan PPh 23

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Pembayaran Jasa (PPh 23)** | Dr. Beban Jasa / Cr. Bank / Cr. Hutang PPh 23 | Bayar vendor jasa dengan potong PPh |

**Formula dengan PPh 23**:
```
Beban Jasa (Dr)    = amount
Bank (Cr)          = amount * 0.98
Hutang PPh 23 (Cr) = amount * 0.02
```

### Beban dengan PPN + PPh 23

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Pembayaran Jasa (PPN + PPh 23)** | Dr. Beban / Dr. PPN Masukan / Cr. Bank / Cr. Hutang PPh 23 | Bayar vendor dengan PPN dan PPh 23 |

**Formula dengan PPN + PPh 23**:
```
DPP = amount / 1.11

Beban Jasa (Dr)    = DPP
PPN Masukan (Dr)   = amount - DPP
Bank (Cr)          = amount - (DPP * 0.02)
Hutang PPh 23 (Cr) = DPP * 0.02
```

### Beban Sewa

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Bayar Sewa** | Dr. Beban Sewa / Cr. Bank | Bayar sewa bulanan |
| **Bayar Sewa Dimuka** | Dr. Sewa Dibayar Dimuka / Cr. Bank | Bayar sewa untuk beberapa bulan |
| **Bayar Sewa (PPh 4(2))** | Dr. Beban Sewa / Cr. Bank / Cr. Hutang PPh 4(2) | Bayar sewa gedung dengan PPh final |

## Transfer

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Transfer Kas** | Dr. Bank Tujuan / Cr. Bank Sumber | Transfer antar rekening |
| **Isi Kas Kecil** | Dr. Kas Kecil / Cr. Bank | Tarik tunai untuk kas kecil |
| **Setor Bank** | Dr. Bank / Cr. Kas | Setor uang tunai ke bank |

## Penyetoran Pajak

| Template | Jurnal | Kapan Digunakan |
|----------|--------|-----------------|
| **Setor PPN** | Dr. Hutang PPN / Cr. Bank | Setor PPN kurang bayar |
| **Setor PPh 21** | Dr. Hutang PPh 21 / Cr. Bank | Setor PPh 21 karyawan |
| **Setor PPh 23** | Dr. Hutang PPh 23 / Cr. Bank | Setor PPh 23 vendor |
| **Setor PPh 25** | Dr. Hutang PPh 25 / Cr. Bank | Setor angsuran PPh |

## Formula yang Sering Digunakan

### Formula Dasar

| Formula | Hasil | Contoh (amount=11.100.000) |
|---------|-------|---------------------------|
| `amount` | Nilai input | 11.100.000 |
| `amount * 0.11` | PPN 11% | 1.221.000 |
| `amount / 1.11` | DPP dari inklusif | 10.000.000 |
| `amount * 0.11 / 1.11` | PPN dari inklusif | 1.100.000 |
| `amount * 0.02` | PPh 23 (2%) | 222.000 |
| `amount * 0.98` | Nett setelah PPh 23 | 10.878.000 |
| `amount * 0.10` | PPh 4(2) (10%) | 1.110.000 |

### Formula Kondisional

```
kondisi ? nilai_jika_benar : nilai_jika_salah
```

**Contoh PPh 23 dengan threshold Rp 2.000.000**:
```
amount > 2000000 ? amount * 0.02 : 0
```

### Variabel Referensi

| Variabel | Arti |
|----------|------|
| `amount` | Jumlah yang diinput user |
| `ppn` | Hasil perhitungan PPN (jika ada di baris sebelumnya) |
| `dpp` | Hasil perhitungan DPP (jika ada di baris sebelumnya) |

## Membuat Template Kustom

Jika template yang Anda butuhkan tidak ada:

1. Klik menu **Template** > **Template Baru**
2. Atau duplikat template yang mirip dan modifikasi
3. Lihat [Kelola Template](51-kelola-template.md) untuk panduan lengkap

## Lihat Juga

- [Kelola Template](51-kelola-template.md) - Buat dan edit template
- [Transaksi PPN](30-transaksi-ppn.md) - Transaksi dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Transaksi dengan PPh
- [Referensi Akun](91-referensi-akun.md) - Daftar akun untuk template
