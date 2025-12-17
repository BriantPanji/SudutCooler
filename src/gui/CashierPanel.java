package gui;

import dao.ProductDAO;
import dao.TransactionDAO;
import model.Product;
import model.Transaction;
import model.TransactionItem;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CashierPanel extends JPanel {
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JTextField txtSearch;
    private JList<Product> productList;
    private DefaultListModel<Product> productListModel;
    private JPopupMenu searchPopup;
    private JSpinner spnQuantity;
    private JLabel lblTotal;
    private JButton btnAddToCart, btnRemove, btnCancel, btnPay;
    private JTextArea txtProductInfo;

    private ProductDAO productDAO;
    private TransactionDAO transactionDAO;
    private User currentUser;
    private double totalAmount = 0;

    private List<Product> allProducts;
    private Product selectedProduct = null;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public CashierPanel(User user) {
        this.currentUser = user;
        productDAO = new ProductDAO();
        transactionDAO = new TransactionDAO();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLeft = new JLabel("Pilih Produk");
        titleLeft.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLeft.setForeground(TEXT_DARK);

        // search
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(Color.WHITE);

        JLabel lblSearch = new JLabel("Cari Produk:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_DARK);

        // autocomple aoiwhadw
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setPreferredSize(new Dimension(0, 40));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        txtSearch.setForeground(Color.GRAY);
        txtSearch.setText("Ketik nama produk...");
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Ketik nama produk...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(TEXT_DARK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText("Ketik nama produk...");
                }
            }
        });

        searchPopup = new JPopupMenu();
        searchPopup.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        searchPopup.setFocusable(false);

        productListModel = new DefaultListModel<>();
        productList = new JList<>(productListModel);
        productList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productList.setCellRenderer(new ProductListCellRenderer());
        productList.setVisibleRowCount(6);
        productList.setFocusable(false);

        JScrollPane listScrollPane = new JScrollPane(productList);
        listScrollPane.setPreferredSize(new Dimension(txtSearch.getWidth(), 200));
        listScrollPane.setBorder(null);
        listScrollPane.setFocusable(false);

        searchPopup.add(listScrollPane);

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectProductFromList();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    searchPopup.setVisible(false);
                } else {
                    filterProducts();
                }
            }
        });

        productList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectProductFromList();
            }
        });

        searchPanel.add(lblSearch, BorderLayout.NORTH);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel productInfoPanel = new JPanel(new BorderLayout(5, 5));
        productInfoPanel.setBackground(new Color(245, 245, 245));
        productInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblProductInfo = new JLabel("Produk Dipilih:");
        lblProductInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProductInfo.setForeground(TEXT_DARK);

        txtProductInfo = new JTextArea();
        txtProductInfo.setEditable(false);
        txtProductInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtProductInfo.setBackground(new Color(245, 245, 245));
        txtProductInfo.setBorder(null);
        txtProductInfo.setText("Belum ada produk dipilih");
        txtProductInfo.setRows(4);

        productInfoPanel.add(lblProductInfo, BorderLayout.NORTH);
        productInfoPanel.add(txtProductInfo, BorderLayout.CENTER);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        quantityPanel.setBackground(Color.WHITE);

        JLabel lblQty = new JLabel("Jumlah:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQty.setForeground(TEXT_DARK);

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField().setColumns(5);
        spnQuantity.setPreferredSize(new Dimension(80, 35));

        btnAddToCart = createStyledButton("[+] TAMBAH KE KERANJANG", SUCCESS_COLOR);
        btnAddToCart.setPreferredSize(new Dimension(220, 45));
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddToCart.addActionListener(e -> addToCart());

        quantityPanel.add(lblQty);
        quantityPanel.add(spnQuantity);
        quantityPanel.add(btnAddToCart);

        JPanel leftContent = new JPanel(new BorderLayout(10, 15));
        leftContent.setBackground(Color.WHITE);
        leftContent.add(searchPanel, BorderLayout.NORTH);
        leftContent.add(productInfoPanel, BorderLayout.CENTER);
        leftContent.add(quantityPanel, BorderLayout.SOUTH);

        leftPanel.add(titleLeft, BorderLayout.NORTH);
        leftPanel.add(leftContent, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleRight = new JLabel("Keranjang Belanja");
        titleRight.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleRight.setForeground(TEXT_DARK);

        String[] columns = { "Produk", "Harga", "Qty", "Subtotal" };
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCart = new JTable(cartModel);
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableCart.setRowHeight(35);
        tableCart.setSelectionBackground(new Color(52, 152, 219, 50));
        tableCart.setSelectionForeground(TEXT_DARK);

        JTableHeader header = tableCart.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tableCart);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        // Total Panel
        JPanel totalPanel = new JPanel(new BorderLayout(10, 10));
        totalPanel.setBackground(new Color(44, 62, 80));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTotalLabel = new JLabel("TOTAL:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalLabel.setForeground(Color.WHITE);

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(new Color(46, 204, 113));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPanel.add(lblTotalLabel, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnRemove = createStyledButton("[X] Hapus Item", DANGER_COLOR);
        btnRemove.setPreferredSize(new Dimension(140, 45));
        btnRemove.addActionListener(e -> removeItem());

        btnCancel = createStyledButton("Batalkan", WARNING_COLOR);
        btnCancel.setPreferredSize(new Dimension(140, 45));
        btnCancel.addActionListener(e -> clearCart(true));

        btnPay = createStyledButton("[$] BAYAR", SUCCESS_COLOR);
        btnPay.setPreferredSize(new Dimension(180, 45));
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.addActionListener(e -> processPayment());

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnPay);

        rightPanel.add(titleRight, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(totalPanel, BorderLayout.SOUTH);

        JPanel rightContainer = new JPanel(new BorderLayout(10, 10));
        rightContainer.setBackground(Color.WHITE);
        rightContainer.add(rightPanel, BorderLayout.CENTER);
        rightContainer.add(buttonPanel, BorderLayout.SOUTH);

        // main
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContainer);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private class ProductListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Product) {
                Product product = (Product) value;
                String displayText = String.format(
                        "<html><b>%s</b> - %s<br><font color='#666'>Harga: Rp %.0f | Stok: %d</font></html>",
                        product.getNama(),
                        product.getKode(),
                        product.getHarga(),
                        product.getStok());
                label.setText(displayText);
                label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            }

            if (isSelected) {
                label.setBackground(new Color(52, 152, 219, 100));
            }

            return label;
        }
    }

    private void filterProducts() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty() || keyword.equals("Ketik nama produk...")) {
            searchPopup.setVisible(false);
            return;
        }

        productListModel.clear();
        List<Product> filtered = new ArrayList<>();

        for (Product p : allProducts) {
            if (p.getNama().toLowerCase().contains(keyword.toLowerCase()) ||
                    p.getKode().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }

        if (!filtered.isEmpty()) {
            for (Product p : filtered) {
                productListModel.addElement(p);
            }

            // Show popup below txtSearch
            searchPopup.setPopupSize(txtSearch.getWidth(), Math.min(200, filtered.size() * 60));
            searchPopup.show(txtSearch, 0, txtSearch.getHeight());

            // IMPORTANT: Return focus to txtSearch immediately
            SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
        } else {
            searchPopup.setVisible(false);
        }
    }

    private void selectProductFromList() {
        Product selected = productList.getSelectedValue();
        if (selected != null) {
            selectedProduct = selected;
            txtSearch.setText(selected.getNama() + " - " + selected.getKode());
            txtSearch.setForeground(TEXT_DARK);
            searchPopup.setVisible(false);

            // Update product info
            txtProductInfo.setText(String.format(
                    "Produk: %s\nKode: %s\nHarga: Rp %.0f\nStok: %d",
                    selectedProduct.getNama(),
                    selectedProduct.getKode(),
                    selectedProduct.getHarga(),
                    selectedProduct.getStok()));

            spnQuantity.requestFocus();
        }
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            Color originalColor = bgColor;

            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void loadProducts() {
        allProducts = productDAO.getAllProducts();
        // Filter only products with stock > 0
        allProducts.removeIf(p -> p.getStok() <= 0);
    }

    private void addToCart() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
            return;
        }

        int quantity = (int) spnQuantity.getValue();

        if (quantity > selectedProduct.getStok()) {
            JOptionPane.showMessageDialog(this,
                    "Stok tidak mencukupi! Stok tersedia: " + selectedProduct.getStok());
            return;
        }

        double subtotal = selectedProduct.getHarga() * quantity;

        boolean found = false;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String productName = cartModel.getValueAt(i, 0).toString();
            if (productName.equals(selectedProduct.toString())) {
                int oldQty = (int) cartModel.getValueAt(i, 2);
                int newQty = oldQty + quantity;

                if (newQty > selectedProduct.getStok()) {
                    JOptionPane.showMessageDialog(this,
                            "Stok tidak mencukupi! Stok tersedia: " + selectedProduct.getStok());
                    return;
                }

                double newSubtotal = selectedProduct.getHarga() * newQty;
                cartModel.setValueAt(newQty, i, 2);
                cartModel.setValueAt(String.format("Rp %.0f", newSubtotal), i, 3);
                found = true;
                break;
            }
        }

        if (!found) {
            Object[] row = {
                    selectedProduct.toString(),
                    String.format("Rp %.0f", selectedProduct.getHarga()),
                    quantity,
                    String.format("Rp %.0f", subtotal)
            };
            cartModel.addRow(row);
        }

        updateTotal();
        spnQuantity.setValue(1);

        // Clear selection
        txtSearch.setText("");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setText("Ketik nama produk...");
        txtProductInfo.setText("Belum ada produk dipilih");
        selectedProduct = null;
        txtSearch.requestFocus();
    }

    private void removeItem() {
        int selectedRow = tableCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!");
            return;
        }
        cartModel.removeRow(selectedRow);
        updateTotal();
    }

    private void clearCart(boolean showConfirmation) {
        if (showConfirmation) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Batalkan semua transaksi?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        cartModel.setRowCount(0);
        updateTotal();
    }

    private void updateTotal() {
        totalAmount = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            String subtotalStr = cartModel.getValueAt(i, 3).toString()
                    .replace("Rp ", "").replace(".", "");
            totalAmount += Double.parseDouble(subtotalStr);
        }
        lblTotal.setText(String.format("Rp %.0f", totalAmount));
    }

    private void processPayment() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
            return;
        }

        // Step 1: Input payment amount
        double paymentAmount = 0;
        boolean validPayment = false;

        while (!validPayment) {
            String input = JOptionPane.showInputDialog(this,
                    "Total Pembayaran: Rp " + String.format("%.0f", totalAmount) +
                            "\n\nMasukkan uang yang diberikan customer:",
                    "Input Pembayaran",
                    JOptionPane.QUESTION_MESSAGE);

            // User cancelled
            if (input == null) {
                return;
            }

            // Validate input
            try {
                paymentAmount = Double.parseDouble(input.trim());

                if (paymentAmount < totalAmount) {
                    JOptionPane.showMessageDialog(this,
                            "Uang tidak cukup!\n\nTotal: Rp " + String.format("%.0f", totalAmount) +
                                    "\nDiberikan: Rp " + String.format("%.0f", paymentAmount) +
                                    "\nKurang: Rp " + String.format("%.0f", totalAmount - paymentAmount),
                            "Pembayaran Kurang",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    validPayment = true;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Input tidak valid! Masukkan angka yang benar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Calculate change
        double changeAmount = paymentAmount - totalAmount;

        // Step 2: Confirm payment with change information
        int confirm = JOptionPane.showConfirmDialog(this,
                "Total Pembayaran: Rp " + String.format("%.0f", totalAmount) +
                        "\nUang Diterima   : Rp " + String.format("%.0f", paymentAmount) +
                        "\nKembalian       : Rp " + String.format("%.0f", changeAmount) +
                        "\n\nProses pembayaran?",
                "Konfirmasi Pembayaran",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Transaction transaction = new Transaction(
                    LocalDateTime.now(),
                    totalAmount,
                    currentUser.getNama());

            List<TransactionItem> items = new ArrayList<>();
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                String productStr = cartModel.getValueAt(i, 0).toString();
                // Format: "Nama - Kode", ambil kode setelah " - "
                String[] parts = productStr.split(" - ");
                String productCode = parts[parts.length - 1];
                Product product = productDAO.getProductByCode(productCode);

                int quantity = (int) cartModel.getValueAt(i, 2);
                String subtotalStr = cartModel.getValueAt(i, 3).toString()
                        .replace("Rp ", "").replace(".", "");
                double subtotal = Double.parseDouble(subtotalStr);

                TransactionItem item = new TransactionItem(
                        0,
                        product.getId(),
                        quantity,
                        subtotal);
                items.add(item);
            }

            if (transactionDAO.saveTransaction(transaction, items)) {
                // Generate receipt with payment info
                String receiptPath = generateReceipt(transaction, items, paymentAmount, changeAmount);

                JOptionPane.showMessageDialog(this,
                        "Pembayaran berhasil!" +
                                "\n\nTotal     : Rp " + String.format("%.0f", totalAmount) +
                                "\nDiterima  : Rp " + String.format("%.0f", paymentAmount) +
                                "\nKembalian : Rp " + String.format("%.0f", changeAmount) +
                                "\n\nStruk telah disimpan di:\n" + receiptPath,
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                clearCart(false); // FIXED: No confirmation after successful payment
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Pembayaran gagal!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateReceipt(Transaction transaction, List<TransactionItem> items, double paymentAmount,
            double changeAmount) {
        try {
            // Create receipts directory if it doesn't exist
            File receiptsDir = new File("receipts");
            if (!receiptsDir.exists()) {
                receiptsDir.mkdirs();
            }

            // Generate filename with timestamp
            DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = transaction.getTanggal().format(fileFormatter);
            String filename = "receipts/struk_" + timestamp + ".txt";

            // Create the receipt file
            File receiptFile = new File(filename);
            PrintWriter writer = new PrintWriter(new FileWriter(receiptFile));

            // Format date for display
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String displayDate = transaction.getTanggal().format(displayFormatter);

            // Write receipt header
            writer.println("================================================");
            writer.println("           SUDUT COOLER STORE");
            writer.println("================================================");
            writer.println("Alamat   : Jl. Universitas No. 24");
            writer.println("Telp     : (021) 1234-5678");
            writer.println("================================================");
            writer.println();
            writer.println("Kasir    : " + currentUser.getNama());
            writer.println("Tanggal  : " + displayDate);
            writer.println("No. Trx  : TRX-" + timestamp);
            writer.println();
            writer.println("------------------------------------------------");
            writer.println("ITEM                    QTY   HARGA    SUBTOTAL");
            writer.println("------------------------------------------------");

            // Write items
            for (TransactionItem item : items) {
                Product product = productDAO.getProductById(item.getProductId());
                String productName = product.getNama();

                // Truncate product name if too long
                if (productName.length() > 20) {
                    productName = productName.substring(0, 17) + "...";
                }

                // Format: product name (20 chars), qty (5 chars), price (8 chars), subtotal (10
                // chars)
                writer.printf("%-20s %5d %8.0f %10.0f\n",
                        productName,
                        item.getQuantity(),
                        product.getHarga(),
                        item.getSubtotal());
            }

            writer.println("------------------------------------------------");
            writer.printf("%34s %10.0f\n", "TOTAL: Rp", transaction.getTotal());
            writer.println();
            writer.printf("%34s %10.0f\n", "Uang Diterima: Rp", paymentAmount);
            writer.printf("%34s %10.0f\n", "Kembalian: Rp", changeAmount);
            writer.println("================================================");
            writer.println("     TERIMA KASIH ATAS KUNJUNGAN ANDA");

            writer.close();

            return receiptFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Gagal membuat struk - " + e.getMessage();
        }
    }

    public void refresh() {
        loadProducts();
        cartModel.setRowCount(0);
        updateTotal();
    }
}
