package com.mediinbusan.backend.hospital.repository;

import com.mediinbusan.backend.hospital.domain.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long>, HospitalSearchRepository {
    Optional<Hospital> findByRegNo(String regNo);
}
