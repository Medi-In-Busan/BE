package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.InstitutionType;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.repository.HospitalRepository;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.domain.WellnessPlaceType;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WellnessServiceTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private WellnessPlaceRepository wellnessPlaceRepository;

    @Autowired
    private WellnessService wellnessService;

    @Test
    void 병원_좌표_기준_반경_내_웰니스_장소를_거리순으로_반환한다() {
        hospitalRepository.save(hospital("H-1", new Coordinates(35.1688, 129.1295)));
        wellnessPlaceRepository.save(place("near", "가까운 장소", new Coordinates(35.1689, 129.1296)));
        wellnessPlaceRepository.save(place("far", "먼 장소", new Coordinates(35.1587, 129.1604)));

        List<WellnessPlaceResponse> result = wellnessService.getNearbyPlaces("H-1", 5_000.0);

        assertThat(result).extracting(WellnessPlaceResponse::contentId).containsExactly("near", "far");
        assertThat(result.getFirst().distanceFromHospitalMeters()).isLessThan(result.get(1).distanceFromHospitalMeters());
    }

    @Test
    void 반경_밖_웰니스_장소는_제외한다() {
        hospitalRepository.save(hospital("H-1", new Coordinates(35.1688, 129.1295)));
        wellnessPlaceRepository.save(place("near", "가까운 장소", new Coordinates(35.1689, 129.1296)));
        wellnessPlaceRepository.save(place("far", "먼 장소", new Coordinates(35.1587, 129.1604)));

        List<WellnessPlaceResponse> result = wellnessService.getNearbyPlaces("H-1", 100.0);

        assertThat(result).extracting(WellnessPlaceResponse::contentId).containsExactly("near");
    }

    @Test
    void 웰니스_상세는_contentId로_조회한다() {
        wellnessPlaceRepository.save(place("place-1", "해운대 해수욕장", new Coordinates(35.1587, 129.1604)));

        WellnessPlaceResponse result = wellnessService.getPlaceDetail("place-1");

        assertThat(result.name()).isEqualTo("해운대 해수욕장");
        assertThat(result.distanceFromHospitalMeters()).isNull();
    }

    private Hospital hospital(String regNo, Coordinates coordinates) {
        return new Hospital(
            regNo,
            null,
            "테스트병원",
            InstitutionType.CLINIC,
            "부산 어딘가",
            coordinates,
            "051-000-0000",
            null,
            null,
            null,
            null,
            Set.of(MedicalSpecialty.ETC),
            Set.of("ko"),
            LocalDate.now(),
            "test",
            null
        );
    }

    private WellnessPlace place(String contentId, String name, Coordinates coordinates) {
        return new WellnessPlace(
            contentId,
            name,
            WellnessPlaceType.TOURIST_ATTRACTION,
            "부산 어딘가",
            coordinates,
            null,
            "설명",
            null,
            LocalDate.now()
        );
    }
}
