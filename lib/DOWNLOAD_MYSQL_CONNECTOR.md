# Download MySQL Connector/J

Aplikasi ini memerlukan MySQL JDBC Driver untuk koneksi ke database.

## Download Link

https://dev.mysql.com/downloads/connector/j/

## Langkah Download:

1. Klik link di atas
2. Pilih "Platform Independent"
3. Download file ZIP
4. Extract file ZIP
5. Copy file `mysql-connector-java-X.X.XX.jar` ke folder `lib/` project ini

## Alternatif (jika sudah punya XAMPP):

Jika Anda menggunakan XAMPP, MySQL Connector mungkin sudah ada di:

- `C:\xampp\mysql\lib\mysql-connector-java-X.X.XX.jar`

Copy file tersebut ke folder `lib/` project ini.

## Versi yang Direkomendasikan:

- MySQL Connector/J 8.0.x (untuk MySQL 8.x)
- MySQL Connector/J 5.1.x (untuk MySQL 5.x)

## Setelah Download:

Pastikan struktur folder seperti ini:

```
tubes/
├── lib/
│   └── mysql-connector-java-8.x.xx.jar  <-- File ini harus ada!
├── src/
│   └── ...
└── compile.bat
```

Kemudian jalankan `compile.bat`
