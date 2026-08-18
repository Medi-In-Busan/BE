package com.mediinbusan.backend.wellness.dto;

import java.util.List;

public record WellnessSnapshotIngestionResponse(int inserted, int updated, int failed, List<String> failures) {}
