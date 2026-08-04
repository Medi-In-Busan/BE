CREATE TABLE hospital (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    reg_no            VARCHAR(20)  NOT NULL,
    original_reg_no   VARCHAR(20),
    name              VARCHAR(200) NOT NULL,
    institution_type  VARCHAR(30)  NOT NULL,
    address           VARCHAR(300) NOT NULL,
    latitude          DOUBLE,
    longitude         DOUBLE,
    phone             VARCHAR(30),
    website           VARCHAR(300),
    business_hours    TEXT,
    description_ko    TEXT,
    description_en    TEXT,
    verified_at       DATE,
    verified_by       VARCHAR(50),
    notes             TEXT,
    CONSTRAINT uk_hospital_reg_no UNIQUE (reg_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE hospital_specialty (
    hospital_id BIGINT      NOT NULL,
    specialty   VARCHAR(30) NOT NULL,
    CONSTRAINT fk_hospital_specialty_hospital FOREIGN KEY (hospital_id) REFERENCES hospital (id) ON DELETE CASCADE,
    CONSTRAINT uk_hospital_specialty UNIQUE (hospital_id, specialty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_hospital_specialty_specialty ON hospital_specialty (specialty);

-- 언어 코드(ko/en/zh/ja)와 한글 국가·권역명(러시아, 중동 등)이 원본 그대로 섞여 저장된다.
-- 정규화는 별도 이슈에서 다룬다.
CREATE TABLE hospital_target_country (
    hospital_id   BIGINT      NOT NULL,
    country_value VARCHAR(20) NOT NULL,
    CONSTRAINT fk_hospital_target_country_hospital FOREIGN KEY (hospital_id) REFERENCES hospital (id) ON DELETE CASCADE,
    CONSTRAINT uk_hospital_target_country UNIQUE (hospital_id, country_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
