package com.erajaya.datamining.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model untuk hasil perhitungan SAW
 */
public class SAWResult {
    private int id;
    private int alternativeId;
    private Alternative alternative;
    private BigDecimal totalScore;
    private int ranking;
    private LocalDateTime calculationDate;
    
    // Untuk detail perhitungan
    private BigDecimal[] normalizedValues;
    private BigDecimal[] weightedValues;
    
    // Constructors
    public SAWResult() {}
    
    public SAWResult(int alternativeId, BigDecimal totalScore, int ranking) {
        this.alternativeId = alternativeId;
        this.totalScore = totalScore;
        this.ranking = ranking;
    }
    
    public SAWResult(Alternative alternative, BigDecimal totalScore, int ranking) {
        this.alternative = alternative;
        this.alternativeId = alternative.getId();
        this.totalScore = totalScore;
        this.ranking = ranking;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAlternativeId() {
        return alternativeId;
    }
    
    public void setAlternativeId(int alternativeId) {
        this.alternativeId = alternativeId;
    }
    
    public Alternative getAlternative() {
        return alternative;
    }
    
    public void setAlternative(Alternative alternative) {
        this.alternative = alternative;
        if (alternative != null) {
            this.alternativeId = alternative.getId();
        }
    }
    
    public BigDecimal getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
    
    public int getRanking() {
        return ranking;
    }
    
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
    
    public LocalDateTime getCalculationDate() {
        return calculationDate;
    }
    
    public void setCalculationDate(LocalDateTime calculationDate) {
        this.calculationDate = calculationDate;
    }
    
    public BigDecimal[] getNormalizedValues() {
        return normalizedValues;
    }
    
    public void setNormalizedValues(BigDecimal[] normalizedValues) {
        this.normalizedValues = normalizedValues;
    }
    
    public BigDecimal[] getWeightedValues() {
        return weightedValues;
    }
    
    public void setWeightedValues(BigDecimal[] weightedValues) {
        this.weightedValues = weightedValues;
    }
    
    // Helper methods
    public double getTotalScoreAsDouble() {
        return totalScore.doubleValue();
    }
    
    public String getFormattedScore() {
        return String.format("%.4f", totalScore);
    }
    
    public String getScorePercentage() {
        return String.format("%.2f%%", totalScore.doubleValue() * 100);
    }
    
    public String getRankingDescription() {
        switch (ranking) {
            case 1: return "Terbaik";
            case 2: return "Sangat Baik";
            case 3: return "Baik";
            case 4: return "Cukup";
            case 5: return "Kurang";
            default: return "Peringkat " + ranking;
        }
    }
    
    public String getAlternativeName() {
        return alternative != null ? alternative.getName() : "Unknown";
    }
    
    public String getAlternativeCode() {
        return alternative != null ? alternative.getCode() : "Unknown";
    }
    
    @Override
    public String toString() {
        return "SAWResult{" +
                "id=" + id +
                ", alternativeId=" + alternativeId +
                ", totalScore=" + totalScore +
                ", ranking=" + ranking +
                ", calculationDate=" + calculationDate +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SAWResult sawResult = (SAWResult) obj;
        return id == sawResult.id && alternativeId == sawResult.alternativeId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(alternativeId);
    }
}