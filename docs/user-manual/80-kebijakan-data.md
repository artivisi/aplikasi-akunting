# Kebijakan Penyimpanan Data

## Ringkasan

Aplikasi ini menyimpan data keuangan Anda sesuai dengan ketentuan perpajakan Indonesia. Dokumen ini menjelaskan berapa lama data disimpan dan bagaimana data dikelola.

## Dasar Hukum

Berdasarkan **UU No. 28 Tahun 2007 tentang Ketentuan Umum Perpajakan (KUP)**, catatan keuangan wajib disimpan selama **10 tahun** sejak akhir tahun pajak.

## Periode Penyimpanan

### Data Keuangan (10 Tahun)

Data berikut disimpan selama 10 tahun dan tidak dapat dihapus:

| Jenis Data | Keterangan |
|------------|------------|
| Transaksi | Semua transaksi pendapatan dan pengeluaran |
| Jurnal | Semua entri jurnal (termasuk yang di-void) |
| Invoice | Faktur dan bukti penagihan |
| Laporan Pajak | PPN, PPh 21, PPh 23, dll |
| Bukti Potong | 1721-A1 dan bukti potong lainnya |
| Data Payroll | Gaji, potongan, dan tunjangan karyawan |
| Dokumen Pendukung | Scan nota, kwitansi, dan bukti transaksi |

### Data Klien dan Proyek (10 Tahun)

| Jenis Data | Keterangan |
|------------|------------|
| Data Klien | Nama, alamat, NPWP klien |
| Data Proyek | Informasi proyek dan milestone |
| Kontrak | Dokumen kontrak yang diunggah |

### Data Karyawan (10 Tahun Setelah Berhenti)

| Jenis Data | Keterangan |
|------------|------------|
| Data Pribadi | NIK, NPWP, alamat |
| Riwayat Gaji | Slip gaji dan komponen |
| BPJS | Nomor dan kontribusi BPJS |

### Data Operasional (Dapat Dihapus)

| Jenis Data | Periode | Keterangan |
|------------|---------|------------|
| Log Aktivitas | 2 tahun | Riwayat login dan perubahan |
| Draft Telegram | 90 hari | Struk yang belum diproses |

## Penghapusan Data

### Soft Delete (Default)

Ketika Anda menghapus data melalui aplikasi:
- Data tidak benar-benar dihapus dari database
- Data ditandai sebagai "dihapus" dan disembunyikan dari tampilan
- Data masih dapat dipulihkan jika diperlukan
- Ini memastikan jejak audit tetap lengkap

### Hard Delete

Penghapusan permanen TIDAK tersedia melalui aplikasi untuk data keuangan. Ini sesuai dengan ketentuan perpajakan yang mewajibkan penyimpanan 10 tahun.

## Backup Data

### Jadwal Backup

| Tipe Backup | Retensi |
|-------------|---------|
| Harian | 7 hari terakhir |
| Mingguan | 4 minggu terakhir |
| Bulanan | 12 bulan terakhir |
| Tahunan | 10 tahun |

### Lokasi Backup

Backup disimpan di lokasi terpisah dari server utama untuk keamanan data.

## Ekspor Data

Anda dapat mengekspor data Anda kapan saja:

### Per Laporan
1. Buka halaman laporan yang diinginkan
2. Klik tombol **Export PDF** atau **Export Excel**

### Ekspor Lengkap
Untuk mengekspor seluruh data perusahaan:

![Halaman Pengaturan Privacy](screenshots/settings-privacy.png)

1. Buka **Pengaturan** > **Ekspor Data**
2. Pilih periode yang diinginkan
3. Klik **Ekspor Semua**
4. Unduh file ZIP yang berisi semua data

## Hak Subjek Data (GDPR/UU PDP)

Aplikasi ini mendukung hak-hak subjek data sesuai dengan GDPR (General Data Protection Regulation) dan UU PDP (Perlindungan Data Pribadi) No. 27/2022.

### Hak Akses (Right to Access - Art. 15 GDPR)

Anda berhak mendapatkan salinan data pribadi Anda. Administrator dapat memproses permintaan ini melalui:

![Halaman Hak Subjek Data](screenshots/settings-data-subjects.png)

1. Buka **Pengaturan > Hak Subjek Data**
2. Cari karyawan berdasarkan nama atau NIK
3. Klik **Lihat Detail** untuk melihat ringkasan data
4. Klik **Ekspor Data (DSAR)** untuk mengunduh data lengkap

Data yang diekspor mencakup:
- Informasi identitas (nama, email, telepon)
- Informasi kepegawaian (jabatan, departemen, tanggal masuk)
- Data sensitif dalam format tersamarkan (NIK, NPWP, nomor rekening)

### Hak Penghapusan (Right to Erasure - Art. 17 GDPR)

Anda berhak meminta penghapusan data pribadi Anda. Karena kewajiban retensi perpajakan, penghapusan dilakukan melalui **anonimisasi**:

![Halaman Hak Subjek Data](screenshots/settings-data-subjects.png)

1. Buka **Pengaturan > Hak Subjek Data**
2. Pilih karyawan yang mengajukan permintaan
3. Klik **Anonimisasi Data**
4. Isi alasan anonimisasi (misalnya: nomor surat permintaan)
5. Centang konfirmasi bahwa tindakan tidak dapat dibatalkan
6. Klik **Anonimisasi Data**

Setelah dianonimisasi:
- Nama diganti menjadi `ANONYMIZED-XXXXXXXX`
- Email, telepon, alamat dihapus
- NIK KTP, NPWP, nomor rekening dihapus
- Data BPJS dihapus
- Catatan keuangan tetap dipertahankan untuk kepatuhan pajak

### Status Retensi Data

Sistem menampilkan status retensi untuk setiap karyawan:
- **Periode retensi**: 10 tahun sesuai UU KUP Art. 28
- **Tanggal berakhir**: Dihitung dari tanggal resign + 10 tahun
- **Dapat dihapus**: Status apakah data sudah melewati periode retensi

### Hak Koreksi (Right to Rectification - Art. 16 GDPR)

- Data profil dapat diubah melalui menu Profil
- Data keuangan yang sudah diposting tidak dapat diubah (harus void dan buat baru)

## Catatan untuk Administrator

Untuk memproses permintaan hak subjek data:

1. Verifikasi identitas pemohon sebelum memproses
2. Catat nomor referensi permintaan tertulis
3. Simpan bukti permintaan selama periode retensi
4. Semua tindakan tercatat dalam log audit

## Keamanan Data

### Enkripsi Data Tersimpan (At Rest)

Data sensitif dienkripsi menggunakan AES-256-GCM:

| Jenis Data | Status Enkripsi |
|------------|-----------------|
| NIK KTP | Terenkripsi |
| NPWP | Terenkripsi |
| Nomor Rekening Bank | Terenkripsi |
| Nomor BPJS Kesehatan | Terenkripsi |
| Nomor BPJS Ketenagakerjaan | Terenkripsi |
| Dokumen yang diunggah | Terenkripsi |
| Backup | Terenkripsi (AES-256 + GPG) |

### Enkripsi Data Transit

- Koneksi ke aplikasi menggunakan HTTPS (TLS 1.2/1.3)
- Koneksi database menggunakan SSL
- Transfer backup ke cloud menggunakan HTTPS

### Akses Terbatas

- Setiap pengguna hanya dapat mengakses data sesuai role-nya
- Aktivitas pengguna dicatat dalam log audit
- Data sensitif ditampilkan dalam format tersamarkan di UI

## Kontak

Untuk pertanyaan tentang data Anda, hubungi administrator sistem.

## Pembaruan Kebijakan

| Versi | Tanggal | Perubahan |
|-------|---------|-----------|
| 1.0 | November 2024 | Kebijakan awal |
| 2.0 | Desember 2024 | Penambahan hak subjek data (GDPR/UU PDP), enkripsi data |
