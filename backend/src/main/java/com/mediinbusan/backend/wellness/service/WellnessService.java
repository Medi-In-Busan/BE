package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.common.GeoDistance;
import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.repository.HospitalRepository;
import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import com.mediinbusan.backend.wellness.dto.WellnessDtoMapper;
import com.mediinbusan.backend.wellness.dto.WellnessPlaceResponse;
import com.mediinbusan.backend.wellness.repository.WellnessPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class WellnessService {

    private static final double DEFAULT_RADIUS_METERS = 3_000.0;

    private final WellnessPlaceRepository wellnessPlaceRepository;
    private final HospitalRepository hospitalRepository;

    public WellnessService(WellnessPlaceRepository wellnessPlaceRepository, HospitalRepository hospitalRepository) {
        this.wellnessPlaceRepository = wellnessPlaceRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public List<WellnessPlaceResponse> getNearbyPlaces(String hospitalRegNo, Double radiusMeters) {
        Hospital hospital = hospitalRepository.findByRegNo(hospitalRegNo)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "병원을 찾을 수 없습니다: " + hospitalRegNo));
        double effectiveRadius = radiusMeters != null ? radiusMeters : DEFAULT_RADIUS_METERS;

        return wellnessPlaceRepository.findAll().stream()
            .map(place -> new PlaceDistance(place, GeoDistance.meters(hospital.getCoordinates(), place.getCoordinates())))
            .filter(item -> item.distance == null || item.distance <= effectiveRadius)
            .sorted(Comparator.comparing(
                PlaceDistance::distanceForSort,
                Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparing(item -> item.place.getId()))
            .map(item -> WellnessDtoMapper.toPlaceResponse(item.place, item.distance))
            .toList();
    }

    public WellnessPlaceResponse getPlaceDetail(String contentId) {
        WellnessPlace place = wellnessPlaceRepository.findByContentId(contentId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "웰니스 장소를 찾을 수 없습니다: " + contentId));
        return WellnessDtoMapper.toPlaceResponse(place, null);
    }

    private record PlaceDistance(WellnessPlace place, Double distance) {
        Double distanceForSort() {
            return distance;
        }
    }
}
