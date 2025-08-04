package com.erajaya.datamining.controller;

import com.erajaya.datamining.dao.UserDAO;
import com.erajaya.datamining.model.User;
import com.erajaya.datamining.view.DashboardView;
import com.erajaya.datamining.view.LoginView;

import javax.swing.*;
import java.awt.*;

/**
 * Controller untuk Login
 */
public class LoginController {
    
    private LoginView loginView;
    private UserDAO userDAO;
    
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();
    }
    
    /**
     * Melakukan autentikasi user
     * @param username Username
     * @param password Password
     */
    public void authenticate(String username, String password) {
        try {
            // Validasi input
            if (username.trim().isEmpty()) {
                loginView.showStatus("Username tidak boleh kosong!", Color.RED);
                return;
            }
            
            if (password.trim().isEmpty()) {
                loginView.showStatus("Password tidak boleh kosong!", Color.RED);
                return;
            }
            
            // Cek koneksi database
            if (!testDatabaseConnection()) {
                loginView.showStatus("Koneksi database gagal! Periksa konfigurasi.", Color.RED);
                return;
            }
            
            // Lakukan autentikasi
            User user = userDAO.authenticate(username.trim(), password);
            
            if (user != null) {
                // Login berhasil
                loginView.showStatus("Login berhasil! Membuka dashboard...", Color.GREEN);
                
                // Simpan session user
                UserSession.setCurrentUser(user);
                
                // Delay sebentar untuk menampilkan pesan sukses
                Timer timer = new Timer(1000, e -> openDashboard(user));
                timer.setRepeats(false);
                timer.start();
                
            } else {
                // Login gagal
                loginView.showStatus("Username atau password salah!", Color.RED);
                loginView.clearForm();
            }
            
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            loginView.showStatus("Terjadi kesalahan sistem: " + e.getMessage(), Color.RED);
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    private boolean testDatabaseConnection() {
        try {
            return com.erajaya.datamining.config.DatabaseConfig.testConnection();
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Membuka dashboard setelah login berhasil
     * @param user User yang login
     */
    private void openDashboard(User user) {
        try {
            // Tutup login window
            loginView.dispose();
            
            // Buka dashboard
            SwingUtilities.invokeLater(() -> {
                DashboardView dashboard = new DashboardView(user);
                dashboard.setVisible(true);
            });
            
        } catch (Exception e) {
            System.err.println("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Jika gagal buka dashboard, tampilkan pesan error dan kembalikan ke login
            JOptionPane.showMessageDialog(
                loginView,
                "Gagal membuka dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            
            // Reset login form
            loginView.setVisible(true);
            loginView.clearForm();
        }
    }
    
    /**
     * Logout user dan kembali ke login
     */
    public static void logout() {
        // Clear session
        UserSession.clearSession();
        
        // Tutup semua window kecuali login
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isDisplayable() && !(window instanceof LoginView)) {
                window.dispose();
            }
        }
        
        // Buka login window baru
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
    
    /**
     * Class untuk menyimpan session user yang sedang login
     */
    public static class UserSession {
        private static User currentUser;
        
        public static void setCurrentUser(User user) {
            currentUser = user;
        }
        
        public static User getCurrentUser() {
            return currentUser;
        }
        
        public static boolean isLoggedIn() {
            return currentUser != null;
        }
        
        public static void clearSession() {
            currentUser = null;
        }
        
        public static String getCurrentUsername() {
            return currentUser != null ? currentUser.getUsername() : "Unknown";
        }
        
        public static String getCurrentFullName() {
            return currentUser != null ? currentUser.getFullName() : "Unknown User";
        }
        
        public static User.UserRole getCurrentRole() {
            return currentUser != null ? currentUser.getRole() : User.UserRole.STAFF;
        }
        
        public static boolean hasPermission(String permission) {
            return currentUser != null && currentUser.hasPermission(permission);
        }
    }
}