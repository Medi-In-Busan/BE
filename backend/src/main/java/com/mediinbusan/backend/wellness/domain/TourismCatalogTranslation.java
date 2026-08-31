package com.mediinbusan.backend.wellness.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "tourism_catalog_translation")
public class TourismCatalogTranslation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30)
    private String category;
    @Column(name = "item_id", nullable = false, length = 100)
    private String itemId;
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;
    @Column(name = "source_hash", nullable = false, length = 64)
    private String sourceHash;
    @Column(nullable = false, length = 500)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String subtitle;
    @Column(length = 1000)
    private String address;
    @Column(name = "details_json", nullable = false, columnDefinition = "TEXT")
    private String detailsJson;
    @Column(name = "translated_at", nullable = false)
    private Instant translatedAt;

    protected TourismCatalogTranslation() {}

    public TourismCatalogTranslation(String category, String itemId, String languageCode, String sourceHash,
                                     String title, String subtitle, String address, String detailsJson) {
        refresh(category, itemId, languageCode, sourceHash, title, subtitle, address, detailsJson);
    }

    public void refresh(String category, String itemId, String languageCode, String sourceHash,
                        String title, String subtitle, String address, String detailsJson) {
        this.category = category;
        this.itemId = itemId;
        this.languageCode = languageCode;
        this.sourceHash = sourceHash;
        this.title = title;
        this.subtitle = subtitle;
        this.address = address;
        this.detailsJson = detailsJson;
        this.translatedAt = Instant.now();
    }

    public String sourceHash() { return sourceHash; }
    public String title() { return title; }
    public String subtitle() { return subtitle; }
    public String address() { return address; }
    public String detailsJson() { return detailsJson; }
}
