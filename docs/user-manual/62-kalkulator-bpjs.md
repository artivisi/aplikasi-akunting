# Kalkulator BPJS

## Pendahuluan

Kalkulator BPJS membantu menghitung iuran BPJS (Kesehatan dan Ketenagakerjaan) berdasarkan gaji karyawan. Fitur ini berguna untuk:
- Estimasi biaya BPJS sebelum proses penggajian
- Memahami pembagian iuran perusahaan dan karyawan
- Memverifikasi perhitungan BPJS

## Mengakses Kalkulator BPJS

1. Klik menu **Kalkulator BPJS** di sidebar
2. Halaman kalkulator akan ditampilkan

## Cara Menggunakan

### Input Data

| Field | Keterangan |
|-------|------------|
| Gaji Pokok (Rp) | Gaji pokok bulanan karyawan |
| Kelas Risiko JKK | Kelas risiko untuk BPJS Kecelakaan Kerja |

### Kelas Risiko JKK

| Kelas | Tingkat Risiko | Rate | Contoh Industri |
|-------|----------------|------|-----------------|
| 1 | Sangat Rendah | 0.24% | IT, Jasa, Perbankan |
| 2 | Rendah | 0.54% | Retail, Perdagangan |
| 3 | Sedang | 0.89% | Manufaktur Ringan |
| 4 | Tinggi | 1.27% | Konstruksi |
| 5 | Sangat Tinggi | 1.74% | Pertambangan |

### Langkah Perhitungan

1. Masukkan **Gaji Pokok** karyawan
2. Pilih **Kelas Risiko JKK** sesuai jenis usaha
3. Klik tombol **Hitung**
4. Hasil perhitungan akan ditampilkan

## Memahami Hasil Perhitungan

### Ringkasan

Tiga kartu ringkasan menampilkan:
- **Total Iuran Perusahaan**: Jumlah yang ditanggung perusahaan
- **Total Potongan Karyawan**: Jumlah yang dipotong dari gaji
- **Total Iuran BPJS**: Gabungan perusahaan dan karyawan

### Rincian per Program

| Program | Perusahaan | Karyawan | Batas Upah |
|---------|------------|----------|------------|
| BPJS Kesehatan | 4% | 1% | Rp 12.000.000 |
| BPJS JKK | 0.24%-1.74% | - | Tidak ada |
| BPJS JKM | 0.3% | - | Tidak ada |
| BPJS JHT | 3.7% | 2% | Tidak ada |
| BPJS JP | 2% | 1% | Rp 10.042.300 |

### Batas Upah (Ceiling)

Untuk gaji yang melebihi batas upah:
- **BPJS Kesehatan**: Iuran dihitung maksimal dari Rp 12.000.000
- **BPJS JP**: Iuran dihitung maksimal dari Rp 10.042.300

Sistem akan menampilkan peringatan jika gaji melebihi batas upah.

## Contoh Perhitungan

### Gaji Rp 10.000.000 (Kelas Risiko 1)

| Program | Perusahaan | Karyawan |
|---------|------------|----------|
| BPJS Kesehatan | Rp 400.000 | Rp 100.000 |
| BPJS JKK | Rp 24.000 | - |
| BPJS JKM | Rp 30.000 | - |
| BPJS JHT | Rp 370.000 | Rp 200.000 |
| BPJS JP | Rp 200.846 | Rp 100.423 |
| **Total** | **Rp 1.024.846** | **Rp 400.423** |

### Gaji Rp 20.000.000 (Melebihi Batas)

| Program | Perusahaan | Karyawan | Catatan |
|---------|------------|----------|---------|
| BPJS Kesehatan | Rp 480.000 | Rp 120.000 | Ceiling Rp 12.000.000 |
| BPJS JKK | Rp 48.000 | - | |
| BPJS JKM | Rp 60.000 | - | |
| BPJS JHT | Rp 740.000 | Rp 400.000 | Tanpa ceiling |
| BPJS JP | Rp 200.846 | Rp 100.423 | Ceiling Rp 10.042.300 |

## Keterangan Program BPJS

### BPJS Kesehatan
Jaminan kesehatan untuk karyawan dan keluarga. Iuran 5% dari gaji (4% perusahaan, 1% karyawan).

### BPJS JKK (Jaminan Kecelakaan Kerja)
Perlindungan terhadap risiko kecelakaan kerja. Sepenuhnya ditanggung perusahaan. Rate bervariasi berdasarkan tingkat risiko pekerjaan.

### BPJS JKM (Jaminan Kematian)
Santunan kematian bukan akibat kecelakaan kerja. Sepenuhnya ditanggung perusahaan.

### BPJS JHT (Jaminan Hari Tua)
Tabungan untuk masa pensiun. Iuran 5.7% (3.7% perusahaan, 2% karyawan). Tidak ada batas upah.

### BPJS JP (Jaminan Pensiun)
Pensiun bulanan setelah usia pensiun. Iuran 3% (2% perusahaan, 1% karyawan). Ada batas upah yang disesuaikan setiap tahun.

## Tips Penggunaan

1. **IT Services** termasuk Kelas Risiko 1 (sangat rendah)
2. Batas upah BPJS JP diperbarui setiap tahun oleh pemerintah
3. Gunakan kalkulator ini untuk estimasi sebelum proses penggajian
4. Hasil kalkulator dapat berbeda dari slip gaji jika ada komponen tambahan

## Lihat Juga

- [Kelola Karyawan](60-kelola-karyawan.md) - Data karyawan dan BPJS
- [Komponen Gaji](61-komponen-gaji.md) - Komponen BPJS dalam penggajian
