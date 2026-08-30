package com.mediinbusan.backend.wellness.service;

import com.mediinbusan.backend.common.GeoDistance;
import com.mediinbusan.backend.hospital.domain.Coordinates;
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

    public List<WellnessPlaceResponse> getNearbyPlaces(String hospitalRegNo, Double radiusMeters, String lang) {
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
            .map(item -> WellnessDtoMapper.toPlaceResponse(item.place, item.distance, lang))
            .toList();
    }

    /**
     * 특정 병원에 종속되지 않은 웰니스 장소 조회 — 지도 "전체 브라우징" 화면(하단 탭 '지도', 병원
     * 미지정 진입)이 쓴다. 예전엔 이 화면이 병원 목록 맨 앞 병원 하나의 반경 3km(getNearbyPlaces)만
     * 빌려써서, 그 반경 밖에 있는 장소(예: 새로 upsert된 다른 구의 부산맛집 데이터)는 지도에 아예
     * 나타나지 않았다. latitude/longitude를 안 넘기면 전체를, 넘기면 기존 getNearbyPlaces와 같은
     * 방식(반경 필터 + 거리순 정렬)으로 반환한다.
     */
    public List<WellnessPlaceResponse> findPlaces(Double latitude, Double longitude, Double radiusMeters, String lang) {
        List<WellnessPlace> places = wellnessPlaceRepository.findAll();

        if (latitude == null || longitude == null) {
            return places.stream()
                .map(place -> WellnessDtoMapper.toPlaceResponse(place, null, lang))
                .toList();
        }

        Coordinates origin = new Coordinates(latitude, longitude);
        double effectiveRadius = radiusMeters != null ? radiusMeters : DEFAULT_RADIUS_METERS;

        return places.stream()
            .map(place -> new PlaceDistance(place, GeoDistance.meters(origin, place.getCoordinates())))
            .filter(item -> item.distance == null || item.distance <= effectiveRadius)
            .sorted(Comparator.comparing(
                PlaceDistance::distanceForSort,
                Comparator.nullsLast(Comparator.naturalOrder())
            ).thenComparing(item -> item.place.getId()))
            .map(item -> WellnessDtoMapper.toPlaceResponse(item.place, item.distance, lang))
            .toList();
    }

    public WellnessPlaceResponse getPlaceDetail(String contentId, String lang) {
        WellnessPlace place = wellnessPlaceRepository.findByContentId(contentId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "웰니스 장소를 찾을 수 없습니다: " + contentId));
        return WellnessDtoMapper.toPlaceResponse(place, null, lang);
    }

    private record PlaceDistance(WellnessPlace place, Double distance) {
        Double distanceForSort() {
            return distance;
        }
    }
}
