package com.mediinbusan.backend.wellness.repository;

import com.mediinbusan.backend.wellness.domain.TourismCatalogTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourismCatalogTranslationRepository extends JpaRepository<TourismCatalogTranslation, Long> {
    Optional<TourismCatalogTranslation> findByCategoryAndItemIdAndLanguageCode(
        String category,
        String itemId,
        String languageCode
    );
}
