CREATE TABLE wellness_place_translation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    content_id      VARCHAR(50)  NOT NULL,
    language_code   VARCHAR(10)  NOT NULL,
    source_hash     VARCHAR(64)  NOT NULL,
    name            VARCHAR(300) NOT NULL,
    address         VARCHAR(500) NOT NULL,
    description     TEXT,
    translated_at   TIMESTAMP    NOT NULL,
    CONSTRAINT uk_wellness_translation_place_language UNIQUE (content_id, language_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

