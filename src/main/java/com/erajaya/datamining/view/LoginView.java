package com.erajaya.datamining.view;

import com.erajaya.datamining.controller.LoginController;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * View untuk Login
 */
public class LoginView extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;
    private LoginController controller;
    
    public LoginView() {
        controller = new LoginController(this);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultValues();
    }
    
    private void initializeComponents() {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Frame properties
        setTitle("Login - Sistem Data Mining SAW PT Erajaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(450, 300);
        setLocationRelativeTo(null);
        
        // Components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        exitButton = new JButton("Keluar");
        statusLabel = new JLabel(" ");
        
        // Styling
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("SISTEM DATA MINING SAW");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("PT ERAJAYA - PENGELOLAAN BARANG ELEKTRONIK");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.setLayout(new GridLayout(2, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        mainPanel.add(statusLabel, gbc);
        
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel footerLabel = new JLabel("© 2025 PT Erajaya - Data Mining System");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerPanel.add(footerLabel);
        
        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Exit button action
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Enter key listener
        KeyListener enterListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
            
            @Override
            public void keyTyped(KeyEvent e) {}
        };
        
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);
    }
    
    private void setDefaultValues() {
        // Set default login values untuk testing
        usernameField.setText("admin");
        passwordField.setText("admin123");
        
        // Focus pada username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Username dan password harus diisi!", Color.RED);
            return;
        }
        
        // Disable button sementara
        loginButton.setEnabled(false);
        loginButton.setText("Memproses...");
        
        // Call controller
        controller.authenticate(username, password);
    }
    
    public void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
        
        // Reset button
        loginButton.setEnabled(true);
        loginButton.setText("Login");
    }
    
    public void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        usernameField.requestFocus();
    }
    
    /**
     * Show login dialog dengan informasi akun default
     */
    public void showDefaultAccounts() {
        String message = "Akun Default untuk Testing:\n\n" +
                        "1. Admin:\n" +
                        "   Username: admin\n" +
                        "   Password: admin123\n\n" +
                        "2. Manager:\n" +
                        "   Username: manager\n" +
                        "   Password: manager123\n\n" +
                        "3. Staff:\n" +
                        "   Username: staff\n" +
                        "   Password: staff123";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Informasi Login",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Main method untuk menjalankan aplikasi
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(new FlatLightLaf());
                
                // Create and show login
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                
                // Show default accounts info
                Timer timer = new Timer(1000, e -> {
                    loginView.showDefaultAccounts();
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Error saat memulai aplikasi: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}