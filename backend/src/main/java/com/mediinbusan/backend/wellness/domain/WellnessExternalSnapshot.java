package com.mediinbusan.backend.wellness.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "wellness_external_snapshot")
public class WellnessExternalSnapshot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "snapshot_key", nullable = false, unique = true, length = 300)
    private String snapshotKey;
    @Column(nullable = false, length = 50)
    private String source;
    @Column(name = "external_id", nullable = false, length = 200)
    private String externalId;
    @Column(nullable = false, length = 100)
    private String scope;
    @Column(name = "period_key", nullable = false, length = 30)
    private String periodKey;
    @Column(length = 500)
    private String title;
    private Double latitude;
    private Double longitude;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
    @Column(name = "synced_at", nullable = false)
    private Instant syncedAt;

    protected WellnessExternalSnapshot() {}

    public WellnessExternalSnapshot(String snapshotKey, String source, String externalId, String scope, String periodKey, String title, Double latitude, Double longitude, String payload) {
        this.snapshotKey = snapshotKey; this.source = source; this.externalId = externalId; this.scope = scope;
        this.periodKey = periodKey; this.title = title; this.latitude = latitude; this.longitude = longitude;
        this.payload = payload; this.syncedAt = Instant.now();
    }

    public void refresh(String title, Double latitude, Double longitude, String payload) {
        this.title = title; this.latitude = latitude; this.longitude = longitude; this.payload = payload; this.syncedAt = Instant.now();
    }
}
