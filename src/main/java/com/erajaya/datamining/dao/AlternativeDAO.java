package com.erajaya.datamining.dao;

import com.erajaya.datamining.config.DatabaseConfig;
import com.erajaya.datamining.model.Alternative;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk Alternative
 */
public class AlternativeDAO {
    
    /**
     * Mendapatkan semua alternatif
     * @return List of alternatives
     */
    public List<Alternative> findAll() {
        List<Alternative> alternatives = new ArrayList<>();
        String sql = "SELECT * FROM alternatives ORDER BY code";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                alternatives.add(mapResultSetToAlternative(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua alternatif: " + e.getMessage());
        }
        
        return alternatives;
    }
    
    /**
     * Mendapatkan alternatif berdasarkan ID
     * @param id ID alternatif
     * @return Alternative object atau null
     */
    public Alternative findById(int id) {
        String sql = "SELECT * FROM alternatives WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAlternative(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mencari alternatif by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Mendapatkan alternatif berdasarkan code
     * @param code Code alternatif
     * @return Alternative object atau null
     */
    public Alternative findByCode(String code) {
        String sql = "SELECT * FROM alternatives WHERE code = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAlternative(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat mencari alternatif by code: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Menyimpan alternatif baru
     * @param alternative Alternative object
     * @return true jika berhasil
     */
    public boolean save(Alternative alternative) {
        String sql = "INSERT INTO alternatives (code, name, price, quality_score, spare_parts_score, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, alternative.getCode());
            stmt.setString(2, alternative.getName());
            stmt.setBigDecimal(3, alternative.getPrice());
            stmt.setInt(4, alternative.getQualityScore());
            stmt.setInt(5, alternative.getSparePartsScore());
            stmt.setString(6, alternative.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        alternative.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan alternatif: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update alternatif
     * @param alternative Alternative object
     * @return true jika berhasil
     */
    public boolean update(Alternative alternative) {
        String sql = "UPDATE alternatives SET code = ?, name = ?, price = ?, quality_score = ?, " +
                    "spare_parts_score = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, alternative.getCode());
            stmt.setString(2, alternative.getName());
            stmt.setBigDecimal(3, alternative.getPrice());
            stmt.setInt(4, alternative.getQualityScore());
            stmt.setInt(5, alternative.getSparePartsScore());
            stmt.setString(6, alternative.getDescription());
            stmt.setInt(7, alternative.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat update alternatif: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Hapus alternatif
     * @param id ID alternatif
     * @return true jika berhasil
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM alternatives WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus alternatif: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Cek apakah code sudah ada
     * @param code Code alternatif
     * @param excludeId ID yang dikecualikan (untuk update)
     * @return true jika code sudah ada
     */
    public boolean isCodeExists(String code, int excludeId) {
        String sql = "SELECT COUNT(*) FROM alternatives WHERE code = ? AND id != ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat cek code exists: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Overload untuk cek code exists tanpa exclude ID
     */
    public boolean isCodeExists(String code) {
        return isCodeExists(code, -1);
    }
    
    /**
     * Mendapatkan alternatif dengan hasil SAW
     * @return List of alternatives dengan total score dan ranking
     */
    public List<Alternative> findAllWithSAWResults() {
        List<Alternative> alternatives = new ArrayList<>();
        String sql = "SELECT a.*, sr.total_score, sr.ranking " +
                    "FROM alternatives a " +
                    "LEFT JOIN saw_results sr ON a.id = sr.alternative_id " +
                    "ORDER BY sr.ranking ASC, a.code ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Alternative alt = mapResultSetToAlternative(rs);
                
                // Set SAW results jika ada
                BigDecimal totalScore = rs.getBigDecimal("total_score");
                int ranking = rs.getInt("ranking");
                
                if (totalScore != null) {
                    alt.setTotalScore(totalScore);
                    alt.setRanking(ranking);
                }
                
                alternatives.add(alt);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil alternatif dengan SAW results: " + e.getMessage());
        }
        
        return alternatives;
    }
    
    /**
     * Mendapatkan statistik alternatif
     * @return String array dengan statistik [total, avg_price, max_quality, max_spare_parts]
     */
    public String[] getStatistics() {
        String sql = "SELECT COUNT(*) as total, " +
                    "AVG(price) as avg_price, " +
                    "MAX(quality_score) as max_quality, " +
                    "MAX(spare_parts_score) as max_spare_parts " +
                    "FROM alternatives";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return new String[] {
                    String.valueOf(rs.getInt("total")),
                    String.format("%.2f", rs.getBigDecimal("avg_price")),
                    String.valueOf(rs.getInt("max_quality")),
                    String.valueOf(rs.getInt("max_spare_parts"))
                };
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil statistik: " + e.getMessage());
        }
        
        return new String[]{"0", "0.00", "0", "0"};
    }
    
    /**
     * Mapping ResultSet ke Alternative object
     * @param rs ResultSet
     * @return Alternative object
     * @throws SQLException
     */
    private Alternative mapResultSetToAlternative(ResultSet rs) throws SQLException {
        Alternative alt = new Alternative();
        alt.setId(rs.getInt("id"));
        alt.setCode(rs.getString("code"));
        alt.setName(rs.getString("name"));
        alt.setPrice(rs.getBigDecimal("price"));
        alt.setQualityScore(rs.getInt("quality_score"));
        alt.setSparePartsScore(rs.getInt("spare_parts_score"));
        alt.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            alt.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            alt.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return alt;
    }
}