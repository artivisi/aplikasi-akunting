# Transaksi PPN

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Menjual jasa atau produk yang dikenakan PPN
- Membeli barang atau jasa dari vendor dengan faktur pajak
- Ingin menghitung PPN yang harus disetor
- Perlu mengkreditkan PPN Masukan dengan PPN Keluaran

## Konsep yang Perlu Dipahami

### Apa Itu PPN?

**Pajak Pertambahan Nilai (PPN)** adalah pajak yang dikenakan atas penyerahan barang/jasa kena pajak. Tarif PPN di Indonesia adalah **11%**.

### PPN Keluaran vs PPN Masukan

| Jenis | Kapan Terjadi | Akun | Saldo Normal |
|-------|---------------|------|--------------|
| **PPN Keluaran** | Saat menjual barang/jasa | Hutang PPN (2.1.03) | Kredit |
| **PPN Masukan** | Saat membeli barang/jasa | PPN Masukan (1.1.25) | Debit |

### Perhitungan PPN

**Dari harga eksklusif (DPP)**:
```
DPP = Rp 10.000.000
PPN = DPP × 11% = Rp 1.100.000
Total = Rp 11.100.000
```

**Dari harga inklusif**:
```
Total = Rp 11.100.000
DPP = Total / 1.11 = Rp 10.000.000
PPN = Total - DPP = Rp 1.100.000
```

### Net PPN Bulanan

Di akhir bulan, hitung:
```
Net PPN = PPN Keluaran - PPN Masukan
```

| Hasil | Arti | Aksi |
|-------|------|------|
| Positif | Kurang Bayar | Setor ke negara |
| Negatif | Lebih Bayar | Kompensasi atau restitusi |

## Skenario 1: Penjualan Jasa dengan PPN

**Situasi**: Anda menagih klien Rp 11.100.000 untuk jasa konsultasi (sudah termasuk PPN).

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

2. Klik tombol **Transaksi Baru**
3. Pilih template **Pendapatan Jasa dengan PPN**

![Form Transaksi](screenshots/transactions-form.png)

4. Isi form:
   - **Tanggal**: Tanggal invoice/pembayaran
   - **Jumlah**: `11100000` (nilai inklusif PPN)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Invoice INV-2025-001 - Jasa Konsultasi IT`
   - **No. Referensi**: Nomor faktur pajak
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA           Rp 11.100.000
   Kredit : Hutang PPN         Rp  1.100.000 (PPN Keluaran)
   Kredit : Pendapatan Jasa    Rp 10.000.000 (DPP)
   ```
6. Klik **Simpan & Posting**

![Detail Transaksi](screenshots/transactions-detail.png)

**Hasil**: Hutang PPN (PPN Keluaran) bertambah Rp 1.100.000.

## Skenario 2: Pembelian dengan Faktur Pajak (PPN Masukan)

**Situasi**: Anda membeli peralatan komputer Rp 5.550.000 (sudah termasuk PPN) dan mendapat faktur pajak.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

2. Klik tombol **Transaksi Baru**
3. Pilih template **Pembelian dengan PPN**

![Form Transaksi](screenshots/transactions-form.png)

4. Isi form:
   - **Tanggal**: Tanggal faktur
   - **Jumlah**: `5550000` (nilai inklusif PPN)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pembelian komputer - Toko Elektronik ABC`
   - **No. Referensi**: Nomor faktur pajak dari vendor
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Peralatan Kantor   Rp 5.000.000 (DPP)
   Debit  : PPN Masukan        Rp   550.000 (dapat dikreditkan)
   Kredit : Bank BCA           Rp 5.550.000
   ```
6. Klik **Simpan & Posting**

![Detail Transaksi](screenshots/transactions-detail.png)

**Hasil**: PPN Masukan bertambah Rp 550.000 (dapat dikreditkan dengan PPN Keluaran).

## Skenario 3: Pembelian Beban Operasional dengan PPN

**Situasi**: Anda membayar subscription software Rp 1.110.000 (sudah termasuk PPN).

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

2. Klik tombol **Transaksi Baru**
3. Pilih template **Beban dengan PPN**

![Form Transaksi](screenshots/transactions-form.png)

4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `1110000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Subscription Adobe Creative Cloud`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Software     Rp 1.000.000 (DPP)
   Debit  : PPN Masukan        Rp   110.000
   Kredit : Bank BCA           Rp 1.110.000
   ```
6. Klik **Simpan & Posting**

![Detail Transaksi](screenshots/transactions-detail.png)

## Skenario 4: Cek Status PPN Bulanan

**Situasi**: Akhir bulan, Anda ingin mengetahui apakah PPN kurang bayar atau lebih bayar.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Ringkasan PPN**
3. Pilih periode:
   - **Tanggal Awal**: 1 November 2025
   - **Tanggal Akhir**: 30 November 2025
4. Klik **Tampilkan**

![Ringkasan PPN](screenshots/reports-ppn-summary.png)

5. Review hasil:

```
Ringkasan PPN November 2025

PPN Keluaran (Hutang PPN)     Rp 5.500.000
PPN Masukan                   Rp 2.200.000
─────────────────────────────────────────
Net PPN (Kurang Bayar)        Rp 3.300.000

Status: KURANG BAYAR
Batas Setor: 15 Desember 2025
```

## Skenario 5: Setor PPN Kurang Bayar

**Situasi**: Dari laporan di atas, Anda perlu menyetor PPN Rp 3.300.000.

**Langkah-langkah**:

1. Bayar PPN melalui bank atau e-billing
2. Setelah mendapat bukti bayar:
3. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

4. Klik tombol **Transaksi Baru**
5. Pilih template **Setor PPN**

![Form Transaksi](screenshots/transactions-form.png)

6. Isi form:
   - **Tanggal**: Tanggal setoran
   - **Jumlah**: `3300000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Setor PPN Masa November 2025`
   - **No. Referensi**: Nomor NTPN
7. Periksa **Preview Jurnal**:
   ```
   Debit  : Hutang PPN         Rp 3.300.000
   Kredit : Bank BCA           Rp 3.300.000
   ```
8. Klik **Simpan & Posting**

![Detail Transaksi](screenshots/transactions-detail.png)

**Hasil**: Saldo Hutang PPN berkurang Rp 3.300.000.

## Skenario 6: Penjualan dengan Harga Eksklusif PPN

**Situasi**: Anda menagih DPP Rp 10.000.000, PPN ditagih terpisah.

**Langkah-langkah**:

1. Hitung total: DPP + PPN = 10.000.000 + 1.100.000 = 11.100.000
2. Gunakan template **Pendapatan Jasa dengan PPN**
3. Masukkan jumlah inklusif: `11100000`
4. Sistem akan menghitung:
   - DPP: Rp 10.000.000
   - PPN: Rp 1.100.000

> Catatan: Template menggunakan formula `amount / 1.11` untuk menghitung DPP dari nilai inklusif. Jika nilai DPP sudah diketahui, kalikan dengan 1.11 untuk mendapat nilai inklusif.

## Skenario 7: Bayar PPN yang Tertunda dari Periode Lalu

**Situasi**: Anda sudah mencatat pendapatan dengan PPN tahun lalu, tapi PPN-nya belum dibayar. Sekarang baru mau bayar.

**Pertanyaan Umum**: 
- "Apakah ini dicatat sebagai beban pajak di tahun ini?"
- "Pakai akun Beban Pajak Lainnya?"

**Jawaban**: **TIDAK!** Ini bukan beban baru, hanya membayar hutang lama.

### Penjelasan

**Saat pendapatan dicatat tahun lalu:**
```
Debit  : Piutang/Bank         Rp 11.100.000
Kredit : Pendapatan           Rp 10.000.000
Kredit : Hutang PPN           Rp  1.100.000  ← SUDAH tercatat sebagai hutang
```

Hutang PPN **sudah ada di neraca** sejak tahun lalu. Ketika Anda bayar sekarang, Anda cuma **melunasi hutang**, bukan menciptakan beban baru.

### Dampak ke Laporan Keuangan

**Laporan tahun lalu (sudah benar):**
- Pendapatan: Rp 10.000.000 ✓
- Hutang PPN di Neraca: Rp 1.100.000 ✓

**Laporan tahun ini (saat bayar):**
- Tidak ada pendapatan baru ✓
- Tidak ada beban baru ✓
- Hanya: Kas berkurang & Hutang berkurang ✓

**Ini transaksi NERACA saja** (Aset vs Liabilitas), tidak mempengaruhi Laba Rugi.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Setor PPN**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran (tahun ini)
   - **Jumlah**: `1100000` (sesuai hutang)
   - **Akun Sumber**: Bank CIMB (atau bank yang digunakan)
   - **Keterangan**: `Setor PPN periode November 2024 (terlambat dibayar)`
   - **No. Referensi**: Nomor NTPN atau bukti setor
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Hutang PPN         Rp 1.100.000  ← Hapus hutang lama
   Kredit : Bank CIMB          Rp 1.100.000  ← Kas keluar
   ```
6. Klik **Simpan & Posting**

**Hasil**: 
- Hutang PPN di neraca berkurang ke nol ✓
- Tidak ada beban di laba rugi tahun ini ✓
- Pembayaran tercatat dengan tanggal aktual ✓

> **Penting**: Jangan gunakan akun "Beban Pajak Lainnya" karena itu akan membuat beban baru di tahun ini dan membuat laporan keuangan salah. PPN yang sudah dicatat sebagai hutang tahun lalu harus dibayar menggunakan template "Setor PPN" yang mendebit Hutang PPN, bukan akun beban.

## Tips

1. **Simpan faktur pajak** - Faktur pajak adalah bukti untuk mengkreditkan PPN Masukan
2. **Rekonsiliasi bulanan** - Cek saldo akun PPN sebelum menyetor
3. **Catat nomor faktur** - Selalu catat nomor faktur pajak di referensi
4. **Batas waktu** - Setor PPN paling lambat tanggal 15 bulan berikutnya
5. **Hutang PPN lama** - Bayar menggunakan template "Setor PPN", bukan "Beban Pajak"

## Kewajiban Pelaporan

| Kewajiban | Batas Waktu |
|-----------|-------------|
| Setor PPN | Tanggal 15 bulan berikutnya |
| Lapor SPT Masa PPN | Tanggal 20 bulan berikutnya |

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| PPN tidak terhitung | Pastikan menggunakan template dengan PPN |
| Nilai PPN salah | Cek formula di template (harus `amount * 0.11 / 1.11` untuk inklusif) |
| PPN Masukan tidak muncul | Pastikan transaksi sudah diposting |

## Lihat Juga

- [Transaksi PPh](31-transaksi-pph.md) - Pemotongan PPh dari vendor
- [Laporan Pajak](32-laporan-pajak.md) - Cetak laporan untuk SPT
- [Kelola Template](51-kelola-template.md) - Buat template PPN kustom
