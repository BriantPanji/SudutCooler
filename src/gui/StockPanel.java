package gui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class StockPanel extends JPanel {
    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtAddStock;
    private JButton btnSearch, btnRefresh, btnAddStock;
    private ProductDAO productDAO;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    // private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public StockPanel() {
        productDAO = new ProductDAO();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel("Manajemen Stok Produk");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        topPanel.add(lblTitle);

        topPanel.add(Box.createHorizontalStrut(30));

        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_DARK);
        topPanel.add(lblSearch);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        topPanel.add(txtSearch);

        btnSearch = createStyledButton("Cari", PRIMARY_COLOR);
        btnSearch.addActionListener(e -> searchProducts());
        topPanel.add(btnSearch);

        btnRefresh = createStyledButton("Refresh", new Color(52, 73, 94));
        btnRefresh.addActionListener(e -> loadProducts());
        topPanel.add(btnRefresh);

        // Table Panel
        String[] columns = { "ID", "Kode", "Nama Produk", "Harga", "Stok Tersedia" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableProducts = new JTable(tableModel);
        tableProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProducts.setRowHeight(32);
        tableProducts.setSelectionBackground(new Color(52, 152, 219, 50));
        tableProducts.setSelectionForeground(TEXT_DARK);

        // Style table header - FIX VISIBILITY
        JTableHeader header = tableProducts.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK); // WHITE TEXT FOR VISIBILITY
        header.setPreferredSize(new Dimension(0, 38));

        JScrollPane scrollPane = new JScrollPane(tableProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        // Bottom Panel - Add Stock Form
        JPanel bottomCard = new JPanel(new BorderLayout(10, 10));
        bottomCard.setBackground(Color.WHITE);
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel formTitle = new JLabel("Tambah Stok");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_DARK);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        formPanel.setBackground(Color.WHITE);

        JLabel lblInstruction = new JLabel("Pilih produk dari tabel, lalu tambah jumlah stok:");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruction.setForeground(TEXT_DARK);
        formPanel.add(lblInstruction);

        txtAddStock = new JTextField(10);
        txtAddStock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAddStock.setPreferredSize(new Dimension(150, 40));
        txtAddStock.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(txtAddStock);

        btnAddStock = createStyledButton("Tambah Stok", SUCCESS_COLOR);
        btnAddStock.setPreferredSize(new Dimension(140, 40));
        btnAddStock.addActionListener(e -> addStock());
        formPanel.add(btnAddStock);

        bottomCard.add(formTitle, BorderLayout.NORTH);
        bottomCard.add(formPanel, BorderLayout.CENTER);

        // Add to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomCard, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            Object[] row = { p.getId(), p.getKode(), p.getNama(),
                    String.format("Rp %.0f", p.getHarga()), p.getStok() };
            tableModel.addRow(row);
        }
        txtAddStock.setText("");
        tableProducts.clearSelection();
    }

    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        tableModel.setRowCount(0);
        List<Product> products = productDAO.searchProducts(keyword);
        for (Product p : products) {
            Object[] row = { p.getId(), p.getKode(), p.getNama(),
                    String.format("Rp %.0f", p.getHarga()), p.getStok() };
            tableModel.addRow(row);
        }
    }

    private void addStock() {
        int selectedRow = tableProducts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
            return;
        }

        String addStockStr = txtAddStock.getText().trim();
        if (addStockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah stok yang akan ditambahkan!");
            return;
        }

        try {
            int addStockAmount = Integer.parseInt(addStockStr);
            if (addStockAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah stok harus lebih dari 0!");
                return;
            }

            int productId = (int) tableProducts.getValueAt(selectedRow, 0);
            String productName = tableProducts.getValueAt(selectedRow, 2).toString();
            int currentStock = (int) tableProducts.getValueAt(selectedRow, 4);
            int newStock = currentStock + addStockAmount;

            // Update product stock
            if (productDAO.updateStock(productId, newStock)) {
                JOptionPane.showMessageDialog(this,
                        "Stok berhasil ditambahkan!\n" +
                                "Produk: " + productName + "\n" +
                                "Stok lama: " + currentStock + "\n" +
                                "Ditambah: " + addStockAmount + "\n" +
                                "Stok baru: " + newStock,
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambah stok!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah stok harus berupa angka!");
        }
    }

    public void refresh() {
        loadProducts();
    }
}
