package com.erajaya.datamining.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model untuk Alternative (Produk Elektronik)
 */
public class Alternative {
    private int id;
    private String code;
    private String name;
    private BigDecimal price;
    private int qualityScore;
    private int sparePartsScore;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Untuk hasil SAW
    private BigDecimal totalScore;
    private int ranking;
    
    // Constructors
    public Alternative() {}
    
    public Alternative(String code, String name, BigDecimal price, 
                      int qualityScore, int sparePartsScore) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.qualityScore = qualityScore;
        this.sparePartsScore = sparePartsScore;
    }
    
    public Alternative(String code, String name, BigDecimal price, 
                      int qualityScore, int sparePartsScore, String description) {
        this(code, name, price, qualityScore, sparePartsScore);
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public int getQualityScore() {
        return qualityScore;
    }
    
    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }
    
    public int getSparePartsScore() {
        return sparePartsScore;
    }
    
    public void setSparePartsScore(int sparePartsScore) {
        this.sparePartsScore = sparePartsScore;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
    
    // Helper methods
    public String getFormattedPrice() {
        return String.format("Rp %,.2f", price);
    }
    
    public double getPriceAsDouble() {
        return price.doubleValue();
    }
    
    public boolean isValidScores() {
        return qualityScore >= 1 && qualityScore <= 100 
            && sparePartsScore >= 1 && sparePartsScore <= 100;
    }
    
    public String getScoreSummary() {
        return String.format("Kualitas: %d, Suku Cadang: %d", 
                           qualityScore, sparePartsScore);
    }
    
    @Override
    public String toString() {
        return "Alternative{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", qualityScore=" + qualityScore +
                ", sparePartsScore=" + sparePartsScore +
                ", totalScore=" + totalScore +
                ", ranking=" + ranking +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Alternative that = (Alternative) obj;
        return id == that.id && code.equals(that.code);
    }
    
    @Override
    public int hashCode() {
        return code.hashCode();
    }
}