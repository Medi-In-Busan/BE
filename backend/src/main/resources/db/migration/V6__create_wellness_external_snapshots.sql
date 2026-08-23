CREATE TABLE wellness_external_snapshot (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    snapshot_key  VARCHAR(300) NOT NULL,
    source        VARCHAR(50)  NOT NULL,
    external_id   VARCHAR(200) NOT NULL,
    scope         VARCHAR(100) NOT NULL,
    period_key    VARCHAR(30)  NOT NULL,
    title         VARCHAR(500),
    latitude      DOUBLE,
    longitude     DOUBLE,
    payload       TEXT         NOT NULL,
    synced_at     TIMESTAMP    NOT NULL,
    CONSTRAINT uk_wellness_external_snapshot UNIQUE (snapshot_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wellness_external_snapshot_source_scope ON wellness_external_snapshot (source, scope);
CREATE INDEX idx_wellness_external_snapshot_period ON wellness_external_snapshot (period_key);
CREATE INDEX idx_wellness_external_snapshot_lookup ON wellness_external_snapshot (source, scope, period_key);
CREATE INDEX idx_wellness_external_snapshot_external_id ON wellness_external_snapshot (source, external_id);
