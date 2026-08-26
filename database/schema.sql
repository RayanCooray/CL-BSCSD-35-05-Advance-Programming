CREATE
DATABASE IF NOT EXISTS sunrise_dental_clinic;
USE
sunrise_dental_clinic;
CREATE TABLE users
(
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255)       NOT NULL,
    full_name     VARCHAR(150)       NOT NULL,
    email         VARCHAR(120),
    role          ENUM('ADMIN','RECEPTIONIST','DENTIST','MANAGEMENT') NOT NULL,
    active        BOOLEAN DEFAULT TRUE
);
CREATE TABLE patients
(
    patient_id      INT AUTO_INCREMENT PRIMARY KEY,
    patient_code    VARCHAR(30) UNIQUE NOT NULL,
    full_name       VARCHAR(150)       NOT NULL,
    date_of_birth   DATE,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    contact_number  VARCHAR(30) UNIQUE NOT NULL,
    email           VARCHAR(120),
    address         VARCHAR(255),
    medical_history TEXT,
    registered_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE dentists
(
    dentist_id          INT AUTO_INCREMENT PRIMARY KEY,
    dentist_code        VARCHAR(30) UNIQUE NOT NULL,
    full_name           VARCHAR(150)       NOT NULL,
    slmc_number         VARCHAR(50) UNIQUE NOT NULL,
    specialization      VARCHAR(100),
    contact_number      VARCHAR(30),
    email               VARCHAR(120),
    consultation_fee    DECIMAL(12, 2)     NOT NULL,
    working_hours_start TIME               NOT NULL,
    working_hours_end   TIME               NOT NULL,
    available           BOOLEAN DEFAULT TRUE
);
CREATE TABLE treatments
(
    treatment_id     INT AUTO_INCREMENT PRIMARY KEY,
    treatment_code   VARCHAR(30) UNIQUE  NOT NULL,
    name             VARCHAR(150) UNIQUE NOT NULL,
    description      VARCHAR(255),
    category         VARCHAR(100),
    base_price       DECIMAL(12, 2)      NOT NULL,
    duration_minutes INT                 NOT NULL,
    active           BOOLEAN DEFAULT TRUE
);
CREATE TABLE appointments
(
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    appointment_no   VARCHAR(30) UNIQUE NOT NULL,
    patient_id       INT                NOT NULL,
    dentist_id       INT                NOT NULL,
    treatment_id     INT                NOT NULL,
    appointment_date DATE               NOT NULL,
    start_time       TIME               NOT NULL,
    end_time         TIME               NOT NULL,
    status           ENUM('SCHEDULED','CONFIRMED','COMPLETED','CANCELLED','NO_SHOW') NOT NULL,
    remarks          VARCHAR(500),
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients (patient_id),
    FOREIGN KEY (dentist_id) REFERENCES dentists (dentist_id),
    FOREIGN KEY (treatment_id) REFERENCES treatments (treatment_id),
    INDEX(dentist_id,appointment_date)
);
CREATE TABLE treatment_record
(
    record_id      INT AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT            NOT NULL,
    dentist_id     INT            NOT NULL,
    treatment_id   INT            NOT NULL,
    appointment_id INT            NOT NULL,
    performed_date DATE           NOT NULL,
    clinical_notes TEXT,
    charged_amount DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients (patient_id),
    FOREIGN KEY (dentist_id) REFERENCES dentists (dentist_id),
    FOREIGN KEY (treatment_id) REFERENCES treatments (treatment_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id),
    INDEX(patient_id,performed_date)
);
CREATE TABLE invoice
(
    invoice_id       INT AUTO_INCREMENT PRIMARY KEY,
    invoice_no       VARCHAR(30) UNIQUE NOT NULL,
    patient_id       INT                NOT NULL,
    appointment_id   INT UNIQUE         NOT NULL,
    issue_date       DATETIME           NOT NULL,
    sub_total        DECIMAL(12, 2)     NOT NULL,
    consultation_fee DECIMAL(12, 2)     NOT NULL,
    tax_rate         DECIMAL(7, 2)      NOT NULL,
    tax_amount       DECIMAL(12, 2)     NOT NULL,
    discount         DECIMAL(12, 2)     NOT NULL,
    total_amount     DECIMAL(12, 2)     NOT NULL,
    status           ENUM('UNPAID','PARTIALLY_PAID','PAID') NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients (patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id)
);
CREATE TABLE invoice_item
(
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id  INT            NOT NULL,
    description VARCHAR(255)   NOT NULL,
    quantity    INT            NOT NULL,
    unit_price  DECIMAL(12, 2) NOT NULL,
    line_total  DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoice (invoice_id) ON DELETE CASCADE
);
CREATE TABLE payment
(
    payment_id   INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id   INT            NOT NULL,
    amount_paid  DECIMAL(12, 2) NOT NULL,
    payment_date DATETIME       NOT NULL,
    method       ENUM('CASH','CARD','BANK_TRANSFER','INSURANCE') NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoice (invoice_id)
);
