# Proses Penggajian (Payroll)

Fitur payroll memungkinkan perhitungan gaji bulanan untuk semua karyawan aktif, lengkap dengan BPJS dan PPh 21.

## Alur Kerja Payroll

```
Buat Payroll → Calculate → Approve → Post ke Jurnal
```

Status payroll:
- **Draft**: Payroll baru dibuat, belum dikalkulasi
- **Calculated**: Perhitungan selesai, siap review
- **Approved**: Disetujui, siap posting
- **Posted**: Jurnal sudah dibuat
- **Cancelled**: Dibatalkan

## Membuat Payroll Baru

1. Klik menu **Payroll** > **Daftar Payroll**

![Daftar Payroll](screenshots/payroll-list.png)

2. Klik tombol **Buat Payroll Baru**

![Form Payroll Baru](screenshots/payroll-form.png)

3. Isi form:
   - **Periode**: Format YYYY-MM (contoh: 2025-01)
   - **Gaji Pokok**: Jumlah gaji pokok untuk semua karyawan
   - **Kelas Risiko JKK**: Kelas risiko kecelakaan kerja (1-5)
4. Klik **Simpan & Hitung**

Sistem akan otomatis:
- Mengambil semua karyawan dengan status ACTIVE
- Menghitung BPJS (Kesehatan, JKK, JKM, JHT, JP)
- Menghitung PPh 21 berdasarkan status PTKP masing-masing karyawan
- Menghitung total potongan dan gaji netto

## Komponen Perhitungan

### BPJS yang Dihitung

| Komponen | Ditanggung Perusahaan | Ditanggung Karyawan |
|----------|----------------------|---------------------|
| Kesehatan | 4% | 1% |
| JKK | 0.24% - 1.74% (sesuai kelas) | - |
| JKM | 0.3% | - |
| JHT | 3.7% | 2% |
| JP | 2% | 1% |

### PPh 21

PPh 21 dihitung dengan tarif progresif:
- 5% untuk PKP 0 - 60 juta
- 15% untuk PKP 60 juta - 250 juta
- 25% untuk PKP 250 juta - 500 juta
- 30% untuk PKP 500 juta - 5 miliar
- 35% untuk PKP di atas 5 miliar

PTKP 2024:
- TK/0: Rp 54.000.000
- K/0: Rp 58.500.000
- K/1: Rp 63.000.000
- K/2: Rp 67.500.000
- K/3: Rp 72.000.000

## Detail Payroll

Halaman detail menampilkan:

![Detail Payroll](screenshots/payroll-detail.png)

### Summary

- **Jumlah Karyawan**: Total karyawan yang diproses
- **Total Bruto**: Total gaji kotor
- **Total Potongan**: BPJS karyawan + PPh 21
- **Total Neto**: Gaji bersih yang dibayarkan

### Rincian per Karyawan

Tabel yang menampilkan:
- NIK dan Nama
- Gaji Bruto
- BPJS Karyawan
- PPh 21
- Total Potongan
- Gaji Neto

## Approve Payroll

Setelah review perhitungan:

1. Buka halaman detail payroll

![Review Detail Payroll](screenshots/payroll-detail.png)

2. Verifikasi jumlah dan perhitungan
3. Klik tombol **Approve**

Setelah di-approve, payroll siap untuk di-posting ke jurnal.

## Posting ke Jurnal

Posting akan membuat jurnal akuntansi:

1. Dari halaman detail payroll yang sudah di-approve

![Posting Payroll](screenshots/payroll-detail.png)

2. Klik tombol **Post ke Jurnal**
3. Konfirmasi posting

### Jurnal yang Dibuat

| Akun | Posisi | Jumlah |
|------|--------|--------|
| Beban Gaji | Debit | Total bruto |
| Beban BPJS | Debit | BPJS perusahaan |
| Hutang Gaji | Kredit | Total neto |
| Hutang BPJS | Kredit | Total BPJS (perusahaan + karyawan) |
| Hutang PPh 21 | Kredit | Total PPh 21 |

Setelah posting:
- Status berubah menjadi **Posted**
- Link ke transaksi ditampilkan
- Payroll tidak dapat dibatalkan

## Membatalkan Payroll

Payroll dapat dibatalkan selama belum di-posting:

1. Buka halaman detail payroll
2. Klik tombol **Batalkan**
3. Konfirmasi pembatalan

Pembatalan tidak menghapus data, hanya mengubah status menjadi Cancelled.

## Menghapus Payroll

Hanya payroll dengan status **Draft** yang dapat dihapus:

1. Buka halaman detail payroll
2. Klik tombol **Hapus**
3. Konfirmasi penghapusan

## Filter dan Pencarian

Di halaman daftar payroll:

![Filter Payroll](screenshots/payroll-list.png)

1. Gunakan dropdown **Status** untuk filter
2. Pilih status: All, Draft, Calculated, Approved, Posted, Cancelled
3. Klik periode untuk melihat detail

## Laporan Payroll

Sistem menyediakan beberapa laporan untuk setiap payroll yang sudah dikalkulasi.

### Mengakses Laporan

1. Buka halaman detail payroll

![Akses Laporan Payroll](screenshots/payroll-detail.png)

2. Klik tombol **Export** (dropdown)
3. Pilih jenis laporan dan format (PDF/Excel)

### Jenis Laporan

#### Rekap Gaji

Ringkasan gaji seluruh karyawan dalam satu periode:
- NIK dan Nama
- Gaji Bruto
- BPJS Karyawan
- PPh 21
- Total Potongan
- Gaji Neto

#### Laporan PPh 21

Laporan untuk pelaporan SPT Masa PPh 21:
- Nama dan NPWP karyawan
- Penghasilan Bruto
- PPh 21 yang dipotong
- Status PTKP

#### Laporan BPJS

Laporan iuran BPJS untuk penyetoran:
- BPJS Kesehatan (perusahaan dan karyawan)
- BPJS Ketenagakerjaan (JKK, JKM, JHT, JP)
- Total iuran perusahaan dan karyawan

### Slip Gaji

Slip gaji individual untuk setiap karyawan:

1. Di tabel rincian karyawan, klik ikon download pada kolom **Slip Gaji**
2. PDF akan terunduh dengan rincian:
   - Informasi karyawan
   - Pendapatan
   - Potongan (BPJS, PPh 21)
   - Gaji bersih

## Bukti Potong 1721-A1

Bukti Potong PPh 21 (1721-A1) adalah dokumen tahunan yang wajib diberikan kepada karyawan.

### Mengakses Bukti Potong

1. Klik menu **Payroll** > **Bukti Potong 1721-A1**
2. Pilih tahun pajak
3. Klik **Download PDF** untuk karyawan yang diinginkan

### Isi Bukti Potong

Dokumen berisi:
- Identitas pemotong pajak (perusahaan)
- Identitas penerima penghasilan (karyawan)
- Penghasilan bruto setahun
- Biaya jabatan (5%, maks Rp 6.000.000)
- Iuran pensiun/JHT/JP
- Penghasilan neto
- PTKP
- PKP
- PPh 21 terutang dan yang telah dipotong

### Waktu Pemberian

- Bukti potong wajib diberikan paling lambat 1 bulan setelah tahun pajak berakhir
- Karyawan menggunakan dokumen ini untuk SPT Tahunan PPh Orang Pribadi

## Tips

### Sebelum Memulai Payroll

- Pastikan data karyawan sudah lengkap (NPWP, status PTKP)
- Pastikan karyawan yang akan digaji berstatus **Active**
- Tentukan kelas risiko JKK sesuai jenis usaha

### Kelas Risiko JKK

| Kelas | Tarif | Contoh Jenis Usaha |
|-------|-------|-------------------|
| 1 | 0.24% | Jasa IT, Konsultan |
| 2 | 0.54% | Retail, Kuliner |
| 3 | 0.89% | Manufaktur ringan |
| 4 | 1.27% | Konstruksi |
| 5 | 1.74% | Pertambangan |

### Pembayaran Gaji

Setelah posting:

1. Transfer gaji ke rekening karyawan sesuai Total Neto
2. Catat jurnal **Transfer Bank** untuk setiap pembayaran
3. Setor BPJS dan PPh 21 sesuai jadwal

## Lihat Juga

- [Kelola Karyawan](60-kelola-karyawan.md) - Setup data karyawan
- [Komponen Gaji](61-komponen-gaji.md) - Pengaturan komponen
- [Kalkulator BPJS](62-kalkulator-bpjs.md) - Simulasi perhitungan BPJS
- [Kalkulator PPh 21](63-kalkulator-pph21.md) - Simulasi perhitungan PPh 21
- [Kalender Pajak](33-kalender-pajak.md) - Jadwal setor dan lapor PPh 21
