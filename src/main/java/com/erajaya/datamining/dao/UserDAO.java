package com.erajaya.datamining.dao;

import com.erajaya.datamining.config.DatabaseConfig;
import com.erajaya.datamining.model.User;
import com.erajaya.datamining.model.User.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk User
 */
public class UserDAO {
    
    /**
     * Authenticate user dengan username dan password
     * @param username Username
     * @param password Password
     * @return User object jika berhasil, null jika gagal
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat authenticating user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Mendapatkan user berdasarkan ID
     * @param id ID user
     * @return User object atau null
     */
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mencari user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Mendapatkan user berdasarkan username
     * @param username Username
     * @return User object atau null
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mencari user by username: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Mendapatkan semua user
     * @return List of users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua user: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Menyimpan user baru
     * @param user User object
     * @return true jika berhasil
     */
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole().getValue());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update user
     * @param user User object
     * @return true jika berhasil
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, role = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole().getValue());
            stmt.setInt(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat update user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Hapus user
     * @param id ID user
     * @return true jika berhasil
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus user: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Cek apakah username sudah ada
     * @param username Username
     * @param excludeId ID yang dikecualikan (untuk update)
     * @return true jika username sudah ada
     */
    public boolean isUsernameExists(String username, int excludeId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat cek username exists: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Overload untuk cek username exists tanpa exclude ID
     */
    public boolean isUsernameExists(String username) {
        return isUsernameExists(username, -1);
    }
    
    /**
     * Mapping ResultSet ke User object
     * @param rs ResultSet
     * @return User object
     * @throws SQLException
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(UserRole.fromString(rs.getString("role")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}