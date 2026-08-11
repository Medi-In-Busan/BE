package com.mediinbusan.backend.hospital.service;

import com.mediinbusan.backend.common.GeoDistance;
import com.mediinbusan.backend.common.PageResponse;
import com.mediinbusan.backend.hospital.domain.Coordinates;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.dto.HospitalDetailResponse;
import com.mediinbusan.backend.hospital.dto.HospitalDtoMapper;
import com.mediinbusan.backend.hospital.dto.HospitalListItemResponse;
import com.mediinbusan.backend.hospital.dto.HospitalNearbyResponse;
import com.mediinbusan.backend.hospital.repository.HospitalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class HospitalService {

    private static final double DEFAULT_NEARBY_RADIUS_METERS = 5_000.0;

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public PageResponse<HospitalListItemResponse> search(String keyword, List<MedicalSpecialty> specialties, Pageable pageable) {
        Page<Hospital> page = hospitalRepository.search(keyword, specialties, pageable);
        return PageResponse.from(page.map(HospitalDtoMapper::toListItem));
    }

    /** 임의 좌표(지도 카메라 중심 등) 기준 반경 내 병원을 거리순으로 반환한다. 사용자 실시간 GPS 위치는 받지 않는다 — 좌표는 호출자가 넘긴다. */
    public List<HospitalNearbyResponse> findNearby(double latitude, double longitude, Double radiusMeters, List<MedicalSpecialty> specialties) {
        Coordinates origin = new Coordinates(latitude, longitude);
        double effectiveRadius = radiusMeters != null ? radiusMeters : DEFAULT_NEARBY_RADIUS_METERS;
        boolean hasSpecialtyFilter = specialties != null && !specialties.isEmpty();

        return hospitalRepository.findAll().stream()
            .filter(hospital -> !hasSpecialtyFilter || hospital.getSpecialties().stream().anyMatch(specialties::contains))
            .map(hospital -> new HospitalDistance(hospital, GeoDistance.meters(origin, hospital.getCoordinates())))
            .filter(item -> item.distance != null && item.distance <= effectiveRadius)
            .sorted(Comparator.comparing(HospitalDistance::distance))
            .map(item -> HospitalDtoMapper.toNearbyItem(item.hospital, item.distance))
            .toList();
    }

    private record HospitalDistance(Hospital hospital, Double distance) {
    }

    public HospitalDetailResponse getDetail(String regNo, String lang) {
        Hospital hospital = hospitalRepository.findByRegNo(regNo)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "병원을 찾을 수 없습니다: " + regNo));
        return HospitalDtoMapper.toDetail(hospital, lang);
    }
}
