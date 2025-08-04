package com.erajaya.datamining.model;

import java.time.LocalDateTime;

/**
 * Model untuk User
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Enum untuk role user
    public enum UserRole {
        ADMIN("admin"), 
        MANAGER("manager"), 
        STAFF("staff");
        
        private String value;
        
        UserRole(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static UserRole fromString(String text) {
            for (UserRole role : UserRole.values()) {
                if (role.value.equalsIgnoreCase(text)) {
                    return role;
                }
            }
            return STAFF; // default
        }
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = UserRole.STAFF;
    }
    
    public User(String username, String password, String fullName, UserRole role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
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
    
    // Helper methods
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    public boolean isManager() {
        return role == UserRole.MANAGER;
    }
    
    public boolean isStaff() {
        return role == UserRole.STAFF;
    }
    
    public boolean hasPermission(String permission) {
        switch (permission.toLowerCase()) {
            case "create":
            case "read":
                return true; // Semua user bisa create dan read
            case "update":
                return role == UserRole.ADMIN || role == UserRole.MANAGER;
            case "delete":
                return role == UserRole.ADMIN;
            case "report":
                return role == UserRole.ADMIN || role == UserRole.MANAGER;
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id && username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}