package com.erajaya.datamining;

import com.erajaya.datamining.config.DatabaseConfig;
import com.erajaya.datamining.view.LoginView;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main class untuk menjalankan aplikasi Data Mining SAW PT Erajaya
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system properties
        System.setProperty("java.awt.headless", "false");
        
        // Initialize Look and Feel
        initializeLookAndFeel();
        
        // Check database connection
        if (!checkDatabaseConnection()) {
            showDatabaseError();
            System.exit(1);
            return;
        }
        
        // Start application
        SwingUtilities.invokeLater(() -> {
            try {
                startApplication();
            } catch (Exception e) {
                e.printStackTrace();
                showStartupError(e);
                System.exit(1);
            }
        });
    }
    
    /**
     * Initialize Look and Feel
     */
    private static void initializeLookAndFeel() {
        try {
            // Set FlatLaf Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Set additional UI properties
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            
            // Set font scaling
            System.setProperty("flatlaf.uiScale", "1.0");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize Look and Feel: " + e.getMessage());            
        }
    }
    
    /**
     * Check database connection
     * @return true if connection successful
     */
    private static boolean checkDatabaseConnection() {
        try {
            System.out.println("Checking database connection...");
            boolean connected = DatabaseConfig.testConnection();
            
            if (connected) {
                System.out.println("Database connection successful");
                return true;
            } else {
                System.err.println("Database connection failed");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Start the main application
     */
    private static void startApplication() {
        // Show splash screen (optional)
        showSplashScreen();
        
        // Create and show login window
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
        
        System.out.println("Application started successfully");
    }
    
    /**
     * Show splash screen for a brief moment
     */
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(400, 300);
        splash.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setBackground(new java.awt.Color(25, 25, 112));
        panel.setLayout(new java.awt.BorderLayout());
        
        JLabel titleLabel = new JLabel("SISTEM DATA MINING SAW", SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        titleLabel.setForeground(java.awt.Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("PT ERAJAYA", SwingConstants.CENTER);
        subtitleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        subtitleLabel.setForeground(java.awt.Color.LIGHT_GRAY);
        
        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        loadingLabel.setForeground(java.awt.Color.WHITE);
        
        panel.add(titleLabel, java.awt.BorderLayout.NORTH);
        panel.add(subtitleLabel, java.awt.BorderLayout.CENTER);
        panel.add(loadingLabel, java.awt.BorderLayout.SOUTH);
        
        splash.add(panel);
        splash.setVisible(true);
        
        // Show splash for 2 seconds
        Timer timer = new Timer(2000, e -> splash.dispose());
        timer.setRepeats(false);
        timer.start();
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Show database connection error dialog
     */
    private static void showDatabaseError() {
        String message = "Tidak dapat terhubung ke database!\n\n" +
                        "Pastikan:\n" +
                        "1. MySQL server berjalan\n" +
                        "2. Database 'erajaya_datamining' sudah dibuat\n" +
                        "3. Konfigurasi koneksi database benar\n" +
                        "4. Username dan password database sesuai\n\n" +
                        "Periksa file konfigurasi database dan coba lagi.";
        
        JOptionPane.showMessageDialog(
            null,
            message,
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show startup error dialog
     * @param e Exception that occurred
     */
    private static void showStartupError(Exception e) {
        String message = "Terjadi kesalahan saat memulai aplikasi:\n\n" +
                        e.getMessage() + "\n\n" +
                        "Silakan coba restart aplikasi atau hubungi administrator.";
        
        JOptionPane.showMessageDialog(
            null,
            message,
            "Startup Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show application information
     */
    public static void showApplicationInfo() {
        String info = "=== SISTEM DATA MINING SAW PT ERAJAYA ===\n" +
                     "Version: 1.0\n" +
                     "Build Date: " + java.time.LocalDate.now() + "\n" +
                     "Java Version: " + System.getProperty("java.version") + "\n" +
                     "OS: " + System.getProperty("os.name") + "\n" +
                     "User: " + System.getProperty("user.name") + "\n" +
                     "========================================";
        
        System.out.println(info);
    }
    
    /**
     * Shutdown hook untuk cleanup
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Application shutting down...");
            // Cleanup code here if needed
            System.out.println("Application shutdown complete");
        }));
    }
}