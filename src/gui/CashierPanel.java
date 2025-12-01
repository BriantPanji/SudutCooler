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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CashierPanel extends JPanel {
    private JComboBox<Product> cmbProduct;
    private JSpinner spnQuantity;
    private JButton btnAddItem, btnRemoveItem, btnPay, btnCancel;
    private JTable tableCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private ProductDAO productDAO;
    private TransactionDAO transactionDAO;
    private User currentUser;
    private double totalAmount = 0;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
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

        // Top Panel
        JPanel topCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topCard.setBackground(Color.WHITE);
        topCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel lblTitle = new JLabel("Pilih Produk:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(TEXT_DARK);
        topCard.add(lblTitle);

        cmbProduct = new JComboBox<>();
        cmbProduct.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbProduct.setPreferredSize(new Dimension(350, 40));
        topCard.add(cmbProduct);

        JLabel lblQty = new JLabel("Jumlah:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQty.setForeground(TEXT_DARK);
        topCard.add(lblQty);

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        spnQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spnQuantity.setPreferredSize(new Dimension(100, 40));
        ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        topCard.add(spnQuantity);

        btnAddItem = createStyledButton("[+] Tambah", PRIMARY_COLOR);
        btnAddItem.setPreferredSize(new Dimension(200, 40));
        btnAddItem.addActionListener(e -> addToCart());
        topCard.add(btnAddItem);

        // Cart Table
        String[] columns = { "Produk", "Harga", "Jumlah", "Subtotal" };
        cartModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCart = new JTable(cartModel);
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableCart.setRowHeight(32);
        tableCart.setSelectionBackground(new Color(52, 152, 219, 50));
        tableCart.setSelectionForeground(TEXT_DARK);

        JTableHeader header = tableCart.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tableCart);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(BG_COLOR);

        // Total Panel
        JPanel totalCard = new JPanel();
        totalCard.setBackground(Color.WHITE);
        totalCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)));
        totalCard.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JLabel lblTotalLabel = new JLabel("TOTAL PEMBAYARAN:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalLabel.setForeground(TEXT_DARK);

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(SUCCESS_COLOR);

        totalCard.add(lblTotalLabel);
        totalCard.add(lblTotal);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        btnRemoveItem = createStyledButton("[X] Hapus Item", DANGER_COLOR);
        btnRemoveItem.setPreferredSize(new Dimension(160, 45));
        btnRemoveItem.addActionListener(e -> removeFromCart());

        btnCancel = createStyledButton("Batal", new Color(149, 165, 166));
        btnCancel.setPreferredSize(new Dimension(150, 45));
        btnCancel.addActionListener(e -> clearCart());

        btnPay = createStyledButton("[$] BAYAR", SUCCESS_COLOR);
        btnPay.setPreferredSize(new Dimension(180, 55));
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPay.addActionListener(e -> processPayment());

        buttonPanel.add(btnRemoveItem);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnPay);

        bottomPanel.add(totalCard, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(topCard, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        cmbProduct.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            if (p.getStok() > 0) {
                cmbProduct.addItem(p);
            }
        }
    }

    private void addToCart() {
        Product selectedProduct = (Product) cmbProduct.getSelectedItem();
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
    }

    private void removeFromCart() {
        int selectedRow = tableCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!");
            return;
        }

        cartModel.removeRow(selectedRow);
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Total: Rp " + String.format("%.0f", totalAmount) + "\n\nProses pembayaran?",
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
                String productCode = parts[parts.length - 1]; // Ambil bagian terakhir (kode)
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
                JOptionPane.showMessageDialog(this,
                        "Pembayaran berhasil!\nTotal: Rp " + String.format("%.0f", totalAmount),
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                clearCart();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Pembayaran gagal!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        totalAmount = 0;
        lblTotal.setText("Rp 0");
        spnQuantity.setValue(1);
    }

    public void refresh() {
        loadProducts();
    }
}
