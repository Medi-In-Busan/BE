package com.mediinbusan.backend.hospital.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 시딩 데이터(122건 큐레이션 JSON) 기준 의료기관 엔티티.
 * meta(verifiedAt/verifiedBy/notes), originalRegNo는 운영/큐레이션 전용 필드라
 * DTO/API 응답에는 절대 노출하지 않는다 — 노출 여부는 여기서가 아니라 매퍼에서 강제한다.
 */
@Entity
@Table(name = "hospital")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reg_no", nullable = false, unique = true, length = 20)
    private String regNo;

    // 큐레이션 이전 원본 시스템의 참조 번호. 내부 추적용, 공개 API 응답에 포함하지 않는다.
    @Column(name = "original_reg_no", length = 20)
    private String originalRegNo;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "institution_type", nullable = false, length = 30)
    private InstitutionType institutionType;

    @Column(name = "address", nullable = false, length = 300)
    private String address;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "website", length = 300)
    private String website;

    @Column(name = "business_hours_ko", columnDefinition = "TEXT")
    private String businessHoursKo;

    @Column(name = "business_hours_en", columnDefinition = "TEXT")
    private String businessHoursEn;

    @Column(name = "business_hours_zh", columnDefinition = "TEXT")
    private String businessHoursZh;

    @Column(name = "business_hours_ja", columnDefinition = "TEXT")
    private String businessHoursJa;

    @Column(name = "description_ko", columnDefinition = "TEXT")
    private String descriptionKo;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_zh", columnDefinition = "TEXT")
    private String descriptionZh;

    @Column(name = "description_ja", columnDefinition = "TEXT")
    private String descriptionJa;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "hospital_specialty", joinColumns = @JoinColumn(name = "hospital_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialty", nullable = false, length = 30)
    private Set<MedicalSpecialty> specialties = new HashSet<>();

    // 언어 코드(ko/en/zh/ja)와 한글 국가·권역명(러시아, 중동 등)이 원본 데이터에 섞여 있는 그대로 저장한다.
    // 정규화는 별도 이슈에서 다룬다.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "hospital_target_country", joinColumns = @JoinColumn(name = "hospital_id"))
    @Column(name = "country_value", nullable = false, length = 20)
    private Set<String> targetCountries = new HashSet<>();

    // 아래 세 필드는 큐레이션 메타데이터. 공개 API 응답에 노출하지 않는다.
    @Column(name = "verified_at")
    private LocalDate verifiedAt;

    @Column(name = "verified_by", length = 50)
    private String verifiedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    protected Hospital() {
    }

    public Hospital(
        String regNo,
        String originalRegNo,
        String name,
        InstitutionType institutionType,
        String address,
        Coordinates coordinates,
        String phone,
        String website,
        String businessHoursKo,
        String businessHoursEn,
        String businessHoursZh,
        String businessHoursJa,
        String descriptionKo,
        String descriptionEn,
        String descriptionZh,
        String descriptionJa,
        Set<MedicalSpecialty> specialties,
        Set<String> targetCountries,
        LocalDate verifiedAt,
        String verifiedBy,
        String notes
    ) {
        this.regNo = regNo;
        this.originalRegNo = originalRegNo;
        this.name = name;
        this.institutionType = institutionType;
        this.address = address;
        this.coordinates = coordinates;
        this.phone = phone;
        this.website = website;
        this.businessHoursKo = businessHoursKo;
        this.businessHoursEn = businessHoursEn;
        this.businessHoursZh = businessHoursZh;
        this.businessHoursJa = businessHoursJa;
        this.descriptionKo = descriptionKo;
        this.descriptionEn = descriptionEn;
        this.descriptionZh = descriptionZh;
        this.descriptionJa = descriptionJa;
        this.specialties = specialties != null ? specialties : new HashSet<>();
        this.targetCountries = targetCountries != null ? targetCountries : new HashSet<>();
        this.verifiedAt = verifiedAt;
        this.verifiedBy = verifiedBy;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getOriginalRegNo() {
        return originalRegNo;
    }

    public String getName() {
        return name;
    }

    public InstitutionType getInstitutionType() {
        return institutionType;
    }

    public String getAddress() {
        return address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getBusinessHoursKo() {
        return businessHoursKo;
    }

    public String getBusinessHoursEn() {
        return businessHoursEn;
    }

    public String getBusinessHoursZh() {
        return businessHoursZh;
    }

    public String getBusinessHoursJa() {
        return businessHoursJa;
    }

    public String getDescriptionKo() {
        return descriptionKo;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getDescriptionZh() {
        return descriptionZh;
    }

    public String getDescriptionJa() {
        return descriptionJa;
    }

    public Set<MedicalSpecialty> getSpecialties() {
        return specialties;
    }

    public Set<String> getTargetCountries() {
        return targetCountries;
    }

    public LocalDate getVerifiedAt() {
        return verifiedAt;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public String getNotes() {
        return notes;
    }
}
