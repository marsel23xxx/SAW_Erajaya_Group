package com.erajaya.datamining.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model untuk Criteria
 */
public class Criteria {
    private int id;
    private String name;
    private BigDecimal weight;
    private CriteriaType type;
    private String description;
    private LocalDateTime createdAt;
    
    // Enum untuk tipe kriteria
    public enum CriteriaType {
        BENEFIT("benefit"),  // Semakin tinggi semakin baik
        COST("cost");        // Semakin rendah semakin baik
        
        private String value;
        
        CriteriaType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static CriteriaType fromString(String text) {
            for (CriteriaType type : CriteriaType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return BENEFIT; // default
        }
    }
    
    // Constructors
    public Criteria() {}
    
    public Criteria(String name, BigDecimal weight, CriteriaType type) {
        this.name = name;
        this.weight = weight;
        this.type = type;
    }
    
    public Criteria(String name, BigDecimal weight, CriteriaType type, String description) {
        this(name, weight, type);
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public CriteriaType getType() {
        return type;
    }
    
    public void setType(CriteriaType type) {
        this.type = type;
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
    
    // Helper methods
    public double getWeightAsDouble() {
        return weight.doubleValue();
    }
    
    public String getFormattedWeight() {
        return String.format("%.2f", weight);
    }
    
    public String getFormattedWeightPercent() {
        return String.format("%.1f%%", weight.doubleValue() * 100);
    }
    
    public boolean isBenefit() {
        return type == CriteriaType.BENEFIT;
    }
    
    public boolean isCost() {
        return type == CriteriaType.COST;
    }
    
    public String getTypeDescription() {
        return type == CriteriaType.BENEFIT ? 
               "Benefit (semakin tinggi semakin baik)" : 
               "Cost (semakin rendah semakin baik)";
    }
    
    @Override
    public String toString() {
        return "Criteria{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Criteria criteria = (Criteria) obj;
        return id == criteria.id && name.equals(criteria.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}