# Tracking Proyek

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin mengupdate progress milestone
- Perlu mencatat pengeluaran terkait proyek
- Ingin memonitor apakah proyek berisiko overrun
- Perlu melihat ringkasan status proyek

## Konsep yang Perlu Dipahami

### Progress Proyek

Progress proyek dihitung dari milestone:
```
Progress = Σ (Bobot Milestone × Completion %)
```

**Contoh**:
| Milestone | Bobot | Completion | Kontribusi |
|-----------|-------|------------|------------|
| Analisis | 20% | 100% | 20% |
| Frontend | 30% | 50% | 15% |
| Backend | 30% | 0% | 0% |
| Testing | 20% | 0% | 0% |
| **Total Progress** | | | **35%** |

### Cost Overrun Detection

Sistem mendeteksi risiko overrun dengan membandingkan:
- **% Budget terpakai** vs **% Progress**

Jika budget terpakai lebih tinggi dari progress, ada risiko overrun.

**Contoh Warning**:
```
Budget:   Rp 35.000.000
Terpakai: Rp 21.000.000 (60%)
Progress: 35%

⚠️ RISIKO OVERRUN: 35% selesai tapi 60% budget terpakai
```

## Skenario 1: Update Progress Milestone

**Situasi**: Tim sudah menyelesaikan tahap Analisis & Desain.

**Langkah-langkah**:

1. Klik menu **Proyek** di sidebar

![Daftar Proyek](screenshots/projects-list.png)

2. Klik proyek yang bersangkutan

![Detail Proyek](screenshots/projects-detail.png)

3. Scroll ke bagian **Milestone**
4. Klik milestone **Analisis & Desain**
5. Update informasi:
   - **Status**: Ubah ke `Completed`
   - **Completion**: `100`
   - Catatan: `Dokumen SRS dan wireframe sudah disetujui klien`
6. Klik **Simpan**

**Hasil**: Progress proyek akan terupdate secara otomatis.

## Skenario 2: Update Progress Parsial

**Situasi**: Milestone Frontend sudah 50% selesai.

**Langkah-langkah**:

1. Buka detail proyek

![Detail Proyek](screenshots/projects-detail.png)

2. Klik milestone **Pengembangan Frontend**
3. Update informasi:
   - **Status**: `In Progress`
   - **Completion**: `50`
4. Klik **Simpan**

**Progress Bar**:
```
Analisis & Desain    [████████████████████] 100%
Pengembangan Frontend [██████████          ] 50%
Pengembangan Backend [                    ] 0%
Testing & Deployment [                    ] 0%

Total Progress: 35%
```

## Skenario 3: Catat Biaya Proyek

**Situasi**: Anda membayar biaya hosting Rp 500.000 untuk proyek ini.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

2. Klik **Transaksi Baru**

![Form Transaksi Baru](screenshots/transactions-form.png)

3. Pilih template **Beban Proyek** atau template yang sesuai
4. Isi form:
   - **Tanggal**: Tanggal pembayaran
   - **Jumlah**: `500000`
   - **Akun Sumber**: Bank BCA
   - **Proyek**: Pilih `PRJ-2025-001 Website E-commerce PT ABC`
   - **Keterangan**: `Biaya hosting server development`
5. Klik **Simpan & Posting**

**Hasil**: Biaya akan masuk ke tracking proyek.

## Skenario 4: Monitor Budget vs Actual

**Situasi**: Anda ingin melihat apakah proyek masih dalam budget.

**Langkah-langkah**:

1. Klik menu **Proyek** di sidebar

![Daftar Proyek](screenshots/projects-list.png)

2. Klik proyek yang bersangkutan

![Detail Proyek](screenshots/projects-detail.png)

3. Lihat bagian **Ringkasan Finansial**:

```
RINGKASAN FINANSIAL

Pendapatan
─────────────────────────────
Nilai Kontrak          Rp 50.000.000
Sudah Diterima         Rp 15.000.000 (DP)
Belum Ditagih          Rp 35.000.000

Biaya
─────────────────────────────
Budget                 Rp 35.000.000
Biaya Aktual           Rp 12.500.000
Sisa Budget            Rp 22.500.000
% Budget Terpakai      36%

Profitabilitas
─────────────────────────────
Target Profit          Rp 15.000.000 (30%)
Estimasi Profit        Rp 22.500.000 (45%)
Progress               35%
```

## Skenario 5: Handle Cost Overrun Warning

**Situasi**: Sistem menampilkan warning risiko overrun.

**Langkah-langkah**:

1. Buka detail proyek

![Detail Proyek](screenshots/projects-detail.png)

2. Lihat warning di bagian atas:
   ```
   ⚠️ RISIKO OVERRUN
   Progress: 35% | Budget Terpakai: 60%
   Estimasi Total Biaya: Rp 60.000.000 (melebihi budget)
   ```

3. Analisis penyebab:
   - Apakah ada biaya tidak terduga?
   - Apakah estimasi budget kurang akurat?
   - Apakah ada inefficiency dalam eksekusi?

4. Tindakan yang bisa dilakukan:
   - Review dan optimasi biaya ke depan
   - Negosiasi addendum dengan klien jika scope bertambah
   - Update budget jika estimasi awal terlalu rendah

## Skenario 6: Catat Penerimaan Pembayaran Proyek

**Situasi**: Klien membayar DP Rp 15.000.000.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar

![Daftar Transaksi](screenshots/transactions-list.png)

2. Klik **Transaksi Baru**

![Form Transaksi Baru](screenshots/transactions-form.png)

3. Pilih template **Terima DP Proyek** atau **Terima Pembayaran Proyek**
4. Isi form:
   - **Tanggal**: Tanggal terima pembayaran
   - **Jumlah**: `15000000`
   - **Akun Sumber**: Bank BCA
   - **Proyek**: Pilih proyek terkait
   - **Keterangan**: `DP 30% Website E-commerce`
5. Klik **Simpan & Posting**

**Hasil**: Pendapatan proyek terupdate.

## Skenario 7: Lihat Semua Transaksi Proyek

**Situasi**: Anda ingin melihat rincian semua transaksi yang terkait proyek.

**Langkah-langkah**:

1. Buka detail proyek

![Detail Proyek](screenshots/projects-detail.png)

2. Scroll ke bagian **Transaksi Terkait**
3. Lihat tabel transaksi:

| Tanggal | Keterangan | Pendapatan | Biaya |
|---------|------------|------------|-------|
| 01/12/2025 | DP 30% | 15.000.000 | |
| 05/12/2025 | Biaya hosting | | 500.000 |
| 10/12/2025 | Bayar freelancer design | | 3.000.000 |
| 15/12/2025 | Beli domain | | 200.000 |

4. Klik **Lihat Semua** untuk detail lengkap di halaman Transaksi dengan filter proyek

## Skenario 8: Ubah Status Proyek

**Situasi**: Proyek sudah selesai dan ingin ditandai completed.

**Langkah-langkah**:

1. Pastikan semua milestone sudah 100%
2. Pastikan semua pembayaran sudah diterima
3. Buka detail proyek

![Detail Proyek](screenshots/projects-detail.png)

4. Klik tombol **Edit**

![Form Edit Proyek](screenshots/projects-form.png)

5. Ubah **Status** ke `Completed`
6. Klik **Simpan**

**Untuk Arsip Proyek**:
- Ubah status ke `Archived`
- Proyek tidak akan muncul di dropdown saat membuat transaksi baru

## Tips

1. **Update rutin** - Update progress minimal seminggu sekali
2. **Catat semua biaya** - Pastikan semua pengeluaran dihubungkan ke proyek
3. **Monitor overrun** - Segera tindak lanjuti jika ada warning
4. **Dokumentasi** - Catat catatan di setiap update milestone

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Progress tidak update | Cek bobot milestone sudah total 100% |
| Biaya tidak muncul | Pastikan transaksi sudah diposting dengan proyek dipilih |
| Warning overrun terus muncul | Update budget jika estimasi awal tidak akurat |

## Lihat Juga

- [Setup Proyek](40-setup-proyek.md) - Buat proyek dan milestone
- [Invoice & Penagihan](42-invoice-penagihan.md) - Tagih termin pembayaran
- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Catat biaya dengan proyek
- [Analisis Profitabilitas](43-analisis-profitabilitas.md) - Laporan profit detail
