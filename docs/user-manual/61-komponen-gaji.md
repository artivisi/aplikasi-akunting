# Komponen Gaji

## Pendahuluan

Menu Komponen Gaji digunakan untuk mengelola jenis-jenis pendapatan dan potongan yang akan digunakan dalam penggajian. Sistem sudah menyediakan komponen standar Indonesia (BPJS, tunjangan umum) yang dapat digunakan langsung atau disesuaikan.

## Mengakses Menu Komponen Gaji

1. Klik menu **Komponen Gaji** di sidebar
2. Halaman daftar komponen gaji akan ditampilkan

## Daftar Komponen Gaji

Halaman daftar menampilkan semua komponen dengan informasi:
- Kode komponen
- Nama komponen
- Tipe (Pendapatan/Potongan/Kontribusi Perusahaan)
- Nilai Default (nominal atau persentase)
- Status (Aktif/Nonaktif)

### Filter dan Pencarian

- **Pencarian**: Ketik kode atau nama komponen
- **Filter Tipe**: Pilih Pendapatan, Potongan, atau Kontribusi Perusahaan

## Tipe Komponen

| Tipe | Keterangan | Contoh |
|------|------------|--------|
| **Pendapatan** | Menambah penghasilan karyawan | Gaji Pokok, Tunjangan |
| **Potongan** | Mengurangi penghasilan karyawan | BPJS Karyawan, PPh 21 |
| **Kontribusi Perusahaan** | Ditanggung perusahaan, tidak mengurangi gaji | BPJS Perusahaan |

## Komponen Bawaan Sistem

Sistem menyediakan 17 komponen standar Indonesia:

### Pendapatan (EARNING)
| Kode | Nama | Keterangan |
|------|------|------------|
| GAPOK | Gaji Pokok | Gaji pokok bulanan |
| TJ-TRANS | Tunjangan Transportasi | Default Rp 500.000 |
| TJ-MAKAN | Tunjangan Makan | Default Rp 500.000 |
| TJ-POSISI | Tunjangan Jabatan | Berdasarkan posisi |
| LEMBUR | Uang Lembur | Sesuai perhitungan |
| BONUS | Bonus | Bonus kinerja |
| THR | Tunjangan Hari Raya | Sesuai ketentuan |

### Kontribusi Perusahaan (COMPANY_CONTRIBUTION)
| Kode | Nama | Rate |
|------|------|------|
| BPJS-KES-P | BPJS Kesehatan (Perusahaan) | 4% |
| BPJS-JHT-P | BPJS JHT (Perusahaan) | 3.7% |
| BPJS-JKK | BPJS JKK | 0.24% |
| BPJS-JKM | BPJS JKM | 0.3% |
| BPJS-JP-P | BPJS JP (Perusahaan) | 2% |

### Potongan (DEDUCTION)
| Kode | Nama | Rate |
|------|------|------|
| BPJS-KES-K | BPJS Kesehatan (Karyawan) | 1% |
| BPJS-JHT-K | BPJS JHT (Karyawan) | 2% |
| BPJS-JP-K | BPJS JP (Karyawan) | 1% |
| PPH21 | PPh Pasal 21 | Dihitung otomatis |
| POT-LAIN | Potongan Lain-lain | Pinjaman, dll |

## Menambah Komponen Baru

1. Klik tombol **+ Komponen Baru**
2. Isi data pada form:

| Field | Keterangan |
|-------|------------|
| Kode | Kode unik komponen (huruf kapital) |
| Nama | Nama komponen |
| Deskripsi | Penjelasan singkat |
| Tipe Komponen | Pendapatan/Potongan/Kontribusi Perusahaan |
| Jenis Nilai | Nominal Tetap atau Persentase |
| Nilai Default | Nominal (Rp) atau Persentase (%) |
| Urutan Tampil | Angka kecil ditampilkan lebih dulu |
| Diperhitungkan Pajak | Centang jika masuk perhitungan PPh 21 |
| Kategori BPJS | Pilih jika terkait BPJS |

3. Klik **Simpan**

### Jenis Nilai

**Nominal Tetap**
- Nilai dalam Rupiah
- Contoh: Tunjangan Makan Rp 500.000

**Persentase**
- Nilai dalam persen dari basis perhitungan
- Masukkan angka langsung (misal: 4 untuk 4%)
- Contoh: BPJS Kesehatan 4% dari gaji pokok

## Melihat Detail Komponen

1. Pada daftar, klik **Lihat** pada baris komponen
2. Halaman detail menampilkan:
   - Semua informasi komponen
   - Status aktif/nonaktif
   - Informasi audit (dibuat/diperbarui)

## Mengedit Komponen

1. Buka halaman detail komponen
2. Klik tombol **Edit**
3. Ubah data yang diperlukan
4. Klik **Simpan**

**Catatan:** Komponen sistem (bawaan) tidak dapat diedit atau dinonaktifkan.

## Mengaktifkan/Menonaktifkan Komponen

### Menonaktifkan Komponen
1. Buka halaman detail komponen aktif
2. Klik tombol **Nonaktifkan**
3. Konfirmasi aksi

Komponen nonaktif tidak akan muncul dalam pilihan saat mengatur gaji karyawan.

### Mengaktifkan Komponen
1. Buka halaman detail komponen nonaktif
2. Klik tombol **Aktifkan**

## Kategori BPJS

Kategori BPJS menandai komponen yang terkait dengan program BPJS:

| Kategori | Program |
|----------|---------|
| KESEHATAN | BPJS Kesehatan |
| JHT | Jaminan Hari Tua |
| JKK | Jaminan Kecelakaan Kerja |
| JKM | Jaminan Kematian |
| JP | Jaminan Pensiun |

Kategori ini digunakan untuk:
- Mengelompokkan komponen terkait
- Validasi rate sesuai ketentuan
- Laporan iuran BPJS

## Pengaturan Pajak

Centang **Diperhitungkan dalam pajak (PPh 21)** untuk komponen yang:
- Termasuk penghasilan bruto karyawan
- Akan dihitung dalam perhitungan PPh 21

Komponen yang umumnya kena pajak:
- Gaji Pokok
- Tunjangan Tetap (Transportasi, Makan, Jabatan)
- Bonus, THR

Komponen yang tidak kena pajak:
- Iuran BPJS (porsi karyawan maupun perusahaan)

## Tips Penggunaan

1. **Gunakan komponen bawaan** untuk standar Indonesia
2. **Buat komponen baru** untuk kebutuhan khusus perusahaan
3. **Perhatikan urutan tampil** untuk slip gaji yang rapi
4. **Jangan hapus komponen** yang sudah digunakan, cukup nonaktifkan

## Lihat Juga

- [Kelola Karyawan](60-kelola-karyawan.md) - Mengatur data karyawan
