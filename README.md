# Sudut Cooler - Aplikasi Kasir Es Krim

Aplikasi kasir modern berbasis Java Swing untuk toko es krim "Sudut Cooler" dengan antarmuka yang user-friendly dan fitur manajemen lengkap.

![Java](https://img.shields.io/badge/Java-17+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-green)

## 📋 Deskripsi

Sudut Cooler adalah aplikasi Point of Sale (POS) yang dirancang khusus untuk toko es krim. Aplikasi ini menyediakan sistem kasir yang efisien dengan fitur manajemen produk, transaksi, stok, dan riwayat penjualan lengkap.

## ✨ Fitur Utama

### 🔐 Sistem Login

- Autentikasi pengguna dengan username dan password
- Akun default: `admin` / `admin`
- UI modern dengan tema es krim

### 🛒 Panel Kasir

- Combobox produk dengan format: **Nama Produk - Kode**
- Keranjang belanja interaktif
- Kalkulasi otomatis total pembayaran
- Validasi stok real-time
- Filter otomatis: hanya tampilkan produk aktif (stok > 0 dan tidak terhapus)

### 📦 Manajemen Produk

- **Auto-generate kode produk** (format: IC001, IC002, IC003, ...)
- CRUD lengkap (Create, Read, Update, Delete)
- Pencarian produk berdasarkan kode atau nama
- **Soft Delete**: Produk yang pernah ditransaksikan tidak dihapus permanen
  - Jika produk pernah ditransaksikan → Soft delete (set `deleted_at` dan stok = 0)
  - Jika produk belum pernah ditransaksikan → Hard delete (hapus permanen)
- Form input dengan validasi

### 📊 Riwayat Transaksi

- Daftar transaksi lengkap dengan tanggal, total, dan nama kasir
- Detail transaksi per item
- Format tanggal yang mudah dibaca
- Dialog detail dengan informasi lengkap

### 📈 Panel Stok

- Monitoring stok produk real-time
- Fitur penambahan stok
- Pencarian produk
- Update stok langsung dari panel

## 🎨 Desain UI/UX

- **Tema Modern**: Desain clean dengan skema warna biru, hijau, dan aksen gradien
- **Branding**: Konsisten dengan tema "Sudut Cooler"
- **Fullscreen**: Aplikasi berjalan fullscreen dengan tata letak optimal
- **No Emoji**: Menggunakan text label untuk kompatibilitas lintas sistem
- **Table Headers**: Text visibility yang jelas dengan background biru
- **Button Contrast**: Tombol dengan warna yang jelas dan hover effects

## 🗄️ Database

### Struktur Tabel

#### `users`

- `id` (INT, PK, Auto Increment)
- `username` (VARCHAR(50), UNIQUE)
- `password` (VARCHAR(100))
- `nama` (VARCHAR(100))
- `created_at` (TIMESTAMP)

#### `products`

- `id` (INT, PK, Auto Increment)
- `kode` (VARCHAR(50), UNIQUE) - Auto-generated (IC001, IC002, ...)
- `nama` (VARCHAR(100))
- `harga` (DECIMAL(10,2))
- `stok` (INT)
- `deleted_at` (DATETIME, NULL) - Untuk soft delete
- Index: `idx_deleted_at`

#### `transactions`

- `id` (INT, PK, Auto Increment)
- `tanggal` (DATETIME)
- `total` (DECIMAL(12,2))
- `kasir` (VARCHAR(100))
- `created_at` (TIMESTAMP)

#### `transaction_items`

- `id` (INT, PK, Auto Increment)
- `transaction_id` (INT, FK)
- `product_id` (INT, FK)
- `quantity` (INT)
- `subtotal` (DECIMAL(12,2))

### Data Sample

Aplikasi sudah dilengkapi dengan 15 produk es krim:

- Es Krim Vanilla, Coklat, Strawberry, Mangga
- Es Krim Mint Chocolate Chip, Cookies and Cream
- Es Krim Durian, Matcha, Tiramisu
- Sundae Special
- Milkshake Vanilla, Coklat
- Float Coke
- Ice Cream Cake Slice
- Wafer Cone

## 🚀 Instalasi

### Persyaratan Sistem

- **Java**: JDK 17 atau lebih tinggi
- **MySQL**: MySQL 8.0 atau lebih tinggi
- **OS**: Windows, macOS, atau Linux

### Langkah Instalasi

1. **Clone Repository**

   ```bash
   git clone https://github.com/BriantPanji/SudutCooler.git
   cd SudutCooler
   ```

2. **Download MySQL Connector**

   - File `mysql-connector-j-8.4.0.jar` sudah termasuk di folder `lib/`
   - Jika tidak ada, lihat instruksi di `lib/DOWNLOAD_MYSQL_CONNECTOR.md`

3. **Setup Database MySQL**

   - Pastikan MySQL server berjalan
   - Database akan dibuat otomatis saat aplikasi pertama kali dijalankan
   - Credential default:
     ```
     Host: localhost
     Port: 3306
     Database: kasirpbol
     User: root
     Password: (kosong)
     ```

4. **Compile Aplikasi**

   ```bash
   compile.bat    # Windows
   ```

5. **Run Aplikasi**
   ```bash
   run.bat        # Windows
   ```

## 📖 Cara Penggunaan

### Login

1. Jalankan aplikasi
2. Login dengan username: `admin` dan password: `admin`

### Menambah Produk Baru

1. Buka tab **PRODUK**
2. Isi form (hanya Nama, Harga, dan Stok)
3. **Kode produk akan di-generate otomatis** (IC016, IC017, ...)
4. Klik tombol **[+] Tambah**
5. Sistem akan menampilkan kode produk yang telah dibuat

### Melakukan Transaksi

1. Buka tab **KASIR**
2. Pilih produk dari dropdown (format: **Nama Produk - Kode**)
3. Tentukan jumlah
4. Klik **[+] Tambah** untuk masukkan ke keranjang
5. Ulangi untuk produk lain
6. Klik **[$] BAYAR** untuk proses pembayaran
7. Konfirmasi transaksi

### Mengelola Stok

1. Buka tab **STOK**
2. Lihat daftar produk dengan stok saat ini
3. Pilih produk yang ingin ditambah stoknya
4. Masukkan jumlah penambahan stok
5. Klik **Tambah Stok**

### Melihat Riwayat

1. Buka tab **RIWAYAT**
2. Lihat daftar semua transaksi
3. Klik transaksi dan pilih **Lihat Detail** untuk melihat item yang dibeli

## 🏗️ Arsitektur Aplikasi

```
src/
├── Main.java                    # Entry point aplikasi
├── model/                       # Model classes
│   ├── User.java
│   ├── Product.java
│   ├── Transaction.java
│   └── TransactionItem.java
├── dao/                         # Data Access Objects
│   ├── UserDAO.java
│   ├── ProductDAO.java
│   └── TransactionDAO.java
├── database/                    # Database connection
│   └── DatabaseConnection.java
└── gui/                         # User Interface
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── CashierPanel.java
    ├── ProductPanel.java
    ├── StockPanel.java
    └── TransactionHistoryPanel.java
```

### Design Pattern

- **MVC (Model-View-Controller)**: Pemisahan logic, data, dan UI
- **DAO Pattern**: Abstraksi akses database
- **Singleton**: Database connection management

## 🔧 Konfigurasi

### Database Connection

Edit file `src/database/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "kasirpbol";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

### UI Theme Colors

Edit konstanta warna di setiap Panel:

```java
private static final Color PRIMARY_COLOR = new Color(52, 152, 219);    // Biru
private static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Hijau
private static final Color DANGER_COLOR = new Color(231, 76, 60);      // Merah
```

## 🐛 Troubleshooting

### Error: ClassNotFoundException MySQL Driver

- Pastikan `mysql-connector-j-8.4.0.jar` ada di folder `lib/`
- Periksa classpath saat compile dan run

### Error: Connection Refused

- Pastikan MySQL server berjalan
- Periksa credential database (user, password, port)
- Pastikan port 3306 tidak diblokir firewall

### Produk Tidak Muncul di Kasir

- Pastikan produk memiliki stok > 0
- Pastikan produk tidak ter-soft-delete (`deleted_at` harus NULL)
- Klik tombol **Refresh** di panel produk

### Auto-generate Kode Tidak Bekerja

- Pastikan format kode di database konsisten (IC001, IC002, ...)
- Jika ada kode manual non-standar, hapus atau ubah formatnya

## 📝 Catatan Pengembangan

### Fitur Soft Delete

Produk yang pernah ditransaksikan tidak dapat dihapus permanen untuk menjaga integritas data history transaksi. Sistem akan:

- Set kolom `deleted_at` dengan timestamp saat ini
- Set `stok` menjadi 0
- Produk tidak akan muncul di panel kasir dan panel stok
- Data transaksi lama tetap valid

### Auto-generate Kode Produk

Sistem akan mencari kode terakhir dengan prefix "IC", increment angka, dan format dengan leading zero (IC001, IC002, ..., IC999).

## 👥 Kontributor

- **Briant Panji** - Developer

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas akademik.

## 🙏 Acknowledgments

- Java Swing Documentation
- MySQL Documentation
- Google Fonts (Segoe UI)

---

**Sudut Cooler** - _Aplikasi Kasir Modern untuk Toko Es Krim_ 🍦
