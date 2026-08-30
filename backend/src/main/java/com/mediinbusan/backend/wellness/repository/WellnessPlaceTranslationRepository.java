package com.mediinbusan.backend.wellness.repository;

import com.mediinbusan.backend.wellness.domain.WellnessPlaceTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WellnessPlaceTranslationRepository extends JpaRepository<WellnessPlaceTranslation, Long> {
    Optional<WellnessPlaceTranslation> findByContentIdAndLanguageCode(String contentId, String languageCode);
}

