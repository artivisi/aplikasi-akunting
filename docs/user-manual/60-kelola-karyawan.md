# Kelola Karyawan

## Pendahuluan

Menu Karyawan digunakan untuk mengelola data karyawan yang akan diproses penggajiannya. Data karyawan mencakup informasi pribadi, status pajak (PTKP), data kepegawaian, dan informasi BPJS.

## Mengakses Menu Karyawan

1. Klik menu **Karyawan** di sidebar
2. Halaman daftar karyawan akan ditampilkan

## Daftar Karyawan

Halaman daftar menampilkan semua karyawan dengan informasi:
- NIK (Nomor Induk Karyawan)
- Nama lengkap
- Jabatan
- Departemen
- Status (Aktif/Nonaktif)

### Filter dan Pencarian

- **Pencarian**: Ketik nama atau NIK karyawan
- **Filter Status**: Pilih Aktif, Nonaktif, atau Semua
- **Filter Departemen**: Pilih departemen tertentu

## Menambah Karyawan Baru

1. Klik tombol **+ Karyawan Baru**
2. Isi data pada form:

### Data Pribadi
| Field | Keterangan |
|-------|------------|
| NIK | Nomor Induk Karyawan (unik) |
| Nama Lengkap | Nama sesuai KTP |
| Email | Email karyawan |
| Nomor Telepon | Nomor HP aktif |
| Alamat | Alamat lengkap |

### Data Pajak
| Field | Keterangan |
|-------|------------|
| NPWP | Nomor Pokok Wajib Pajak (format: XX.XXX.XXX.X-XXX.XXX) |
| Status PTKP | Penghasilan Tidak Kena Pajak |

#### Status PTKP yang Tersedia

| Kode | Keterangan | PTKP/Tahun |
|------|------------|------------|
| TK/0 | Tidak Kawin, tanpa tanggungan | Rp 54.000.000 |
| TK/1 | Tidak Kawin, 1 tanggungan | Rp 58.500.000 |
| TK/2 | Tidak Kawin, 2 tanggungan | Rp 63.000.000 |
| TK/3 | Tidak Kawin, 3 tanggungan | Rp 67.500.000 |
| K/0 | Kawin, tanpa tanggungan | Rp 58.500.000 |
| K/1 | Kawin, 1 tanggungan | Rp 63.000.000 |
| K/2 | Kawin, 2 tanggungan | Rp 67.500.000 |
| K/3 | Kawin, 3 tanggungan | Rp 72.000.000 |
| K/I/0 | Kawin, penghasilan istri digabung, tanpa tanggungan | Rp 112.500.000 |
| K/I/1 | Kawin, penghasilan istri digabung, 1 tanggungan | Rp 117.000.000 |
| K/I/2 | Kawin, penghasilan istri digabung, 2 tanggungan | Rp 121.500.000 |
| K/I/3 | Kawin, penghasilan istri digabung, 3 tanggungan | Rp 126.000.000 |

### Data Kepegawaian
| Field | Keterangan |
|-------|------------|
| Jabatan | Posisi/title karyawan |
| Departemen | Unit kerja |
| Tanggal Bergabung | Tanggal mulai bekerja |
| Tipe Karyawan | Tetap atau Kontrak |

### Data Bank
| Field | Keterangan |
|-------|------------|
| Nama Bank | Bank untuk transfer gaji |
| Nomor Rekening | Nomor rekening karyawan |
| Nama Pemilik Rekening | Nama sesuai buku rekening |

### Data BPJS
| Field | Keterangan |
|-------|------------|
| No. BPJS Kesehatan | Nomor kepesertaan BPJS Kesehatan |
| No. BPJS Ketenagakerjaan | Nomor kepesertaan BPJS TK |

3. Klik **Simpan** untuk menyimpan data

## Melihat Detail Karyawan

1. Pada daftar karyawan, klik **Lihat** pada baris karyawan
2. Halaman detail menampilkan:
   - Semua informasi karyawan
   - Status aktif/nonaktif
   - Tombol Edit dan Nonaktifkan/Aktifkan

## Mengedit Data Karyawan

1. Buka halaman detail karyawan
2. Klik tombol **Edit**
3. Ubah data yang diperlukan
4. Klik **Simpan**

## Mengaktifkan/Menonaktifkan Karyawan

### Menonaktifkan Karyawan
1. Buka halaman detail karyawan aktif
2. Klik tombol **Nonaktifkan**
3. Konfirmasi aksi

Karyawan nonaktif tidak akan diproses dalam penggajian bulanan.

### Mengaktifkan Karyawan
1. Buka halaman detail karyawan nonaktif
2. Klik tombol **Aktifkan**

## Validasi Data

### Validasi NIK
- NIK harus unik (tidak boleh duplikat)
- Format bebas (alfanumerik)

### Validasi NPWP
- Format: XX.XXX.XXX.X-XXX.XXX
- Harus unik per karyawan
- Sistem akan memvalidasi format saat menyimpan

## Tips Penggunaan

1. **Lengkapi data BPJS** untuk memastikan perhitungan iuran BPJS akurat
2. **Perbarui status PTKP** jika ada perubahan status pernikahan atau tanggungan
3. **Nonaktifkan** karyawan yang sudah resign, jangan menghapus data
4. **Gunakan filter** untuk menemukan karyawan dengan cepat

## Lihat Juga

- [Komponen Gaji](61-komponen-gaji.md) - Mengatur komponen pendapatan dan potongan
