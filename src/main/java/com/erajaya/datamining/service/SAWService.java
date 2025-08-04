package com.erajaya.datamining.service;

import com.erajaya.datamining.config.DatabaseConfig;
import com.erajaya.datamining.dao.AlternativeDAO;
import com.erajaya.datamining.model.Alternative;
import com.erajaya.datamining.model.SAWResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Service untuk perhitungan algoritma Simple Additive Weighting (SAW)
 */
public class SAWService {
    
    private final AlternativeDAO alternativeDAO;
    
    // Bobot kriteria: Harga (0.4), Kualitas (0.35), Suku Cadang (0.25)
    private static final double[] WEIGHTS = {0.40, 0.35, 0.25};
    private static final String[] CRITERIA_NAMES = {"Harga", "Kualitas", "Suku Cadang"};
    private static final boolean[] IS_BENEFIT = {false, true, true}; // Harga = cost, lainnya = benefit
    
    public SAWService() {
        this.alternativeDAO = new AlternativeDAO();
    }
    
    /**
     * Menghitung SAW untuk semua alternatif
     * @return List hasil SAW yang sudah diurutkan berdasarkan ranking
     */
    public List<SAWResult> calculateSAW() {
        // Ambil semua alternatif
        List<Alternative> alternatives = alternativeDAO.findAll();
        
        if (alternatives.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 1. Buat matriks keputusan
        double[][] decisionMatrix = createDecisionMatrix(alternatives);
        
        // 2. Normalisasi matriks
        double[][] normalizedMatrix = normalizeMatrix(decisionMatrix);
        
        // 3. Hitung nilai preferensi
        List<SAWResult> results = calculatePreferenceValues(alternatives, normalizedMatrix);
        
        // 4. Urutkan berdasarkan nilai preferensi (descending)
        results.sort((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()));
        
        // 5. Set ranking
        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRanking(i + 1);
        }
        
        // 6. Simpan hasil ke database
        saveSAWResults(results);
        
        return results;
    }
    
    /**
     * Membuat matriks keputusan dari alternatif
     * @param alternatives List alternatif
     * @return Matriks keputusan [alternatif][kriteria]
     */
    private double[][] createDecisionMatrix(List<Alternative> alternatives) {
        int numAlternatives = alternatives.size();
        int numCriteria = 3; // Harga, Kualitas, Suku Cadang
        
        double[][] matrix = new double[numAlternatives][numCriteria];
        
        for (int i = 0; i < numAlternatives; i++) {
            Alternative alt = alternatives.get(i);
            matrix[i][0] = alt.getPriceAsDouble();        // Harga
            matrix[i][1] = alt.getQualityScore();         // Kualitas
            matrix[i][2] = alt.getSparePartsScore();      // Suku Cadang
        }
        
        return matrix;
    }
    
    /**
     * Normalisasi matriks keputusan
     * @param decisionMatrix Matriks keputusan
     * @return Matriks yang sudah dinormalisasi
     */
    private double[][] normalizeMatrix(double[][] decisionMatrix) {
        int numAlternatives = decisionMatrix.length;
        int numCriteria = decisionMatrix[0].length;
        
        double[][] normalizedMatrix = new double[numAlternatives][numCriteria];
        
        // Untuk setiap kriteria
        for (int j = 0; j < numCriteria; j++) {
            if (IS_BENEFIT[j]) {
                // Kriteria benefit: nilai dibagi dengan nilai maksimum
                double maxValue = findMaxInColumn(decisionMatrix, j);
                for (int i = 0; i < numAlternatives; i++) {
                    normalizedMatrix[i][j] = decisionMatrix[i][j] / maxValue;
                }
            } else {
                // Kriteria cost: nilai minimum dibagi dengan nilai
                double minValue = findMinInColumn(decisionMatrix, j);
                for (int i = 0; i < numAlternatives; i++) {
                    normalizedMatrix[i][j] = minValue / decisionMatrix[i][j];
                }
            }
        }
        
        return normalizedMatrix;
    }
    
    /**
     * Mencari nilai maksimum dalam kolom
     */
    private double findMaxInColumn(double[][] matrix, int column) {
        double max = matrix[0][column];
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i][column] > max) {
                max = matrix[i][column];
            }
        }
        return max;
    }
    
    /**
     * Mencari nilai minimum dalam kolom
     */
    private double findMinInColumn(double[][] matrix, int column) {
        double min = matrix[0][column];
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i][column] < min) {
                min = matrix[i][column];
            }
        }
        return min;
    }
    
    /**
     * Menghitung nilai preferensi untuk setiap alternatif
     * @param alternatives List alternatif
     * @param normalizedMatrix Matriks yang sudah dinormalisasi
     * @return List hasil SAW
     */
    private List<SAWResult> calculatePreferenceValues(List<Alternative> alternatives, 
                                                     double[][] normalizedMatrix) {
        List<SAWResult> results = new ArrayList<>();
        
        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alt = alternatives.get(i);
            
            // Hitung nilai preferensi: sum(wi * rij)
            double preferenceValue = 0.0;
            for (int j = 0; j < normalizedMatrix[i].length; j++) {
                preferenceValue += WEIGHTS[j] * normalizedMatrix[i][j];
            }
            
            // Buat SAW result
            SAWResult result = new SAWResult(alt, 
                BigDecimal.valueOf(preferenceValue).setScale(4, RoundingMode.HALF_UP), 
                0); // ranking akan diset nanti
            
            // Set detail perhitungan
            BigDecimal[] normalizedValues = new BigDecimal[normalizedMatrix[i].length];
            BigDecimal[] weightedValues = new BigDecimal[normalizedMatrix[i].length];
            
            for (int j = 0; j < normalizedMatrix[i].length; j++) {
                normalizedValues[j] = BigDecimal.valueOf(normalizedMatrix[i][j])
                    .setScale(4, RoundingMode.HALF_UP);
                weightedValues[j] = BigDecimal.valueOf(WEIGHTS[j] * normalizedMatrix[i][j])
                    .setScale(4, RoundingMode.HALF_UP);
            }
            
            result.setNormalizedValues(normalizedValues);
            result.setWeightedValues(weightedValues);
            
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * Menyimpan hasil SAW ke database
     * @param results List hasil SAW
     */
    private void saveSAWResults(List<SAWResult> results) {
        String deleteSql = "DELETE FROM saw_results";
        String insertSql = "INSERT INTO saw_results (alternative_id, total_score, ranking) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Hapus hasil lama
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.executeUpdate();
            }
            
            // Insert hasil baru
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (SAWResult result : results) {
                    insertStmt.setInt(1, result.getAlternativeId());
                    insertStmt.setBigDecimal(2, result.getTotalScore());
                    insertStmt.setInt(3, result.getRanking());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan hasil SAW: " + e.getMessage());
        }
    }
    
    /**
     * Mendapatkan detail perhitungan SAW untuk laporan
     * @return Map berisi detail perhitungan
     */
    public Map<String, Object> getSAWCalculationDetails() {
        List<Alternative> alternatives = alternativeDAO.findAll();
        
        if (alternatives.isEmpty()) {
            return new HashMap<>();
        }
        
        double[][] decisionMatrix = createDecisionMatrix(alternatives);
        double[][] normalizedMatrix = normalizeMatrix(decisionMatrix);
        List<SAWResult> results = calculatePreferenceValues(alternatives, normalizedMatrix);
        
        Map<String, Object> details = new HashMap<>();
        details.put("alternatives", alternatives);
        details.put("decisionMatrix", decisionMatrix);
        details.put("normalizedMatrix", normalizedMatrix);
        details.put("results", results);
        details.put("criteriaNames", CRITERIA_NAMES);
        details.put("weights", WEIGHTS);
        details.put("isBenefit", IS_BENEFIT);
        
        return details;
    }
    
    /**
     * Mendapatkan matriks keputusan dalam format string untuk tampilan
     * @return String array untuk tabel
     */
    public String[][] getDecisionMatrixDisplay() {
        List<Alternative> alternatives = alternativeDAO.findAll();
        
        if (alternatives.isEmpty()) {
            return new String[0][0];
        }
        
        double[][] matrix = createDecisionMatrix(alternatives);
        String[][] display = new String[alternatives.size()][4]; // Code + 3 kriteria
        
        for (int i = 0; i < alternatives.size(); i++) {
            display[i][0] = alternatives.get(i).getCode();
            display[i][1] = String.format("%.2f", matrix[i][0]); // Harga
            display[i][2] = String.format("%.0f", matrix[i][1]); // Kualitas
            display[i][3] = String.format("%.0f", matrix[i][2]); // Suku Cadang
        }
        
        return display;
    }
    
    /**
     * Mendapatkan matriks normalisasi dalam format string untuk tampilan
     * @return String array untuk tabel
     */
    public String[][] getNormalizedMatrixDisplay() {
        List<Alternative> alternatives = alternativeDAO.findAll();
        
        if (alternatives.isEmpty()) {
            return new String[0][0];
        }
        
        double[][] decisionMatrix = createDecisionMatrix(alternatives);
        double[][] normalizedMatrix = normalizeMatrix(decisionMatrix);
        String[][] display = new String[alternatives.size()][4]; // Code + 3 kriteria
        
        for (int i = 0; i < alternatives.size(); i++) {
            display[i][0] = alternatives.get(i).getCode();
            display[i][1] = String.format("%.4f", normalizedMatrix[i][0]);
            display[i][2] = String.format("%.4f", normalizedMatrix[i][1]);
            display[i][3] = String.format("%.4f", normalizedMatrix[i][2]);
        }
        
        return display;
    }
    
    /**
     * Mendapatkan hasil akhir SAW
     * @return List SAWResult yang sudah diurutkan
     */
    public List<SAWResult> getSAWResults() {
        return calculateSAW();
    }
    
    /**
     * Mendapatkan informasi kriteria
     * @return Map berisi informasi kriteria
     */
    public Map<String, Object> getCriteriaInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("names", CRITERIA_NAMES);
        info.put("weights", WEIGHTS);
        info.put("types", new String[]{"Cost", "Benefit", "Benefit"});
        info.put("descriptions", new String[]{
            "Harga produk (semakin rendah semakin baik)",
            "Skor kualitas produk (semakin tinggi semakin baik)",
            "Ketersediaan suku cadang (semakin tinggi semakin baik)"
        });
        return info;
    }
    
    /**
     * Validasi data sebelum perhitungan SAW
     * @return List pesan error, kosong jika valid
     */
    public List<String> validateData() {
        List<String> errors = new ArrayList<>();
        List<Alternative> alternatives = alternativeDAO.findAll();
        
        if (alternatives.isEmpty()) {
            errors.add("Tidak ada data alternatif untuk dihitung");
            return errors;
        }
        
        if (alternatives.size() < 2) {
            errors.add("Minimal 2 alternatif diperlukan untuk perhitungan SAW");
        }
        
        // Validasi data alternatif
        for (Alternative alt : alternatives) {
            if (alt.getPrice() == null || alt.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Harga alternatif " + alt.getCode() + " tidak valid");
            }
            
            if (alt.getQualityScore() < 1 || alt.getQualityScore() > 100) {
                errors.add("Skor kualitas alternatif " + alt.getCode() + " harus antara 1-100");
            }
            
            if (alt.getSparePartsScore() < 1 || alt.getSparePartsScore() > 100) {
                errors.add("Skor suku cadang alternatif " + alt.getCode() + " harus antara 1-100");
            }
        }
        
        // Validasi bobot (harus total = 1.0)
        double totalWeight = 0.0;
        for (double weight : WEIGHTS) {
            totalWeight += weight;
        }
        
        if (Math.abs(totalWeight - 1.0) > 0.001) {
            errors.add("Total bobot kriteria harus sama dengan 1.0 (saat ini: " + totalWeight + ")");
        }
        
        return errors;
    }
    
    /**
     * Mendapatkan statistik hasil SAW
     * @return Map berisi statistik
     */
    public Map<String, Object> getSAWStatistics() {
        List<SAWResult> results = getSAWResults();
        Map<String, Object> stats = new HashMap<>();
        
        if (results.isEmpty()) {
            return stats;
        }
        
        double totalScore = 0.0;
        double maxScore = results.get(0).getTotalScoreAsDouble();
        double minScore = results.get(results.size() - 1).getTotalScoreAsDouble();
        
        for (SAWResult result : results) {
            totalScore += result.getTotalScoreAsDouble();
        }
        
        double avgScore = totalScore / results.size();
        
        stats.put("totalAlternatives", results.size());
        stats.put("maxScore", maxScore);
        stats.put("minScore", minScore);
        stats.put("avgScore", avgScore);
        stats.put("bestAlternative", results.get(0).getAlternativeName());
        stats.put("worstAlternative", results.get(results.size() - 1).getAlternativeName());
        
        return stats;
    }
}