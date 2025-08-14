-- Database Schema untuk Sistem Data Mining SAW PT Erajaya
-- Buat database
CREATE DATABASE IF NOT EXISTS erajaya_datamining;
USE erajaya_datamining;

-- Tabel Users untuk login
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'manager', 'staff') DEFAULT 'staff',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel Kriteria
CREATE TABLE criteria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    weight DECIMAL(3,2) NOT NULL,
    type ENUM('benefit', 'cost') NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Alternatif (Produk)
CREATE TABLE alternatives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    quality_score INT NOT NULL,
    spare_parts_score INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel Evaluasi (untuk menyimpan nilai alternatif terhadap kriteria)
CREATE TABLE evaluations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alternative_id INT NOT NULL,
    criteria_id INT NOT NULL,
    value DECIMAL(10,4) NOT NULL,
    normalized_value DECIMAL(10,4),
    weighted_value DECIMAL(10,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alternative_id) REFERENCES alternatives(id) ON DELETE CASCADE,
    FOREIGN KEY (criteria_id) REFERENCES criteria(id) ON DELETE CASCADE,
    UNIQUE KEY unique_evaluation (alternative_id, criteria_id)
);

-- Tabel Hasil SAW
CREATE TABLE saw_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alternative_id INT NOT NULL,
    total_score DECIMAL(10,4) NOT NULL,
    ranking INT NOT NULL,
    calculation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alternative_id) REFERENCES alternatives(id) ON DELETE CASCADE
);

-- Tabel Logs untuk audit
CREATE TABLE activity_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(50),
    record_id INT,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Insert data default
-- Users
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Administrator', 'admin'),
('manager', 'manager123', 'Manager Logistik', 'manager'),
('staff', 'staff123', 'Staff Gudang', 'staff');

-- Kriteria
INSERT INTO criteria (name, weight, type, description) VALUES
('Harga', 0.40, 'cost', 'Harga produk dalam rupiah'),
('Kualitas', 0.35, 'benefit', 'Skor kualitas produk (1-100)'),
('Suku Cadang', 0.25, 'benefit', 'Ketersediaan suku cadang (1-100)');

-- Alternatif (berdasarkan data dari tabel)
INSERT INTO alternatives (code, name, price, quality_score, spare_parts_score, description) VALUES
('A1', 'iPhone 15 Pro', 18000000.00, 95, 85, 'Smartphone premium Apple terbaru'),
('A2', 'Samsung Galaxy S24', 12000000.00, 90, 95, 'Flagship Android Samsung'),
('A3', 'Xiaomi 14 Ultra', 9500000.00, 88, 80, 'Smartphone premium Xiaomi dengan kamera canggih'),
('A4', 'OPPO Find X7', 11000000.00, 85, 75, 'Smartphone premium OPPO dengan desain elegan'),
('A5', 'Vivo X100 Pro', 10500000.00, 87, 70, 'Smartphone premium Vivo dengan performa tinggi');

-- Insert evaluasi untuk setiap alternatif dan kriteria
-- Harga (kriteria cost - semakin rendah semakin baik)
INSERT INTO evaluations (alternative_id, criteria_id, value) VALUES
(1, 1, 18000000.00), -- iPhone 15 Pro - Harga
(2, 1, 12000000.00), -- Samsung Galaxy S24 - Harga  
(3, 1, 9500000.00),  -- Xiaomi 14 Ultra - Harga
(4, 1, 11000000.00), -- OPPO Find X7 - Harga
(5, 1, 10500000.00); -- Vivo X100 Pro - Harga

-- Kualitas (kriteria benefit - semakin tinggi semakin baik)
INSERT INTO evaluations (alternative_id, criteria_id, value) VALUES
(1, 2, 95), -- iPhone 15 Pro - Kualitas
(2, 2, 90), -- Samsung Galaxy S24 - Kualitas
(3, 2, 88), -- Xiaomi 14 Ultra - Kualitas
(4, 2, 85), -- OPPO Find X7 - Kualitas
(5, 2, 87); -- Vivo X100 Pro - Kualitas

-- Suku Cadang (kriteria benefit - semakin tinggi semakin baik)
INSERT INTO evaluations (alternative_id, criteria_id, value) VALUES
(1, 3, 85), -- iPhone 15 Pro - Suku Cadang
(2, 3, 95), -- Samsung Galaxy S24 - Suku Cadang
(3, 3, 80), -- Xiaomi 14 Ultra - Suku Cadang
(4, 3, 75), -- OPPO Find X7 - Suku Cadang
(5, 3, 70); -- Vivo X100 Pro - Suku Cadangusersusersusers