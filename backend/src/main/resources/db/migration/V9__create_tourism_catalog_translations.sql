CREATE TABLE tourism_catalog_translation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    category        VARCHAR(30)  NOT NULL,
    item_id         VARCHAR(100) NOT NULL,
    language_code   VARCHAR(10)  NOT NULL,
    source_hash     VARCHAR(64)  NOT NULL,
    title           VARCHAR(500) NOT NULL,
    subtitle        TEXT,
    address         VARCHAR(1000),
    details_json    TEXT         NOT NULL,
    translated_at   TIMESTAMP    NOT NULL,
    CONSTRAINT uk_tourism_catalog_translation UNIQUE (category, item_id, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
