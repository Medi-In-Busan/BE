package com.mediinbusan.backend.hospital.repository;

import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalSearchRepository {

    /**
     * keyword는 병원명(제목)에 대한 부분일치, specialties는 IN(OR) 조건으로 필터링한다.
     * 주소까지 포함하면 "부산" 같은 흔한 지명 때문에 사실상 전체가 매칭돼서 제목만 본다.
     * 둘 다 없으면 전체 목록을 페이지네이션해서 반환한다.
     */
    Page<Hospital> search(String keyword, List<MedicalSpecialty> specialties, Pageable pageable);
}
