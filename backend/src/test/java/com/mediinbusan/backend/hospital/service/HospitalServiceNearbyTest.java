package com.mediinbusan.backend.hospital.service;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.InstitutionType;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.dto.HospitalNearbyResponse;
import com.mediinbusan.backend.hospital.repository.HospitalRepository;
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
class HospitalServiceNearbyTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalService hospitalService;

    @Test
    void 기준_좌표_반경_내_병원을_거리순으로_반환한다() {
        hospitalRepository.save(hospital("near", "가까운병원", new Coordinates(35.1689, 129.1296), Set.of(MedicalSpecialty.ETC)));
        hospitalRepository.save(hospital("far", "먼병원", new Coordinates(35.1587, 129.1604), Set.of(MedicalSpecialty.ETC)));

        List<HospitalNearbyResponse> result = hospitalService.findNearby(35.1688, 129.1295, 5_000.0, null);

        assertThat(result).extracting(HospitalNearbyResponse::regNo).containsExactly("near", "far");
        assertThat(result.getFirst().distanceMeters()).isLessThan(result.get(1).distanceMeters());
    }

    @Test
    void 반경_밖_병원은_제외한다() {
        hospitalRepository.save(hospital("near", "가까운병원", new Coordinates(35.1689, 129.1296), Set.of(MedicalSpecialty.ETC)));
        hospitalRepository.save(hospital("far", "먼병원", new Coordinates(35.1587, 129.1604), Set.of(MedicalSpecialty.ETC)));

        List<HospitalNearbyResponse> result = hospitalService.findNearby(35.1688, 129.1295, 100.0, null);

        assertThat(result).extracting(HospitalNearbyResponse::regNo).containsExactly("near");
    }

    @Test
    void specialties로_추가_필터링한다() {
        hospitalRepository.save(hospital("skin", "피부과", new Coordinates(35.1689, 129.1296), Set.of(MedicalSpecialty.SKIN_BEAUTY)));
        hospitalRepository.save(hospital("dental", "치과", new Coordinates(35.1689, 129.1296), Set.of(MedicalSpecialty.DENTAL)));

        List<HospitalNearbyResponse> result = hospitalService.findNearby(35.1688, 129.1295, 5_000.0, List.of(MedicalSpecialty.DENTAL));

        assertThat(result).extracting(HospitalNearbyResponse::regNo).containsExactly("dental");
    }

    private Hospital hospital(String regNo, String name, Coordinates coordinates, Set<MedicalSpecialty> specialties) {
        return new Hospital(
            regNo,
            null,
            name,
            InstitutionType.CLINIC,
            "부산 어딘가",
            coordinates,
            "051-000-0000",
            null,
            null,
            null,
            null,
            specialties,
            Set.of("ko"),
            LocalDate.now(),
            "test",
            null
        );
    }
}
