package com.mediinbusan.backend.wellness.repository;

import com.mediinbusan.backend.wellness.domain.WellnessPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WellnessPlaceRepository extends JpaRepository<WellnessPlace, Long> {
    Optional<WellnessPlace> findByContentId(String contentId);
    boolean existsByContentId(String contentId);
}
