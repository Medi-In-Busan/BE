package com.mediinbusan.backend.wellness.domain;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "wellness_place")
public class WellnessPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false, unique = true, length = 50)
    private String contentId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type", nullable = false, length = 30)
    private WellnessPlaceType placeType;

    /**
     * TourAPI cat3 원본 코드(예: A04010300 = 백화점). {@link WellnessPlaceType}만으로는 "쇼핑" 안의
     * 백화점/전통시장/면세점이 구분되지 않아 이 코드를 같이 들고 있는다.
     *
     * 우리 분류로 변환하지 않고 원본을 그대로 저장하는 이유는 V11 마이그레이션 주석 참고 —
     * 변환은 응답을 만들 때(WellnessDtoMapper) 한다. 아직 재수집하지 않은 행이나 TourAPI가 아닌
     * 소스(부산맛집정보 등)에서 온 행은 null이다.
     */
    @Column(name = "category_code", length = 20)
    private String categoryCode;

    @Column(name = "address", nullable = false, length = 300)
    private String address;

    @Embedded
    private Coordinates coordinates;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "modified_date")
    private LocalDate modifiedDate;

    // 부산맛집정보(getFoodEn/getFoodJa/getFoodZhs) 등 다국어 소스로 채워지는 번역 — 없으면 null이고
    // WellnessDtoMapper가 name/address/description(한국어 원문)으로 폴백한다. Hospital의
    // descriptionEn/Zh/Ja(HospitalDtoMapper 참고)와 같은 규칙.
    @Column(name = "name_en", length = 200)
    private String nameEn;

    @Column(name = "name_zh", length = 200)
    private String nameZh;

    @Column(name = "name_ja", length = 200)
    private String nameJa;

    @Column(name = "address_en", length = 300)
    private String addressEn;

    @Column(name = "address_zh", length = 300)
    private String addressZh;

    @Column(name = "address_ja", length = 300)
    private String addressJa;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_zh", columnDefinition = "TEXT")
    private String descriptionZh;

    @Column(name = "description_ja", columnDefinition = "TEXT")
    private String descriptionJa;

    protected WellnessPlace() {
    }

    public WellnessPlace(
        String contentId,
        String name,
        WellnessPlaceType placeType,
        String categoryCode,
        String address,
        Coordinates coordinates,
        String imageUrl,
        String description,
        String phoneNumber,
        LocalDate modifiedDate
    ) {
        this.contentId = contentId;
        this.name = name;
        this.placeType = placeType;
        this.categoryCode = categoryCode;
        this.address = address;
        this.coordinates = coordinates;
        this.imageUrl = imageUrl;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public String getContentId() {
        return contentId;
    }

    public String getName() {
        return name;
    }

    public WellnessPlaceType getPlaceType() {
        return placeType;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getAddress() {
        return address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getModifiedDate() {
        return modifiedDate;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameZh() {
        return nameZh;
    }

    public String getNameJa() {
        return nameJa;
    }

    public String getAddressEn() {
        return addressEn;
    }

    public String getAddressZh() {
        return addressZh;
    }

    public String getAddressJa() {
        return addressJa;
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

    /**
     * 다국어 소스(예: 부산맛집정보 getFoodEn/getFoodJa/getFoodZhs) 한 건을 이 장소에 반영한다.
     * 빈 값은 무시한다 — 이번 응답이 비어 있다고 해서 이전에 저장해둔 번역을 지우지 않는다.
     */
    public void applyTranslation(String lang, String name, String address, String description) {
        switch (lang) {
            case "en" -> {
                if (hasText(name)) this.nameEn = name;
                if (hasText(address)) this.addressEn = address;
                if (hasText(description)) this.descriptionEn = description;
            }
            case "zh" -> {
                if (hasText(name)) this.nameZh = name;
                if (hasText(address)) this.addressZh = address;
                if (hasText(description)) this.descriptionZh = description;
            }
            case "ja" -> {
                if (hasText(name)) this.nameJa = name;
                if (hasText(address)) this.addressJa = address;
                if (hasText(description)) this.descriptionJa = description;
            }
            default -> {
                // ko는 name/address/description(원문) 필드가 이미 담당한다.
            }
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public void updateFrom(
        String name,
        WellnessPlaceType placeType,
        String categoryCode,
        String address,
        Coordinates coordinates,
        String imageUrl,
        String description,
        String phoneNumber,
        LocalDate modifiedDate
    ) {
        this.name = name;
        this.placeType = placeType;
        // 이번 수집에서 코드를 못 받았으면(다른 소스 등) 이미 저장돼 있던 값을 지우지 않는다.
        if (categoryCode != null) {
            this.categoryCode = categoryCode;
        }
        this.address = address;
        this.coordinates = coordinates;
        this.imageUrl = imageUrl;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.modifiedDate = modifiedDate;
    }
}
