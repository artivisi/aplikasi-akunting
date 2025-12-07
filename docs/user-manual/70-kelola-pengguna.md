# Kelola Pengguna

Fitur kelola pengguna memungkinkan administrator untuk mengatur akun pengguna dan hak akses dalam aplikasi.

## Konsep Role-Based Access Control (RBAC)

Aplikasi menggunakan sistem kontrol akses berbasis role. Setiap pengguna dapat memiliki satu atau lebih role, dan setiap role memiliki kumpulan hak akses (permission) tertentu.

### Daftar Role

| Role | Deskripsi | Hak Akses |
|------|-----------|-----------|
| **Administrator** | Akses penuh termasuk manajemen pengguna | Semua fitur + kelola pengguna |
| **Pemilik** | Akses semua fitur bisnis | Semua fitur kecuali manajemen pengguna |
| **Akuntan** | Operasi akuntansi dan laporan | Transaksi, jurnal, laporan, pajak, payroll |
| **Staf** | Akses terbatas untuk operasi harian | Lihat transaksi, lihat laporan, buat transaksi |
| **Auditor** | Akses baca-saja untuk audit | Lihat semua data dan laporan |
| **Karyawan** | Akses slip gaji dan profil sendiri | Lihat slip gaji dan profil sendiri |

### Sistem Permission Aditif

Jika pengguna memiliki beberapa role, hak aksesnya adalah gabungan dari semua role yang dimiliki. Contoh:
- Pengguna dengan role Akuntan + Auditor akan memiliki hak akses dari kedua role tersebut

## Daftar Pengguna

Halaman daftar pengguna menampilkan semua pengguna yang terdaftar dalam sistem. Anda dapat:

- Melihat username, nama lengkap, dan email pengguna
- Melihat role yang dimiliki setiap pengguna dengan badge berwarna
- Melihat status aktif/nonaktif pengguna
- Mencari pengguna berdasarkan username, nama, atau email
- Menambah pengguna baru

![Daftar Pengguna](screenshots/users-list.png)

### Filter dan Pencarian

Gunakan kolom pencarian untuk mencari pengguna berdasarkan:
- Username
- Nama lengkap
- Email

## Menambah Pengguna Baru

1. Klik tombol **Pengguna Baru** di halaman daftar
2. Anda akan melihat form pengguna baru:

![Form Pengguna Baru](screenshots/users-form.png)

3. Isi informasi pengguna:
   - **Username**: Username unik untuk login (wajib)
   - **Password**: Password sesuai kebijakan keamanan (wajib untuk pengguna baru)
   - **Nama Lengkap**: Nama lengkap pengguna (wajib)
   - **Email**: Alamat email (opsional)
4. Pilih minimal satu role dari daftar yang tersedia
5. Klik **Simpan** untuk membuat pengguna

### Persyaratan Password

Password harus memenuhi persyaratan berikut:

| Persyaratan | Keterangan |
|-------------|------------|
| Panjang minimal | 12 karakter |
| Huruf besar | Minimal 1 karakter (A-Z) |
| Huruf kecil | Minimal 1 karakter (a-z) |
| Angka | Minimal 1 digit (0-9) |
| Karakter spesial | Minimal 1 karakter (!@#$%^&*(),.?":{}|<>) |

Contoh password yang valid: `SecurePass123!`

## Mengedit Pengguna

1. Klik link **Detail** pada baris pengguna yang ingin diedit
2. Anda akan melihat halaman detail pengguna:

![Detail Pengguna](screenshots/users-detail.png)

3. Di halaman detail, klik tombol **Edit**
4. Ubah informasi yang diperlukan
5. Klik **Simpan** untuk menyimpan perubahan

**Catatan**: Password tidak ditampilkan saat mengedit. Untuk mengubah password, gunakan fitur Ubah Password.

## Mengubah Password

1. Buka halaman detail pengguna
2. Klik tombol **Ubah Password**
3. Masukkan password baru sesuai persyaratan keamanan
4. Konfirmasi password baru
5. Klik **Simpan Password**

Lihat bagian **Persyaratan Password** untuk ketentuan password yang valid.

## Menonaktifkan/Mengaktifkan Pengguna

1. Buka halaman detail pengguna
2. Klik tombol **Nonaktifkan** (untuk pengguna aktif) atau **Aktifkan** (untuk pengguna nonaktif)

Pengguna yang dinonaktifkan tidak dapat login ke aplikasi tetapi datanya tetap tersimpan.

## Menghapus Pengguna

1. Buka halaman detail pengguna
2. Klik tombol **Hapus**
3. Konfirmasi penghapusan

**Catatan**: Anda tidak dapat menghapus akun sendiri.

## Penguncian Akun (Account Lockout)

Untuk melindungi dari serangan brute force, sistem akan mengunci akun setelah beberapa kali gagal login:

| Parameter | Nilai |
|-----------|-------|
| Percobaan gagal maksimal | 5 kali |
| Durasi penguncian | 30 menit |

Setelah dikunci:
- Pengguna tidak dapat login meskipun password benar
- Penguncian otomatis berakhir setelah 30 menit
- Administrator dapat melihat status penguncian di log audit

## Log Audit Keamanan

Semua aktivitas keamanan dicatat dalam log audit yang dapat diakses di **Pengaturan > Log Audit**. Aktivitas yang dicatat:

| Event | Keterangan |
|-------|------------|
| LOGIN_SUCCESS | Login berhasil |
| LOGIN_FAILURE | Login gagal (password salah) |
| ACCOUNT_LOCKED | Akun dikunci karena terlalu banyak percobaan gagal |
| PASSWORD_CHANGE | Perubahan password |
| USER_CREATE | Pengguna baru dibuat |
| USER_UPDATE | Data pengguna diubah |
| USER_DELETE | Pengguna dihapus |
| ROLE_CHANGE | Perubahan role pengguna |

## Best Practices

1. **Prinsip Least Privilege**: Berikan hanya role yang diperlukan untuk tugas pengguna
2. **Review Berkala**: Periksa dan update role pengguna secara berkala
3. **Password Aman**: Gunakan password yang kuat sesuai persyaratan (12+ karakter, kombinasi huruf, angka, dan simbol)
4. **Nonaktifkan Pengguna**: Nonaktifkan pengguna yang sudah tidak aktif daripada menghapus untuk keperluan audit
5. **Monitor Log Audit**: Periksa log audit secara berkala untuk mendeteksi aktivitas mencurigakan
