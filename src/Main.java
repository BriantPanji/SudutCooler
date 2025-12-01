import database.DatabaseConnection;
import gui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Inisialisasi database
        System.out.println("=== APLIKASI KASIR ===");
        System.out.println("Menginisialisasi database...");
        DatabaseConnection.initialize();
        
        // Jalankan GUI di Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
