package com.mediinbusan.backend.wellness.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "wellness_place_translation")
public class WellnessPlaceTranslation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content_id", nullable = false, length = 50)
    private String contentId;
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;
    @Column(name = "source_hash", nullable = false, length = 64)
    private String sourceHash;
    @Column(nullable = false, length = 300)
    private String name;
    @Column(nullable = false, length = 500)
    private String address;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "translated_at", nullable = false)
    private Instant translatedAt;

    protected WellnessPlaceTranslation() {}

    public WellnessPlaceTranslation(String contentId, String languageCode, String sourceHash, String name, String address, String description) {
        refresh(contentId, languageCode, sourceHash, name, address, description);
    }

    public void refresh(String contentId, String languageCode, String sourceHash, String name, String address, String description) {
        this.contentId = contentId;
        this.languageCode = languageCode;
        this.sourceHash = sourceHash;
        this.name = name;
        this.address = address;
        this.description = description;
        this.translatedAt = Instant.now();
    }

    public String sourceHash() { return sourceHash; }
    public String name() { return name; }
    public String address() { return address; }
    public String description() { return description; }
}

