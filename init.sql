-- Kullanıcılar tablosu (Hasta, Doktor ve Personel için ortak tablo)
CREATE TABLE
    users (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        role ENUM ('DOCTOR', 'PATIENT', 'STAFF') NOT NULL,
        name VARCHAR(100) NOT NULL,
        surname VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE,
        phone VARCHAR(20),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Hastalar tablosu
CREATE TABLE
    patients (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        tc_no VARCHAR(11) UNIQUE,
        birth_date DATE,
        blood_type VARCHAR(5),
        address TEXT,
        FOREIGN KEY (user_id) REFERENCES users (id)
    );

-- Doktorlar tablosu
CREATE TABLE
    doctors (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        user_id BIGINT NOT NULL,
        specialty VARCHAR(100) NOT NULL,
        license_number VARCHAR(20) UNIQUE,
        FOREIGN KEY (user_id) REFERENCES users (id)
    );

-- Randevular tablosu
CREATE TABLE
    appointments (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        patient_id BIGINT NOT NULL,
        doctor_id BIGINT NOT NULL,
        appointment_date DATETIME NOT NULL,
        status ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
        notes TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (patient_id) REFERENCES patients (id),
        FOREIGN KEY (doctor_id) REFERENCES doctors (id)
    );

-- Reçeteler tablosu
CREATE TABLE
    prescriptions (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        appointment_id BIGINT NOT NULL,
        prescription_number VARCHAR(20) UNIQUE NOT NULL,
        description TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (appointment_id) REFERENCES appointments (id)
    );

-- Sonuçlar tablosu
CREATE TABLE
    medical_results (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        appointment_id BIGINT NOT NULL,
        result_type VARCHAR(100) NOT NULL,
        result_description TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (appointment_id) REFERENCES appointments (id)
    );