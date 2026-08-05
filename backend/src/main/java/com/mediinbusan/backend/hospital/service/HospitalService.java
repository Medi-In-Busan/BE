package com.mediinbusan.backend.hospital.service;

import com.mediinbusan.backend.common.PageResponse;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.dto.HospitalDetailResponse;
import com.mediinbusan.backend.hospital.dto.HospitalDtoMapper;
import com.mediinbusan.backend.hospital.dto.HospitalListItemResponse;
import com.mediinbusan.backend.hospital.repository.HospitalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public PageResponse<HospitalListItemResponse> search(String keyword, List<MedicalSpecialty> specialties, Pageable pageable) {
        Page<Hospital> page = hospitalRepository.search(keyword, specialties, pageable);
        return PageResponse.from(page.map(HospitalDtoMapper::toListItem));
    }

    public HospitalDetailResponse getDetail(String regNo) {
        Hospital hospital = hospitalRepository.findByRegNo(regNo)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "병원을 찾을 수 없습니다: " + regNo));
        return HospitalDtoMapper.toDetail(hospital);
    }
}
