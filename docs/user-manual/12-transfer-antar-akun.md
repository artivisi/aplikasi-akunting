# Transfer Antar Akun

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Memindahkan uang dari rekening bank ke rekening lain
- Mengisi kas kecil dari rekening bank
- Menyetor uang tunai ke bank
- Transfer antar rekening bank yang berbeda

## Konsep yang Perlu Dipahami

**Transfer antar akun** adalah pemindahan dana dari satu akun kas/bank ke akun kas/bank lainnya. Transaksi ini:
- Tidak menambah atau mengurangi total aset
- Hanya memindahkan dari satu tempat ke tempat lain
- Kedua sisi (debit dan kredit) adalah akun aset

```
Debit  : Akun tujuan (bertambah)
Kredit : Akun sumber (berkurang)
```

## Skenario 1: Isi Kas Kecil dari Bank

**Situasi**: Menarik uang Rp 2.000.000 dari Bank BCA untuk mengisi kas kecil kantor.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal penarikan
   - **Jumlah**: `2000000`
   - **Dari Akun**: Bank BCA
   - **Ke Akun**: Kas Kecil
   - **Keterangan**: `Isi kas kecil November 2025`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Kas Kecil   Rp 2.000.000
   Kredit : Bank BCA    Rp 2.000.000
   ```
6. Klik **Simpan & Posting**

**Hasil**: Saldo Bank BCA berkurang Rp 2.000.000, Kas Kecil bertambah Rp 2.000.000. Total aset tetap sama.

## Skenario 2: Setor Tunai ke Bank

**Situasi**: Menyetor uang tunai Rp 5.000.000 dari kas ke rekening Bank Mandiri.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal setoran
   - **Jumlah**: `5000000`
   - **Dari Akun**: Kas Kecil
   - **Ke Akun**: Bank Mandiri
   - **Keterangan**: `Setor tunai dari kas`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank Mandiri   Rp 5.000.000
   Kredit : Kas Kecil      Rp 5.000.000
   ```
6. Klik **Simpan & Posting**

## Skenario 3: Transfer Antar Bank

**Situasi**: Transfer Rp 10.000.000 dari Bank BCA ke Bank Mandiri untuk operasional.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal transfer
   - **Jumlah**: `10000000`
   - **Dari Akun**: Bank BCA
   - **Ke Akun**: Bank Mandiri
   - **Keterangan**: `Transfer operasional antar bank`
   - **No. Referensi**: Nomor referensi transfer dari bank
5. Klik **Simpan & Posting**

## Skenario 4: Transfer dengan Biaya Admin

**Situasi**: Transfer Rp 10.000.000 dari Bank BCA ke Bank Mandiri, dikenakan biaya transfer Rp 6.500.

Untuk kasus ini, Anda perlu mencatat dua transaksi:

**Transaksi 1: Transfer Pokok**

1. Buat transaksi **Transfer Kas**
2. Jumlah: `10000000`
3. Dari: Bank BCA, Ke: Bank Mandiri

**Transaksi 2: Biaya Admin**

1. Buat transaksi baru dengan template **Beban Administrasi Bank**
2. Jumlah: `6500`
3. Akun Sumber: Bank BCA
4. Keterangan: `Biaya transfer ke Bank Mandiri`

**Preview Jurnal Gabungan**:
```
Transfer:
Debit  : Bank Mandiri              Rp 10.000.000
Kredit : Bank BCA                  Rp 10.000.000

Biaya Admin:
Debit  : Beban Administrasi Bank   Rp      6.500
Kredit : Bank BCA                  Rp      6.500
```

## Skenario 5: Pemindahan ke Deposito

**Situasi**: Menempatkan Rp 50.000.000 dari rekening giro ke deposito.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal penempatan
   - **Jumlah**: `50000000`
   - **Dari Akun**: Bank BCA (Giro)
   - **Ke Akun**: Deposito BCA
   - **Keterangan**: `Penempatan deposito 3 bulan`
5. Klik **Simpan & Posting**

## Tips

1. **Catat tanggal yang tepat** - Gunakan tanggal aktual transfer, bukan tanggal pencatatan
2. **Simpan referensi** - Catat nomor referensi transfer untuk rekonsiliasi
3. **Biaya admin terpisah** - Catat biaya administrasi bank sebagai transaksi terpisah
4. **Verifikasi saldo** - Cek saldo di aplikasi dengan mutasi bank

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Saldo tidak balance | Periksa apakah ada transaksi yang belum diposting |
| Akun tidak muncul di dropdown | Pastikan akun bertipe Kas/Bank di Bagan Akun |
| Biaya admin tidak tercatat | Buat transaksi terpisah untuk biaya admin |

## Lihat Juga

- [Laporan Harian](20-laporan-harian.md) - Cek saldo dan mutasi akun
- [Setup Awal](50-setup-awal.md) - Menambah akun bank baru
- [Referensi Akun](91-referensi-akun.md) - Daftar akun kas dan bank
