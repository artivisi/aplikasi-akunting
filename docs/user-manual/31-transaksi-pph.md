# Transaksi PPh

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Membayar vendor jasa dan perlu memotong PPh 23
- Membayar gaji karyawan dengan pemotongan PPh 21
- Perlu menyetor PPh yang sudah dipotong ke negara
- Ingin tracking pemotongan PPh untuk bukti potong

## Konsep yang Perlu Dipahami

### Jenis PPh yang Umum

| Jenis | Objek | Tarif | Pemotong |
|-------|-------|-------|----------|
| **PPh 21** | Gaji, honor, upah | Progresif | Pemberi kerja |
| **PPh 23** | Jasa teknik, konsultan, sewa | 2% | Pengguna jasa |
| **PPh 4(2)** | Sewa tanah/bangunan | 10% | Penyewa |
| **PPh 25** | Angsuran bulanan | Berdasar SPT | Wajib pajak |

### PPh 23 - Yang Paling Umum untuk UKM

**Kapan Anda Memotong PPh 23**:
- Membayar jasa konsultan, desainer, programmer (outsource)
- Membayar jasa teknik, manajemen
- Membayar sewa peralatan (bukan tanah/bangunan)

**Tarif**: 2% dari nilai bruto (sebelum PPN)

**Contoh**:
```
Nilai jasa bruto: Rp 5.000.000
PPh 23 (2%):      Rp   100.000
Dibayar ke vendor: Rp 4.900.000
```

### Kewajiban Pemotong

Sebagai pemotong PPh 23, Anda wajib:
1. Memotong PPh saat membayar vendor
2. Menyetor PPh ke negara (max tanggal 10 bulan berikutnya)
3. Memberikan bukti potong ke vendor
4. Melaporkan SPT Masa PPh 23 (max tanggal 20 bulan berikutnya)

## Skenario 1: Bayar Vendor Jasa dengan PPh 23

**Situasi**: Anda membayar vendor desain grafis Rp 5.000.000 untuk desain logo.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pembayaran Jasa (PPh 23)**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `5000000` (nilai bruto sebelum potong)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Jasa desain logo - CV Kreatif Design`
   - **No. Referensi**: Nomor invoice vendor
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Jasa Profesional   Rp 5.000.000
   Kredit : Bank BCA                 Rp 4.900.000 (yang dibayar)
   Kredit : Hutang PPh 23            Rp   100.000 (yang dipotong)
   ```
6. Klik **Simpan & Posting**

**Hasil**:
- Anda membayar vendor Rp 4.900.000
- PPh 23 Rp 100.000 masuk ke Hutang PPh 23 (harus disetor ke negara)

## Skenario 2: Bayar Vendor dengan PPN dan PPh 23

**Situasi**: Vendor menagih Rp 5.550.000 (DPP 5.000.000 + PPN 550.000), Anda perlu memotong PPh 23.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pembayaran Jasa (PPN + PPh 23)**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `5550000` (nilai inklusif PPN)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Jasa konsultasi - PT Konsultan ABC`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Jasa Konsultan     Rp 5.000.000 (DPP)
   Debit  : PPN Masukan              Rp   550.000
   Kredit : Bank BCA                 Rp 5.450.000 (yang dibayar)
   Kredit : Hutang PPh 23            Rp   100.000 (2% x DPP)
   ```
6. Klik **Simpan & Posting**

**Catatan**: PPh 23 dihitung dari DPP (sebelum PPN), bukan dari nilai total.

## Skenario 3: Bayar Vendor Tanpa PPh (Nilai Kecil)

**Situasi**: Beberapa template memiliki threshold untuk PPh 23. Jika di bawah Rp 2.000.000, PPh tidak dipotong.

**Contoh dengan threshold**:

```
Jumlah: Rp 1.500.000 (di bawah threshold Rp 2.000.000)

Preview Jurnal:
Debit  : Beban Jasa              Rp 1.500.000
Kredit : Bank BCA                Rp 1.500.000

(PPh 23 = 0 karena di bawah threshold)
```

**Langkah-langkah**:
1. Gunakan template yang sudah dikonfigurasi dengan threshold
2. Masukkan jumlah
3. Sistem akan otomatis menghitung apakah PPh dipotong atau tidak

## Skenario 4: Cek Status PPh yang Dipotong

**Situasi**: Anda ingin mengetahui total PPh 23 yang sudah dipotong dan harus disetor.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Pemotongan PPh 23**
3. Pilih periode:
   - **Tanggal Awal**: 1 November 2025
   - **Tanggal Akhir**: 30 November 2025
4. Klik **Tampilkan**
5. Review hasil:

```
Pemotongan PPh 23 November 2025

Total Dipotong          Rp 500.000
Total Disetor           Rp 300.000
─────────────────────────────────────────
Saldo Hutang PPh 23     Rp 200.000

Batas Setor: 10 Desember 2025
Batas Lapor: 20 Desember 2025
```

## Skenario 5: Setor PPh 23 ke Negara

**Situasi**: Anda perlu menyetor PPh 23 yang sudah dipotong.

**Langkah-langkah**:

1. Buat billing pajak melalui e-billing DJP
2. Bayar melalui bank atau ATM
3. Setelah mendapat NTPN:
4. Klik menu **Transaksi** di sidebar
5. Klik tombol **Transaksi Baru**
6. Pilih template **Setor PPh 23**
7. Isi form:
   - **Tanggal**: Tanggal setoran
   - **Jumlah**: `200000` (sesuai saldo hutang)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Setor PPh 23 Masa November 2025`
   - **No. Referensi**: Nomor NTPN
8. Periksa **Preview Jurnal**:
   ```
   Debit  : Hutang PPh 23      Rp 200.000
   Kredit : Bank BCA           Rp 200.000
   ```
9. Klik **Simpan & Posting**

**Hasil**: Saldo Hutang PPh 23 berkurang.

## Skenario 6: Bayar Sewa Gedung (PPh 4 ayat 2)

**Situasi**: Anda membayar sewa gedung/tanah Rp 24.000.000. PPh final 10%.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Bayar Sewa Gedung (PPh 4(2))**
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `24000000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Sewa gedung kantor 2025`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Beban Sewa              Rp 24.000.000
   Kredit : Bank BCA                Rp 21.600.000 (yang dibayar)
   Kredit : Hutang PPh 4(2)         Rp  2.400.000 (10%)
   ```
6. Klik **Simpan & Posting**

## Skenario 7: Bayar Gaji dengan PPh 21

**Situasi**: Membayar gaji karyawan Rp 10.000.000, PPh 21 terutang Rp 500.000.

**Catatan**: Perhitungan PPh 21 kompleks (PTKP, tarif progresif). Nilai PPh 21 biasanya sudah dihitung terlebih dahulu.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Bayar Gaji (PPh 21)**
4. Isi form sesuai hasil perhitungan PPh 21
5. Atau buat template kustom dengan nilai PPh yang sudah dihitung

## Tips

1. **DPP untuk PPh 23** - Hitung PPh dari nilai sebelum PPN
2. **Threshold** - Beberapa jasa memiliki batas minimal untuk pemotongan
3. **Bukti potong** - Wajib memberikan bukti potong ke vendor
4. **Tepat waktu** - Setor max tanggal 10, lapor max tanggal 20 bulan berikutnya

## Kewajiban Pelaporan

| Jenis PPh | Batas Setor | Batas Lapor |
|-----------|-------------|-------------|
| PPh 21 | Tanggal 10 bulan berikutnya | Tanggal 20 bulan berikutnya |
| PPh 23 | Tanggal 10 bulan berikutnya | Tanggal 20 bulan berikutnya |
| PPh 4(2) | Tanggal 10 bulan berikutnya | Tanggal 20 bulan berikutnya |

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| PPh tidak terpotong | Pastikan menggunakan template dengan PPh |
| Tarif PPh salah | Edit formula di template |
| Threshold tidak berfungsi | Cek formula kondisional di template |

## Lihat Juga

- [Transaksi PPN](30-transaksi-ppn.md) - Transaksi dengan PPN
- [Laporan Pajak](32-laporan-pajak.md) - Cetak laporan untuk SPT
- [Kelola Template](51-kelola-template.md) - Buat template PPh kustom
