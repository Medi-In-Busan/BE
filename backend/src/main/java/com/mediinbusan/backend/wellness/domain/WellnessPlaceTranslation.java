package com.mediinbusan.backend.wellness.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
// Flyway V10과 같은 제약을 엔티티에도 선언한다 — 테스트(ddl-auto=create-drop, Flyway 꺼짐) 스키마에도 생기게 하려는 것.
// 동시 번역의 중복 저장을 이 제약이 막는다(WellnessPlaceTranslationService.localizeAll 참고).
@Table(
    name = "wellness_place_translation",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_wellness_translation_place_language",
        columnNames = {"content_id", "language_code"}
    )
)
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

    // 여러 건을 한 번에 읽어 contentId로 묶을 때 쓴다(WellnessPlaceTranslationService.loadCache).
    public String contentId() { return contentId; }
    public String sourceHash() { return sourceHash; }
    public String name() { return name; }
    public String address() { return address; }
    public String description() { return description; }
}

