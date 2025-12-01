# Aplikasi Kasir - Java Swing + MySQL

Program kasir sederhana dengan GUI Java Swing yang terhubung dengan database MySQL.

## Fitur

- **Login Wajib** - Autentikasi user sebelum akses aplikasi
- **Manajemen Produk** - CRUD produk (tambah, edit, hapus, cari)
- **Kasir/Transaksi** - Proses penjualan dengan keranjang belanja
- **Riwayat Transaksi** - Lihat dan detail transaksi

## Struktur Direktori

```
tubes/
├── database/
│   └── schema.sql              # SQL schema (opsional)
├── src/
│   ├── Main.java               # Entry point
│   ├── database/
│   │   └── DatabaseConnection.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Transaction.java
│   │   └── TransactionItem.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── ProductDAO.java
│   │   └── TransactionDAO.java
│   └── gui/
│       ├── LoginFrame.java
│       ├── MainFrame.java
│       ├── ProductPanel.java
│       ├── CashierPanel.java
│       └── TransactionHistoryPanel.java
└── lib/
    └── mysql-connector-java-x.x.x.jar (perlu didownload)
```

## Requirement

1. **Java JDK 8+**
2. **MySQL Server** (XAMPP/MySQL Workbench/dll)
3. **MySQL Connector/J** (JDBC Driver)

## Setup

### 1. Install MySQL

Pastikan MySQL Server sudah terinstall dan berjalan di `localhost:3306`

### 2. Download MySQL Connector

Download dari: https://dev.mysql.com/downloads/connector/j/

- Pilih "Platform Independent"
- Extract dan copy file `.jar` ke folder `lib/`

### 3. Konfigurasi Database

Database akan dibuat otomatis saat aplikasi pertama kali dijalankan.

**Kredensial default:**

- Host: `localhost`
- Port: `3306`
- Database: `kasirpbol`
- Username: `root`
- Password: _(kosong)_

Jika password root berbeda, edit file `DatabaseConnection.java` baris 13:

```java
private static final String DB_PASSWORD = "your_password";
```

### 4. Compile

```bash
# Windows
javac -d bin -cp "lib/*" src/**/*.java src/*.java

# Linux/Mac
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

### 5. Run

```bash
# Windows
java -cp "bin;lib/*" Main

# Linux/Mac
java -cp "bin:lib/*" Main
```

## Login Default

```
Username: admin
Password: admin
```

## Produk Sample

Setelah database dibuat, akan ada 5 produk sample:

- P001 - Teh Botol (Rp 5.000)
- P002 - Indomie Goreng (Rp 3.500)
- P003 - Aqua 600ml (Rp 4.000)
- P004 - Chitato (Rp 8.000)
- P005 - Kopiko (Rp 1.000)

## Cara Penggunaan

### 1. Login

- Jalankan aplikasi
- Masukkan username dan password
- Klik Login

### 2. Kasir (Transaksi)

- Pilih produk dari dropdown
- Masukkan jumlah
- Klik "Tambah ke Keranjang"
- Klik "Bayar" untuk proses pembayaran
- Stok otomatis berkurang

### 3. Manajemen Produk

- Lihat daftar produk
- Tambah produk baru
- Edit produk (klik produk di tabel)
- Hapus produk
- Cari produk

### 4. Riwayat Transaksi

- Lihat semua transaksi
- Klik transaksi dan "Lihat Detail" untuk melihat item

## Troubleshooting

### Error: MySQL JDBC Driver tidak ditemukan

Pastikan file `mysql-connector-java-x.x.x.jar` ada di folder `lib/` dan sudah di-include saat compile/run.

### Error: Access denied for user 'root'@'localhost'

Periksa password MySQL root Anda dan sesuaikan di `DatabaseConnection.java`.

### Database tidak terbuat otomatis

Jalankan manual SQL di `database/schema.sql` melalui MySQL Workbench atau phpMyAdmin.

## Notes

- Database dan tabel akan dibuat otomatis saat aplikasi pertama dijalankan
- Semua fitur memerlukan login terlebih dahulu
- Transaksi menggunakan database transaction untuk memastikan konsistensi data
