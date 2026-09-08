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

    /*
     * 방문 정보 6종 — TourAPI detailIntro2(콘텐츠 타입별로 필드명이 다르다)와 부산맛집정보
     * getFoodKr에서 채운다. 어느 소스도 전부 주지는 않아서 대부분의 장소는 이 중 일부만 차 있다.
     *
     * 값은 API 원문(한국어)이다 — 이유는 WellnessVisitInfo 주석 참고.
     */
    @Column(name = "business_hours", length = 500)
    private String businessHours;

    @Column(name = "rest_date", length = 300)
    private String restDate;

    @Column(name = "signature_menu", length = 500)
    private String signatureMenu;

    @Column(name = "usage_fee", length = 500)
    private String usageFee;

    @Column(name = "parking_info", length = 500)
    private String parkingInfo;

    @Column(name = "homepage_url", length = 500)
    private String homepageUrl;

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

    public String getBusinessHours() {
        return businessHours;
    }

    public String getRestDate() {
        return restDate;
    }

    public String getSignatureMenu() {
        return signatureMenu;
    }

    public String getUsageFee() {
        return usageFee;
    }

    public String getParkingInfo() {
        return parkingInfo;
    }

    public String getHomepageUrl() {
        return homepageUrl;
    }

    /**
     * 방문 정보를 채운다. {@link #applyTranslation}과 같은 규칙으로 <b>빈 값은 무시</b>한다 —
     * 이번 ingest에서 값을 못 받았다고 지난번에 받아둔 영업시간·대표메뉴를 지우지 않는다.
     *
     * 이게 중요한 이유: TourAPI 상세 조회는 일일 트래픽 한도 때문에 매번 앞쪽 N건만 부른다
     * ({@code tourApiIntroFetchLimit}). 덮어쓰기로 만들면 한도 밖으로 밀려난 장소의 방문 정보가
     * ingest를 돌릴 때마다 지워졌다 채워졌다 한다.
     */
    public void applyVisitInfo(
        String businessHours,
        String restDate,
        String signatureMenu,
        String usageFee,
        String parkingInfo,
        String homepageUrl
    ) {
        if (hasText(businessHours)) this.businessHours = businessHours;
        if (hasText(restDate)) this.restDate = restDate;
        if (hasText(signatureMenu)) this.signatureMenu = signatureMenu;
        if (hasText(usageFee)) this.usageFee = usageFee;
        if (hasText(parkingInfo)) this.parkingInfo = parkingInfo;
        if (hasText(homepageUrl)) this.homepageUrl = homepageUrl;
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
        // 설명·전화번호도 categoryCode와 같은 규칙으로 빈 값은 무시한다.
        //
        // 이 둘은 목록 응답(areaBasedList2)에 아예 없고 detailCommon2를 따로 불러야만 채워지는데,
        // 그 호출은 일일 트래픽 한도 때문에 매 ingest마다 일부 장소에만 돌아간다. 예전처럼 무조건
        // 덮어쓰면 이번에 호출 대상이 아니었던 장소는 candidate.description이 null인 채로 들어가
        // 지난 ingest에서 받아둔 설명이 매번 지워졌다 — 그래서 아무리 여러 번 돌려도 저장된 설명
        // 수가 한도(300건)를 넘지 못하고 제자리걸음이었다(실측: 관광지 351건 중 정확히 300건).
        // 빈 값을 무시해야 여러 번의 ingest가 비로소 누적된다(applyVisitInfo와 같은 규칙).
        if (hasText(description)) {
            this.description = description;
        }
        if (hasText(phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }
        this.modifiedDate = modifiedDate;
    }
}
