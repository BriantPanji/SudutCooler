package gui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SECONDARY_COLOR = new Color(155, 89, 182);
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    public LoginFrame() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Sudut Cooler - Sistem Kasir");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, w, h, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BorderLayout(20, 20));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));
        loginCard.setPreferredSize(new Dimension(450, 520));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblIcon = new JLabel("[SC]");
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblIcon.setForeground(PRIMARY_COLOR);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("SUDUT COOLER");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistem Kasir Es Krim");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(TEXT_DARK);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblIcon);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(TEXT_DARK);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(TEXT_DARK);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        formPanel.add(lblUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(lblPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);

        btnLogin = new JButton("MASUK");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setPreferredSize(new Dimension(350, 50));
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(PRIMARY_COLOR);
            }
        });

        buttonPanel.add(btnLogin, BorderLayout.CENTER);

        loginCard.add(headerPanel, BorderLayout.NORTH);
        loginCard.add(formPanel, BorderLayout.CENTER);
        loginCard.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(loginCard);
        add(mainPanel);

        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan password tidak boleh kosong!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Login berhasil! Selamat datang, " + user.getNama(),
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);

            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }
}
