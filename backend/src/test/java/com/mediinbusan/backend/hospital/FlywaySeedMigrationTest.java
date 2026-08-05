package com.mediinbusan.backend.hospital;

import com.mediinbusan.backend.hospital.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 다른 테스트들은 ddl-auto=create-drop + flyway.enabled=false(src/test/resources/application.yml)로 도는데,
 * 이 테스트만 실제 V1/V2 마이그레이션 SQL이 깨지지 않고 122건을 그대로 적재하는지 확인한다.
 * H2를 MySQL 호환 모드로 띄워서 배포 대상(MySQL) SQL 문법을 최대한 그대로 검증한다.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:flyway-seed-check;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=validate",
    "spring.flyway.enabled=true"
})
@Transactional
class FlywaySeedMigrationTest {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Test
    void V1_V2_마이그레이션이_122건을_적재한다() {
        assertThat(hospitalRepository.count()).isEqualTo(122);
    }

    @Test
    void 시딩된_병원의_specialties와_targetCountries가_비어있지_않다() {
        var hospital = hospitalRepository.findByRegNo("1").orElseThrow();
        assertThat(hospital.getName()).isEqualTo("더큰아이소아청소년과의원");
        assertThat(hospital.getSpecialties()).isNotEmpty();
        assertThat(hospital.getTargetCountries()).isNotEmpty();
    }
}
