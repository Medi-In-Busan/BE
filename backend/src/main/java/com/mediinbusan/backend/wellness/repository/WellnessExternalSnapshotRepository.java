package com.mediinbusan.backend.wellness.repository;

import com.mediinbusan.backend.wellness.domain.WellnessExternalSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WellnessExternalSnapshotRepository extends JpaRepository<WellnessExternalSnapshot, Long> {
    Optional<WellnessExternalSnapshot> findBySnapshotKey(String snapshotKey);

    Optional<WellnessExternalSnapshot> findTopBySourceAndScopeOrderBySyncedAtDesc(String source, String scope);
}
