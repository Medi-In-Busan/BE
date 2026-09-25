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

/**
 * 트랜잭션은 메서드마다 명시한다(클래스 레벨에 두지 않는다) — getPlaceDetail은 트랜잭션 스코프 자체가 없어야
 * 해서다. 그 메서드 주석 참고.
 */
@Service
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

    @Transactional(readOnly = true)
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
                item.distance,
                language
            ))
            .toList();

        return translationService.localizeAllFromCache(
            places,
            language
        );
    }

    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
                        null,
                        language
                    )
                )
                .toList();

            return translationService.localizeAllFromCache(
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
                item.distance,
                language
            ))
            .toList();

        return translationService.localizeAllFromCache(
            responses,
            language
        );
    }

    /**
     * 클래스 레벨 readOnly 트랜잭션에서 뺀다 — 이 경로는 캐시 미스 시 Papago(외부 HTTP)를 부르고 번역을 저장한다.
     * 트랜잭션 안에 두면 커넥션을 Papago 응답까지 쥐고, 번역 저장이 두 번째 커넥션을 요구해 요청 한 건이
     * 커넥션 2개를 점유했다. 동시 요청이 풀 크기를 넘으면 전부 첫 커넥션을 쥔 채 두 번째를 기다리는 교착이
     * 되어 커넥션 획득 타임아웃으로만 풀렸다(k6 부하에서 상세 63% 실패 — WellnessDetailConnectionPoolTest 참고).
     * 지금은 조회·캐시 읽기·저장이 각자 리포지토리 호출 단위로 커넥션을 잠깐씩만 쓴다.
     * WellnessPlace는 지연 로딩 연관이 없어 트랜잭션 밖에서 DTO로 바꿔도 안전하다.
     *
     * <b>NOT_SUPPORTED/SUPPORTS로 "트랜잭션 없음"을 표시하면 안 된다.</b> 그 둘도 트랜잭션 동기화 스코프는 열어서
     * 공유 EntityManager가 메서드 끝까지 스레드에 묶이고, Hibernate가 첫 조회에서 잡은 커넥션을 그 EntityManager가
     * 닫힐 때까지 놓지 않는다 — 결국 Papago를 기다리는 내내 커넥션을 쥐어 교착이 그대로 남는다(Hikari 누수 감지로
     * 확인). 그래서 이 메서드에는 트랜잭션 어노테이션을 아예 두지 않는다.
     */
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
                null,
                language
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