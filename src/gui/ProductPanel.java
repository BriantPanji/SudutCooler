package gui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtKode, txtNama, txtHarga, txtStok, txtSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnClear, btnSearch;
    private ProductDAO productDAO;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public ProductPanel() {
        productDAO = new ProductDAO();
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_DARK);
        topPanel.add(lblSearch);

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(250, 35));
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

        // Table
        String[] columns = { "ID", "Kode", "Nama Produk", "Harga", "Stok" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(TEXT_DARK);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedProduct();
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 38));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        // Form Panel
        JPanel formCard = new JPanel(new BorderLayout(10, 10));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        JLabel formTitle = new JLabel("Form Produk Es Krim");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(TEXT_DARK);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kode - disabled (auto-generated)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Kode Produk:"), gbc);
        gbc.gridx = 1;
        txtKode = createStyledTextField();
        txtKode.setEnabled(false);
        txtKode.setBackground(new Color(240, 240, 240));
        txtKode.setText("(Auto-generated)");
        formPanel.add(txtKode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("Nama Produk:"), gbc);
        gbc.gridx = 1;
        txtNama = createStyledTextField();
        formPanel.add(txtNama, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("Harga:"), gbc);
        gbc.gridx = 1;
        txtHarga = createStyledTextField();
        formPanel.add(txtHarga, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("Stok:"), gbc);
        gbc.gridx = 1;
        txtStok = createStyledTextField();
        formPanel.add(txtStok, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnAdd = createStyledButton("[+] Tambah", SUCCESS_COLOR);
        btnAdd.setPreferredSize(new Dimension(130, 40));
        btnAdd.addActionListener(e -> addProduct());

        btnEdit = createStyledButton("[*] Edit", WARNING_COLOR);
        btnEdit.setPreferredSize(new Dimension(130, 40));
        btnEdit.addActionListener(e -> updateProduct());

        btnDelete = createStyledButton("[X] Hapus", DANGER_COLOR);
        btnDelete.setPreferredSize(new Dimension(130, 40));
        btnDelete.addActionListener(e -> deleteProduct());

        btnClear = createStyledButton("Clear", new Color(149, 165, 166));
        btnClear.setPreferredSize(new Dimension(130, 40));
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(formPanel, BorderLayout.CENTER);
        formCard.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(formCard, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return field;
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
        clearForm();
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

    private void loadSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            txtKode.setText(table.getValueAt(selectedRow, 1).toString());
            txtNama.setText(table.getValueAt(selectedRow, 2).toString());
            String hargaStr = table.getValueAt(selectedRow, 3).toString().replace("Rp ", "").replace(".", "");
            txtHarga.setText(hargaStr);
            txtStok.setText(table.getValueAt(selectedRow, 4).toString());
        }
    }

    private void addProduct() {
        try {
            String nama = txtNama.getText().trim();
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama produk harus diisi!");
                return;
            }

            // Generate kode produk otomatis
            String kode = productDAO.getNextProductCode();

            Product product = new Product(kode, nama, harga, stok);
            if (productDAO.addProduct(product)) {
                JOptionPane.showMessageDialog(this,
                        "Produk berhasil ditambahkan!\nKode Produk: " + kode,
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan produk!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!");
        }
    }

    private void updateProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan diedit!");
            return;
        }

        try {
            int id = (int) table.getValueAt(selectedRow, 0);
            String kode = txtKode.getText().trim();
            String nama = txtNama.getText().trim();
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());

            Product product = new Product(id, kode, nama, harga, stok);
            if (productDAO.updateProduct(product)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil diupdate!");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate produk!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!");
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus produk ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(selectedRow, 0);

            if (productDAO.deleteProduct(id)) {
                JOptionPane.showMessageDialog(this,
                        "Produk berhasil dihapus!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus produk!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtKode.setText("(Auto-generated)");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        txtSearch.setText("");
        table.clearSelection();
    }

    public void refresh() {
        loadProducts();
    }
}
