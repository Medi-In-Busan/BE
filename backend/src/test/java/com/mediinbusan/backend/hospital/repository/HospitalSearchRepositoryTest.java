package com.mediinbusan.backend.hospital.repository;

import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.InstitutionType;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

// 테스트마다 트랜잭션을 롤백해서 저장한 병원 데이터가 다음 테스트로 새지 않게 한다
// (@DataJpaTest는 이 프로젝트의 test 스타터에 JPA 슬라이스 오토컨피그가 없어 못 쓴다).
@SpringBootTest
@Transactional
class HospitalSearchRepositoryTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Test
    void 이름에_키워드가_포함된_것만_매칭되고_주소는_안_본다() {
        hospitalRepository.save(hospital("1", "서면피부과의원", "부산 부산진구 서면로 1", Set.of(MedicalSpecialty.SKIN_BEAUTY)));
        hospitalRepository.save(hospital("2", "해운대안과", "부산 해운대구 해운대로 1", Set.of(MedicalSpecialty.OPHTHALMOLOGY)));
        // 이름엔 "서면"이 없고 주소에만 있는 케이스 — 주소까지 매칭하면 걸려서는 안 된다.
        hospitalRepository.save(hospital("3", "동래한의원", "부산 동래구 서면대로 1", Set.of(MedicalSpecialty.ORIENTAL_MEDICINE)));

        Page<Hospital> result = hospitalRepository.search("서면", null, PageRequest.of(0, 10));

        assertThat(result.getContent())
            .extracting(Hospital::getRegNo)
            .containsExactly("1");
    }

    @Test
    void 흔한_지명이_주소에만_있으면_전체결과로_새지_않는다() {
        // "부산"처럼 거의 모든 주소에 들어가는 단어로 검색해도 이름에 없으면 매칭되면 안 된다.
        // (주소까지 OR로 검색하다가 실제로 이 케이스가 터져서 title-only로 좁힌 회귀 테스트)
        hospitalRepository.save(hospital("1", "서면피부과의원", "부산 부산진구 서면로 1", Set.of(MedicalSpecialty.SKIN_BEAUTY)));
        hospitalRepository.save(hospital("2", "해운대안과", "부산 해운대구 해운대로 1", Set.of(MedicalSpecialty.OPHTHALMOLOGY)));

        Page<Hospital> result = hospitalRepository.search("부산", null, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void specialties로_필터링하면_IN_OR_조건으로_동작한다() {
        hospitalRepository.save(hospital("1", "A의원", "부산 어딘가 1", Set.of(MedicalSpecialty.SKIN_BEAUTY)));
        hospitalRepository.save(hospital("2", "B의원", "부산 어딘가 2", Set.of(MedicalSpecialty.DENTAL)));
        hospitalRepository.save(hospital("3", "C의원", "부산 어딘가 3", Set.of(MedicalSpecialty.SKIN_BEAUTY, MedicalSpecialty.PLASTIC_SURGERY)));

        Page<Hospital> result = hospitalRepository.search(
            null,
            List.of(MedicalSpecialty.SKIN_BEAUTY, MedicalSpecialty.DENTAL),
            PageRequest.of(0, 10)
        );

        assertThat(result.getContent())
            .extracting(Hospital::getRegNo)
            .containsExactlyInAnyOrder("1", "2", "3");
    }

    @Test
    void 페이지네이션이_적용된다() {
        for (int i = 1; i <= 5; i++) {
            hospitalRepository.save(hospital(String.valueOf(i), "병원" + i, "부산 어딘가 " + i, Set.of(MedicalSpecialty.ETC)));
        }

        Page<Hospital> firstPage = hospitalRepository.search(null, null, PageRequest.of(0, 2));

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(5);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
    }

    private Hospital hospital(String regNo, String name, String address, Set<MedicalSpecialty> specialties) {
        return new Hospital(
            regNo,
            null,
            name,
            InstitutionType.CLINIC,
            address,
            new Coordinates(35.0, 129.0),
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
