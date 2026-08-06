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

    protected WellnessPlace() {
    }

    public WellnessPlace(
        String contentId,
        String name,
        WellnessPlaceType placeType,
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

    public void updateFrom(
        String name,
        WellnessPlaceType placeType,
        String address,
        Coordinates coordinates,
        String imageUrl,
        String description,
        String phoneNumber,
        LocalDate modifiedDate
    ) {
        this.name = name;
        this.placeType = placeType;
        this.address = address;
        this.coordinates = coordinates;
        this.imageUrl = imageUrl;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.modifiedDate = modifiedDate;
    }
}
