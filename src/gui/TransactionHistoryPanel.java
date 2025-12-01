package gui;

import dao.TransactionDAO;
import model.Transaction;
import model.TransactionItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryPanel extends JPanel {
    private JTable tableTransactions;
    private DefaultTableModel transactionModel;
    private JButton btnRefresh, btnViewDetail;
    private TransactionDAO transactionDAO;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public TransactionHistoryPanel() {
        transactionDAO = new TransactionDAO();
        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel with modern styling
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel("Riwayat Transaksi Penjualan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        topPanel.add(lblTitle);

        topPanel.add(Box.createHorizontalStrut(30));

        btnRefresh = createStyledButton("Refresh", PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> loadTransactions());
        topPanel.add(btnRefresh);

        btnViewDetail = createStyledButton("Lihat Detail", new Color(46, 204, 113));
        btnViewDetail.addActionListener(e -> viewDetail());
        topPanel.add(btnViewDetail);

        // Table Panel with modern styling
        String[] columns = { "ID", "Tanggal & Waktu", "Total", "Kasir" };
        transactionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTransactions = new JTable(transactionModel);
        tableTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTransactions.setRowHeight(32);
        tableTransactions.setSelectionBackground(new Color(52, 152, 219, 50));
        tableTransactions.setSelectionForeground(TEXT_DARK);

        // Style table header - FIX VISIBILITY
        JTableHeader header = tableTransactions.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.BLACK); // WHITE TEXT FOR VISIBILITY
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tableTransactions);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));

        // Add to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 38));

        // Hover effect
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

    private void loadTransactions() {
        transactionModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Transaction t : transactions) {
            Object[] row = {
                    t.getId(),
                    t.getTanggal().format(formatter),
                    String.format("Rp %.0f", t.getTotal()),
                    t.getKasir()
            };
            transactionModel.addRow(row);
        }
    }

    private void viewDetail() {
        int selectedRow = tableTransactions.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih transaksi terlebih dahulu!");
            return;
        }

        int transactionId = (int) tableTransactions.getValueAt(selectedRow, 0);
        Transaction transaction = transactionDAO.getTransactionById(transactionId);
        List<TransactionItem> items = transactionDAO.getTransactionItems(transactionId);

        // Buat dialog untuk menampilkan detail
        JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Detail Transaksi #" + transactionId, true);
        detailDialog.setSize(700, 500);
        detailDialog.setLocationRelativeTo(this);

        JPanel detailPanel = new JPanel(new BorderLayout(15, 15));
        detailPanel.setBackground(BG_COLOR);
        detailPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info Panel with modern card style
        JPanel infoCard = new JPanel(new GridLayout(4, 2, 15, 12));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss");

        infoCard.add(createInfoLabel("ID Transaksi:"));
        infoCard.add(createValueLabel("#" + transaction.getId()));

        infoCard.add(createInfoLabel("Tanggal & Waktu:"));
        infoCard.add(createValueLabel(transaction.getTanggal().format(formatter)));

        infoCard.add(createInfoLabel("Kasir:"));
        infoCard.add(createValueLabel(transaction.getKasir()));

        infoCard.add(createInfoLabel("Total Pembayaran:"));
        JLabel lblDetailTotal = new JLabel(String.format("Rp %.0f", transaction.getTotal()));
        lblDetailTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDetailTotal.setForeground(new Color(46, 204, 113));
        infoCard.add(lblDetailTotal);

        // Items Table with modern styling
        String[] columns = { "Produk", "Harga Satuan", "Jumlah", "Subtotal" };
        DefaultTableModel itemModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (TransactionItem item : items) {
            Object[] row = {
                    item.getProductName(),
                    String.format("Rp %.0f", item.getProductPrice()),
                    item.getQuantity(),
                    String.format("Rp %.0f", item.getSubtotal())
            };
            itemModel.addRow(row);
        }

        JTable itemTable = new JTable(itemModel);
        itemTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemTable.setRowHeight(30);
        itemTable.setSelectionBackground(new Color(52, 152, 219, 50));

        JTableHeader itemHeader = itemTable.getTableHeader();
        itemHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemHeader.setBackground(PRIMARY_COLOR);
        itemHeader.setForeground(Color.BLACK); // WHITE TEXT FOR VISIBILITY
        itemHeader.setPreferredSize(new Dimension(0, 38));

        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_COLOR);

        JButton btnClose = createStyledButton("Tutup", new Color(149, 165, 166));
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(btnClose);

        detailPanel.add(infoCard, BorderLayout.NORTH);
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailDialog.add(detailPanel);
        detailDialog.setVisible(true);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }

    public void refresh() {
        loadTransactions();
    }
}
