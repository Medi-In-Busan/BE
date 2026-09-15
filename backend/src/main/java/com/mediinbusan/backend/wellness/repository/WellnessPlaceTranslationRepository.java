package com.mediinbusan.backend.wellness.repository;

import com.mediinbusan.backend.wellness.domain.WellnessPlaceTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WellnessPlaceTranslationRepository extends JpaRepository<WellnessPlaceTranslation, Long> {
    Optional<WellnessPlaceTranslation> findByContentIdAndLanguageCode(String contentId, String languageCode);

    /**
     * 여러 장소의 캐시를 한 번에 읽는다. 목록 응답(장소 2천여 건)에서 장소마다
     * {@link #findByContentIdAndLanguageCode}를 부르면 요청 한 번에 쿼리가 2천 번 나가 응답이
     * 수십 초로 늘어난다 — 실측으로 확인된 문제다(WellnessPlaceTranslationService 주석 참고).
     */
    List<WellnessPlaceTranslation> findByLanguageCodeAndContentIdIn(String languageCode, Collection<String> contentIds);
}

