package dao;

import database.DatabaseConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /**
     * Mendapatkan semua produk aktif (tidak soft-deleted)
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE deleted_at IS NULL ORDER BY kode";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setKode(rs.getString("kode"));
                product.setNama(rs.getString("nama"));
                product.setHarga(rs.getDouble("harga"));
                product.setStok(rs.getInt("stok"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil produk: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Mendapatkan produk berdasarkan kode
     */
    public Product getProductByCode(String kode) {
        String query = "SELECT * FROM products WHERE kode = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, kode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setKode(rs.getString("kode"));
                product.setNama(rs.getString("nama"));
                product.setHarga(rs.getDouble("harga"));
                product.setStok(rs.getInt("stok"));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil produk: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mendapatkan produk berdasarkan ID
     */
    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setKode(rs.getString("kode"));
                product.setNama(rs.getString("nama"));
                product.setHarga(rs.getDouble("harga"));
                product.setStok(rs.getInt("stok"));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil produk: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mencari produk aktif berdasarkan keyword (kode atau nama)
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE deleted_at IS NULL AND (kode LIKE ? OR nama LIKE ?) ORDER BY kode";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setKode(rs.getString("kode"));
                product.setNama(rs.getString("nama"));
                product.setHarga(rs.getDouble("harga"));
                product.setStok(rs.getInt("stok"));
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error saat mencari produk: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Menambah produk baru
     */
    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (kode, nama, harga, stok) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getKode());
            stmt.setString(2, product.getNama());
            stmt.setDouble(3, product.getHarga());
            stmt.setInt(4, product.getStok());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat menambah produk: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update produk
     */
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET kode = ?, nama = ?, harga = ?, stok = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getKode());
            stmt.setString(2, product.getNama());
            stmt.setDouble(3, product.getHarga());
            stmt.setInt(4, product.getStok());
            stmt.setInt(5, product.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat update produk: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hapus produk (soft delete jika pernah ditransaksikan, hard delete jika belum)
     */
    public boolean deleteProduct(int id) {
        try {
            Connection conn = DatabaseConnection.getConnection();

            // Cek apakah produk pernah digunakan dalam transaksi
            String checkQuery = "SELECT COUNT(*) FROM transaction_items WHERE product_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            boolean hasTransactions = false;
            if (rs.next() && rs.getInt(1) > 0) {
                hasTransactions = true;
            }
            rs.close();
            checkStmt.close();

            String query;
            if (hasTransactions) {
                // Soft delete: set deleted_at dan stok = 0
                query = "UPDATE products SET deleted_at = NOW(), stok = 0 WHERE id = ?";
                System.out.println("Soft delete produk ID: " + id);
            } else {
                // Hard delete: hapus row
                query = "DELETE FROM products WHERE id = ?";
                System.out.println("Hard delete produk ID: " + id);
            }

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat hapus produk: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update stok produk
     */
    public boolean updateStock(int productId, int newStock) {
        String query = "UPDATE products SET stok = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saat update stok: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate kode produk otomatis berikutnya (format: IC001, IC002, ...)
     */
    public String getNextProductCode() {
        String query = "SELECT kode FROM products WHERE kode LIKE 'IC%' ORDER BY kode DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String lastCode = rs.getString("kode");
                // Extract number dari IC001 -> 001
                String numPart = lastCode.substring(2);
                int nextNum = Integer.parseInt(numPart) + 1;
                // Format kembali dengan leading zero
                return String.format("IC%03d", nextNum);
            } else {
                // Jika belum ada produk, mulai dari IC001
                return "IC001";
            }

        } catch (SQLException e) {
            System.err.println("Error saat generate kode produk: " + e.getMessage());
            e.printStackTrace();
            // Default fallback
            return "IC001";
        }
    }
}
