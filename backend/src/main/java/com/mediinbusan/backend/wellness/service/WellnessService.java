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
    private final WellnessPlaceTranslationService translationService;

    public WellnessService(
        WellnessPlaceRepository wellnessPlaceRepository,
        HospitalRepository hospitalRepository,
        WellnessPlaceTranslationService translationService
    ) {
        this.wellnessPlaceRepository = wellnessPlaceRepository;
        this.hospitalRepository = hospitalRepository;
        this.translationService = translationService;
    }

    public List<WellnessPlaceResponse> getNearbyPlaces(
        String hospitalRegNo,
        Double radiusMeters,
        String language
    ) {
        Hospital hospital = hospitalRepository.findByRegNo(hospitalRegNo)
            .orElseThrow(() -> new ResponseStatusException(
                NOT_FOUND,
                "병원을 찾을 수 없습니다: " + hospitalRegNo
            ));

        double effectiveRadius = radiusMeters != null
            ? radiusMeters
            : DEFAULT_RADIUS_METERS;

        List<WellnessPlaceResponse> places = wellnessPlaceRepository
            .findAll()
            .stream()
            .map(place -> new PlaceDistance(
                place,
                GeoDistance.meters(
                    hospital.getCoordinates(),
                    place.getCoordinates()
                )
            ))
            .filter(item ->
                item.distance == null ||
                    item.distance <= effectiveRadius
            )
            .sorted(
                Comparator.comparing(
                    PlaceDistance::distanceForSort,
                    Comparator.nullsLast(
                        Comparator.naturalOrder()
                    )
                ).thenComparing(item -> item.place.getId())
            )
            .map(item -> WellnessDtoMapper.toPlaceResponse(
                item.place,
                item.distance
            ))
            .toList();

        return translationService.localizeAll(
            places,
            language
        );
    }

    public List<WellnessPlaceResponse> getNearbyPlaces(
        String hospitalRegNo,
        Double radiusMeters
    ) {
        return getNearbyPlaces(
            hospitalRegNo,
            radiusMeters,
            "ko"
        );
    }

    /**
     * 병원에 종속되지 않은 웰니스 장소를 조회한다.
     * 좌표가 없으면 전체 장소를 반환하고, 좌표가 있으면 반경 내 장소를 거리순으로 반환한다.
     */
    public List<WellnessPlaceResponse> findPlaces(
        Double latitude,
        Double longitude,
        Double radiusMeters,
        String language
    ) {
        List<WellnessPlace> places =
            wellnessPlaceRepository.findAll();

        if (latitude == null || longitude == null) {
            List<WellnessPlaceResponse> responses = places
                .stream()
                .map(place ->
                    WellnessDtoMapper.toPlaceResponse(
                        place,
                        null
                    )
                )
                .toList();

            return translationService.localizeAll(
                responses,
                language
            );
        }

        Coordinates origin = new Coordinates(
            latitude,
            longitude
        );

        double effectiveRadius = radiusMeters != null
            ? radiusMeters
            : DEFAULT_RADIUS_METERS;

        List<WellnessPlaceResponse> responses = places
            .stream()
            .map(place -> new PlaceDistance(
                place,
                GeoDistance.meters(
                    origin,
                    place.getCoordinates()
                )
            ))
            .filter(item ->
                item.distance == null ||
                    item.distance <= effectiveRadius
            )
            .sorted(
                Comparator.comparing(
                    PlaceDistance::distanceForSort,
                    Comparator.nullsLast(
                        Comparator.naturalOrder()
                    )
                ).thenComparing(item -> item.place.getId())
            )
            .map(item -> WellnessDtoMapper.toPlaceResponse(
                item.place,
                item.distance
            ))
            .toList();

        return translationService.localizeAll(
            responses,
            language
        );
    }

    public WellnessPlaceResponse getPlaceDetail(
        String contentId,
        String language
    ) {
        WellnessPlace place = wellnessPlaceRepository
            .findByContentId(contentId)
            .orElseThrow(() -> new ResponseStatusException(
                NOT_FOUND,
                "웰니스 장소를 찾을 수 없습니다: " + contentId
            ));

        WellnessPlaceResponse response =
            WellnessDtoMapper.toPlaceResponse(
                place,
                null
            );

        return translationService.localize(
            response,
            language
        );
    }

    public WellnessPlaceResponse getPlaceDetail(
        String contentId
    ) {
        return getPlaceDetail(contentId, "ko");
    }

    private record PlaceDistance(
        WellnessPlace place,
        Double distance
    ) {
        Double distanceForSort() {
            return distance;
        }
    }
}