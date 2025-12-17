package gui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private CashierPanel cashierPanel;
    private ProductPanel productPanel;
    private TransactionHistoryPanel historyPanel;
    private StockPanel stockPanel;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(155, 89, 182);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Sudut Cooler - Sistem Kasir");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        ///gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        //header kiri
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JLabel lblLogo = new JLabel("[SC]");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("SUDUT COOLER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Sistem Kasir Es Krim");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        leftHeader.add(lblLogo);
        leftHeader.add(titlePanel);

        //header kanan
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightHeader.setOpaque(false);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);

        JLabel lblUserGreet = new JLabel("Kasir:");
        lblUserGreet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUserGreet.setForeground(new Color(255, 255, 255, 180));
        lblUserGreet.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblUserName = new JLabel(currentUser.getNama());
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userPanel.add(lblUserGreet);
        userPanel.add(lblUserName);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setBackground(DANGER_COLOR);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(100, 40));
        btnLogout.addActionListener(e -> logout());

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(DANGER_COLOR);
            }
        });

        rightHeader.add(userPanel);
        rightHeader.add(btnLogout);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        //konten
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(TEXT_DARK);

        cashierPanel = new CashierPanel(currentUser);
        productPanel = new ProductPanel();
        historyPanel = new TransactionHistoryPanel();
        stockPanel = new StockPanel();

        tabbedPane.addTab("  KASIR  ", cashierPanel);
        tabbedPane.addTab("  PRODUK  ", productPanel);
        tabbedPane.addTab("  RIWAYAT  ", historyPanel);
        tabbedPane.addTab("  STOK  ", stockPanel);

        tabbedPane.setTabPlacement(JTabbedPane.TOP);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    cashierPanel.refresh();
                    break;
                case 1:
                    productPanel.refresh();
                    break;
                case 2:
                    historyPanel.refresh();
                    break;
                case 3:
                    stockPanel.refresh();
                    break;
            }
        });

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
}
