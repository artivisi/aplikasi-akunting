# Kelola Klien

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin menambah klien baru
- Perlu mengupdate informasi klien
- Ingin melihat riwayat proyek dan invoice per klien
- Perlu data klien untuk keperluan pajak (NPWP, alamat)

## Konsep yang Perlu Dipahami

### Data Klien

Data klien digunakan untuk:
- **Proyek** - Menghubungkan proyek ke klien
- **Invoice** - Header invoice dengan data klien lengkap
- **Laporan** - Analisis profitabilitas per klien
- **Pajak** - Bukti potong PPh dengan NPWP klien

### Informasi Klien

| Field | Kegunaan |
|-------|----------|
| **Nama** | Nama lengkap perusahaan/individu |
| **Alias** | Nama pendek untuk pencarian |
| **NPWP** | Untuk keperluan faktur pajak dan bukti potong |
| **Alamat** | Alamat lengkap untuk invoice |
| **Email** | Kontak untuk pengiriman invoice |
| **Telepon** | Kontak untuk follow up |
| **PIC** | Person in charge / contact person |

## Skenario 1: Tambah Klien Baru (Perusahaan)

**Situasi**: Anda mendapat klien baru PT ABC.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Klik tombol **Klien Baru**
3. Isi informasi:
   - **Nama**: `PT ABC Indonesia`
   - **Alias**: `PT ABC`
   - **Tipe**: `Perusahaan`
   - **NPWP**: `01.234.567.8-901.000`
   - **Alamat**: `Jl. Sudirman No. 123, Jakarta Pusat 10220`
   - **Email**: `finance@ptabc.co.id`
   - **Telepon**: `021-12345678`
   - **PIC**: `Budi Santoso`
   - **Jabatan PIC**: `Finance Manager`
4. Klik **Simpan**

**Hasil**: Klien siap digunakan untuk proyek dan invoice.

## Skenario 2: Tambah Klien Individu

**Situasi**: Anda punya klien perorangan untuk jasa konsultasi.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Klik tombol **Klien Baru**
3. Isi informasi:
   - **Nama**: `Andi Wijaya`
   - **Tipe**: `Individu`
   - **NPWP**: `12.345.678.9-012.000` (jika ada)
   - **Alamat**: `Jl. Melati No. 45, Bandung`
   - **Email**: `andi.wijaya@email.com`
   - **Telepon**: `0812-3456-7890`
4. Klik **Simpan**

## Skenario 3: Edit Informasi Klien

**Situasi**: Klien pindah alamat atau ganti kontak.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Cari klien yang ingin diedit
3. Klik nama klien untuk buka detail
4. Klik tombol **Edit**
5. Ubah informasi yang diperlukan
6. Klik **Simpan Perubahan**

**Catatan**: Perubahan alamat tidak mempengaruhi invoice yang sudah dibuat.

## Skenario 4: Lihat Riwayat Klien

**Situasi**: Anda ingin melihat semua proyek dan invoice untuk klien tertentu.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Klik nama klien
3. Di halaman detail, lihat:

**Tab Proyek**:
```
PROYEK PT ABC

Kode         Nama                    Status      Nilai
PRJ-001      Website E-commerce      Completed   50.000.000
PRJ-003      Mobile App             Active       80.000.000
PRJ-005      Maintenance 2025       Active       24.000.000
─────────────────────────────────────────────────────────
Total                                           154.000.000
```

**Tab Invoice**:
```
INVOICE PT ABC

No. Invoice   Tanggal      Jumlah        Status
INV-001       15/01/2025   22.200.000    Paid
INV-003       15/03/2025   33.300.000    Paid
INV-007       15/11/2025   11.100.000    Sent
─────────────────────────────────────────────────────────
Total                      66.600.000
Outstanding                11.100.000
```

**Tab Transaksi**:
```
TRANSAKSI TERKAIT PT ABC

Tanggal      Keterangan              Pendapatan    Biaya
01/01/2025   DP Proyek Website       15.000.000
15/01/2025   Progress Payment        20.000.000
...
```

## Skenario 5: Cari Klien

**Situasi**: Anda ingin mencari klien tertentu dari daftar yang panjang.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Gunakan kolom **Pencarian**
3. Ketik:
   - Nama klien
   - Alias
   - NPWP
   - Nama PIC
4. Hasil pencarian akan ditampilkan

## Skenario 6: Filter Klien berdasarkan Tipe

**Situasi**: Anda ingin melihat hanya klien perusahaan.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Di filter **Tipe**, pilih:
   - **Semua** - Tampilkan semua klien
   - **Perusahaan** - Hanya klien perusahaan
   - **Individu** - Hanya klien perorangan
3. Daftar akan terfilter sesuai pilihan

## Skenario 7: Nonaktifkan Klien

**Situasi**: Klien sudah tidak aktif dan tidak perlu muncul di dropdown.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Cari dan klik klien
3. Klik **Edit**
4. Nonaktifkan toggle **Status Aktif**
5. Klik **Simpan**

**Efek**:
- Klien tidak muncul di dropdown saat buat proyek/invoice baru
- Data historis (proyek, invoice, transaksi) tetap ada
- Klien masih muncul di laporan

## Skenario 8: Export Data Klien

**Situasi**: Anda perlu data klien untuk keperluan lain.

**Langkah-langkah**:

1. Klik menu **Klien** di sidebar
2. Klik tombol **Export**
3. Pilih format:
   - **Excel** - Untuk analisis atau mail merge
   - **CSV** - Untuk import ke sistem lain
4. File akan terunduh

**Data yang Diekspor**:
- Nama, alias, tipe
- NPWP, alamat
- Email, telepon
- PIC, jabatan

## Skenario 9: Analisis Klien Terbaik

**Situasi**: Anda ingin mengetahui klien mana yang paling menguntungkan.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Profitabilitas per Klien**
3. Pilih periode
4. Klik **Tampilkan**
5. Lihat ranking klien berdasarkan profit

Lihat [Analisis Profitabilitas](43-analisis-profitabilitas.md) untuk detail.

## Tips

1. **Lengkapi data** - Isi NPWP dan alamat lengkap untuk keperluan pajak
2. **Update rutin** - Pastikan data kontak selalu up-to-date
3. **Gunakan alias** - Alias memudahkan pencarian
4. **Catat PIC** - Penting untuk follow up invoice

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Klien tidak muncul di dropdown | Pastikan klien aktif |
| NPWP format salah | Gunakan format: XX.XXX.XXX.X-XXX.XXX |
| Tidak bisa hapus klien | Klien dengan proyek/invoice tidak bisa dihapus, nonaktifkan saja |

## Lihat Juga

- [Setup Proyek](40-setup-proyek.md) - Menghubungkan proyek ke klien
- [Invoice & Penagihan](42-invoice-penagihan.md) - Buat invoice untuk klien
- [Analisis Profitabilitas](43-analisis-profitabilitas.md) - Profit per klien
