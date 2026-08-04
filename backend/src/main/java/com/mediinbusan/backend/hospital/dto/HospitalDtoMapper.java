package com.mediinbusan.backend.hospital.dto;

import com.mediinbusan.backend.hospital.domain.Hospital;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;

import java.util.Comparator;
import java.util.List;

/** meta(verifiedAt/verifiedBy/notes), originalRegNo는 여기서 걸러지고 응답 DTO로 넘어가지 않는다. */
public final class HospitalDtoMapper {

    private HospitalDtoMapper() {
    }

    public static HospitalListItemResponse toListItem(Hospital hospital) {
        return new HospitalListItemResponse(
            hospital.getRegNo(),
            hospital.getName(),
            hospital.getInstitutionType().name(),
            hospital.getAddress(),
            latitude(hospital),
            longitude(hospital),
            hospital.getPhone(),
            sortedNames(hospital.getSpecialties())
        );
    }

    public static HospitalDetailResponse toDetail(Hospital hospital) {
        return new HospitalDetailResponse(
            hospital.getRegNo(),
            hospital.getName(),
            hospital.getInstitutionType().name(),
            hospital.getAddress(),
            latitude(hospital),
            longitude(hospital),
            hospital.getPhone(),
            hospital.getWebsite(),
            hospital.getBusinessHours(),
            hospital.getDescriptionKo(),
            hospital.getDescriptionEn(),
            sortedNames(hospital.getSpecialties()),
            hospital.getTargetCountries().stream().sorted().toList()
        );
    }

    private static Double latitude(Hospital hospital) {
        return hospital.getCoordinates() != null ? hospital.getCoordinates().getLatitude() : null;
    }

    private static Double longitude(Hospital hospital) {
        return hospital.getCoordinates() != null ? hospital.getCoordinates().getLongitude() : null;
    }

    private static List<String> sortedNames(java.util.Set<MedicalSpecialty> specialties) {
        return specialties.stream().map(Enum::name).sorted(Comparator.naturalOrder()).toList();
    }
}
