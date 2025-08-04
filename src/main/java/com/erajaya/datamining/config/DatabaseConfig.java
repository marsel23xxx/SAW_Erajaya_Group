package com.erajaya.datamining.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Konfigurasi koneksi database MySQL
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "/database.properties";
    private static Properties props = new Properties();
    
    // Default configuration
    private static String URL = "jdbc:mysql://localhost:3306/erajaya_datamining";
    private static String USERNAME = "root";
    private static String PASSWORD = "";
    private static String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    static {
        loadConfig();
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver tidak ditemukan", e);
        }
    }
    
    private static void loadConfig() {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
                URL = props.getProperty("db.url", URL);
                USERNAME = props.getProperty("db.username", USERNAME);
                PASSWORD = props.getProperty("db.password", PASSWORD);
                DRIVER = props.getProperty("db.driver", DRIVER);
            }
        } catch (IOException e) {
            System.out.println("Menggunakan konfigurasi default database");
        }
    }
    
    /**
     * Mendapatkan koneksi database
     * @return Connection object
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    
    /**
     * Test koneksi database
     * @return true jika koneksi berhasil
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Gagal koneksi ke database: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Menutup koneksi database
     * @param conn Connection yang akan ditutup
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi: " + e.getMessage());
            }
        }
    }
    
    // Getters untuk konfigurasi
    public static String getUrl() { return URL; }
    public static String getUsername() { return USERNAME; }
    public static String getDriver() { return DRIVER; }
}