CREATE DATABASE IF NOT EXISTS kasirpbol;
USE kasirpbol;

-- Tabel Users untuk login
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    nama VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Products untuk data produk
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    kode VARCHAR(50) UNIQUE NOT NULL,
    nama VARCHAR(100) NOT NULL,
    harga DECIMAL(10, 2) NOT NULL,
    stok INT NOT NULL DEFAULT 0,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_deleted_at (deleted_at)
);

-- Tabel Transactions untuk data transaksi
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12, 2) NOT NULL,
    kasir VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Transaction Items untuk detail item transaksi
CREATE TABLE IF NOT EXISTS transaction_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert default admin user (password: admin)
INSERT IGNORE INTO users (username, password, nama) 
VALUES ('admin', 'admin', 'Administrator');

-- Insert ice cream products
INSERT IGNORE INTO products (kode, nama, harga, stok) VALUES
('IC001', 'Es Krim Vanilla', 12000, 50),
('IC002', 'Es Krim Coklat', 12000, 50),
('IC003', 'Es Krim Strawberry', 12000, 45),
('IC004', 'Es Krim Mangga', 13000, 40),
('IC005', 'Es Krim Mint Chocolate Chip', 15000, 30),
('IC006', 'Es Krim Cookies and Cream', 15000, 35),
('IC007', 'Es Krim Durian', 18000, 25),
('IC008', 'Es Krim Matcha', 16000, 30),
('IC009', 'Es Krim Tiramisu', 17000, 28),
('IC010', 'Sundae Special', 25000, 20),
('IC011', 'Milkshake Vanilla', 18000, 40),
('IC012', 'Milkshake Coklat', 18000, 40),
('IC013', 'Float Coke', 15000, 50),
('IC014', 'Ice Cream Cake Slice', 35000, 15),
('IC015', 'Wafer Cone', 2000, 100);
