package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "kasirpbol";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Coba koneksi ke database kasirpbol
                try {
                    connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
                    System.out.println("Koneksi ke database kasirpbol berhasil!");
                } catch (SQLException e) {
                    // Jika database belum ada, buat database baru
                    System.out.println("Database belum ada, membuat database...");
                    createDatabase();
                    connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
                }
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            System.err.println("Pastikan library mysql-connector-java sudah ditambahkan ke classpath");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Error koneksi database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void createDatabase() {
        try (Connection tempConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = tempConn.createStatement()) {

            // Buat database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database " + DB_NAME + " berhasil dibuat!");

            // Gunakan database
            stmt.executeUpdate("USE " + DB_NAME);

            // Buat tabel users
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "nama VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(createUsersTable);

            // Buat tabel products
            String createProductsTable = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "kode VARCHAR(50) UNIQUE NOT NULL, " +
                    "nama VARCHAR(100) NOT NULL, " +
                    "harga DECIMAL(10,2) NOT NULL, " +
                    "stok INT NOT NULL DEFAULT 0, " +
                    "deleted_at DATETIME DEFAULT NULL, " +
                    "INDEX idx_deleted_at (deleted_at))";
            stmt.executeUpdate(createProductsTable);

            // Buat tabel transactions
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "total DECIMAL(12,2) NOT NULL, " +
                    "kasir VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(createTransactionsTable);

            // Buat tabel transaction_items
            String createTransactionItemsTable = "CREATE TABLE IF NOT EXISTS transaction_items (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "transaction_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "subtotal DECIMAL(12,2) NOT NULL, " +
                    "FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (product_id) REFERENCES products(id))";
            stmt.executeUpdate(createTransactionItemsTable);

            // Insert default admin user
            String insertAdmin = "INSERT IGNORE INTO users (username, password, nama) " +
                    "VALUES ('admin', 'admin', 'Administrator')";
            stmt.executeUpdate(insertAdmin);

            // Insert ice cream products
            String insertProducts = "INSERT IGNORE INTO products (kode, nama, harga, stok) VALUES " +
                    "('IC001', 'Es Krim Vanilla', 12000, 50), " +
                    "('IC002', 'Es Krim Coklat', 12000, 50), " +
                    "('IC003', 'Es Krim Strawberry', 12000, 45), " +
                    "('IC004', 'Es Krim Mangga', 13000, 40), " +
                    "('IC005', 'Es Krim Mint Chocolate Chip', 15000, 30), " +
                    "('IC006', 'Es Krim Cookies and Cream', 15000, 35), " +
                    "('IC007', 'Es Krim Durian', 18000, 25), " +
                    "('IC008', 'Es Krim Matcha', 16000, 30), " +
                    "('IC009', 'Es Krim Tiramisu', 17000, 28), " +
                    "('IC010', 'Sundae Special', 25000, 20), " +
                    "('IC011', 'Milkshake Vanilla', 18000, 40), " +
                    "('IC012', 'Milkshake Coklat', 18000, 40), " +
                    "('IC013', 'Float Coke', 15000, 50), " +
                    "('IC014', 'Ice Cream Cake Slice', 35000, 15), " +
                    "('IC015', 'Wafer Cone', 2000, 100)";
            stmt.executeUpdate(insertProducts);

            System.out.println("Tabel-tabel berhasil dibuat dan data sample berhasil diinsert!");

        } catch (SQLException e) {
            System.err.println("Error membuat database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inisialisasi database (dipanggil saat aplikasi dimulai)
     */
    public static void initialize() {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Database berhasil diinisialisasi!");
        } else {
            System.err.println("Gagal menginisialisasi database!");
        }
    }

    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}
