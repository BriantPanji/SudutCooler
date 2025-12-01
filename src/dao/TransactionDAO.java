package dao;

import database.DatabaseConnection;
import model.Transaction;
import model.TransactionItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Menyimpan transaksi beserta item-itemnya
     */
    public boolean saveTransaction(Transaction transaction, List<TransactionItem> items) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi

            // Insert transaksi
            String transQuery = "INSERT INTO transactions (tanggal, total, kasir) VALUES (?, ?, ?)";
            PreparedStatement transStmt = conn.prepareStatement(transQuery, Statement.RETURN_GENERATED_KEYS);
            transStmt.setTimestamp(1, Timestamp.valueOf(transaction.getTanggal()));
            transStmt.setDouble(2, transaction.getTotal());
            transStmt.setString(3, transaction.getKasir());

            int rowsAffected = transStmt.executeUpdate();

            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }

            // Dapatkan ID transaksi yang baru saja dibuat
            ResultSet generatedKeys = transStmt.getGeneratedKeys();
            int transactionId = 0;
            if (generatedKeys.next()) {
                transactionId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }

            // Insert transaction items
            String itemQuery = "INSERT INTO transaction_items (transaction_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemQuery);

            for (TransactionItem item : items) {
                itemStmt.setInt(1, transactionId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getSubtotal());
                itemStmt.addBatch();

                // Update stok produk
                String updateStockQuery = "UPDATE products SET stok = stok - ? WHERE id = ?";
                PreparedStatement stockStmt = conn.prepareStatement(updateStockQuery);
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getProductId());
                stockStmt.executeUpdate();
                stockStmt.close();
            }

            itemStmt.executeBatch();

            conn.commit(); // Commit transaksi
            conn.setAutoCommit(true);

            transStmt.close();
            itemStmt.close();

            return true;

        } catch (SQLException e) {
            System.err.println("Error saat menyimpan transaksi: " + e.getMessage());
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }

    /**
     * Mendapatkan semua transaksi
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions ORDER BY tanggal DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTanggal(rs.getTimestamp("tanggal").toLocalDateTime());
                transaction.setTotal(rs.getDouble("total"));
                transaction.setKasir(rs.getString("kasir"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil transaksi: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Mendapatkan transaksi berdasarkan ID
     */
    public Transaction getTransactionById(int id) {
        String query = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTanggal(rs.getTimestamp("tanggal").toLocalDateTime());
                transaction.setTotal(rs.getDouble("total"));
                transaction.setKasir(rs.getString("kasir"));
                return transaction;
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil transaksi: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mendapatkan item-item dari transaksi
     */
    public List<TransactionItem> getTransactionItems(int transactionId) {
        List<TransactionItem> items = new ArrayList<>();
        String query = "SELECT ti.*, p.nama as product_name, p.harga as product_price " +
                "FROM transaction_items ti " +
                "JOIN products p ON ti.product_id = p.id " +
                "WHERE ti.transaction_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TransactionItem item = new TransactionItem();
                item.setId(rs.getInt("id"));
                item.setTransactionId(rs.getInt("transaction_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setProductPrice(rs.getDouble("product_price"));
                item.setQuantity(rs.getInt("quantity"));
                item.setSubtotal(rs.getDouble("subtotal"));
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil item transaksi: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Mendapatkan transaksi berdasarkan tanggal
     */
    public List<Transaction> getTransactionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTanggal(rs.getTimestamp("tanggal").toLocalDateTime());
                transaction.setTotal(rs.getDouble("total"));
                transaction.setKasir(rs.getString("kasir"));
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Error saat mengambil transaksi: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }
}
