# Download MySQL Connector/J

Aplikasi Sudut Cooler membutuhkan MySQL Connector/J untuk terhubung ke database MySQL.

## ✅ File Sudah Tersedia

File `mysql-connector-j-8.4.0.jar` **sudah termasuk** dalam repository ini di folder `lib/`.

Jika file hilang atau Anda ingin menggunakan versi terbaru, ikuti instruksi di bawah.

## 📥 Download Manual (Opsional)

### Metode 1: Download dari Maven Central (Recommended)

1. Kunjungi: https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/
2. Pilih versi terbaru (misalnya `8.4.0/`)
3. Download file `.jar` (contoh: `mysql-connector-j-8.4.0.jar`)
4. Pindahkan file ke folder `lib/` di project ini

### Metode 2: Download dari MySQL Official Site

1. Kunjungi: https://dev.mysql.com/downloads/connector/j/
2. Pilih **Platform Independent**
3. Download file ZIP
4. Extract dan ambil file `.jar`
5. Pindahkan ke folder `lib/` dengan nama `mysql-connector-j-8.4.0.jar`

## 🔧 Instalasi

1. Pastikan file JAR ada di: `lib/mysql-connector-j-8.4.0.jar`
2. Compile script (`compile.bat`) sudah dikonfigurasi untuk menggunakan library ini
3. Run script (`run.bat`) juga sudah menginclude classpath yang benar

## ✔️ Verifikasi

Jalankan aplikasi:

```bash
compile.bat
run.bat
```

Jika muncul error `ClassNotFoundException: com.mysql.cj.jdbc.Driver`, berarti:

- File JAR tidak ada di folder `lib/`
- Atau classpath tidak dikonfigurasi dengan benar

## 📋 Versi yang Digunakan

- **MySQL Connector/J**: 8.4.0
- **Kompatibel dengan**: MySQL 8.0+
- **Java Version**: Java 17+

## 🆘 Troubleshooting

### Error: NoClassDefFoundError

**Solusi**: Pastikan file JAR ada dan classpath benar

```bash
# Check file exists
dir lib\mysql-connector-j-8.4.0.jar

# Should show: mysql-connector-j-8.4.0.jar
```

### Error: Wrong Version

**Solusi**: Gunakan MySQL Connector/J 8.x untuk MySQL 8.x

### Permission Denied

**Solusi**: Pastikan file JAR memiliki permission read

```bash
# Windows: Right-click -> Properties -> Unblock
```

## 📚 Referensi

- [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/en/)
- [Maven Repository](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j)

---

**Note**: File JAR ini hanya diperlukan untuk development dan deployment. Pastikan selalu gunakan versi yang kompatibel dengan MySQL server Anda.
