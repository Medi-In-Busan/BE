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

    public static HospitalNearbyResponse toNearbyItem(Hospital hospital, Double distanceMeters) {
        return new HospitalNearbyResponse(
            hospital.getRegNo(),
            hospital.getName(),
            hospital.getInstitutionType().name(),
            hospital.getAddress(),
            latitude(hospital),
            longitude(hospital),
            hospital.getPhone(),
            sortedNames(hospital.getSpecialties()),
            distanceMeters
        );
    }

    public static HospitalDetailResponse toDetail(Hospital hospital, String lang) {
        return new HospitalDetailResponse(
            hospital.getRegNo(),
            hospital.getName(),
            hospital.getInstitutionType().name(),
            hospital.getAddress(),
            latitude(hospital),
            longitude(hospital),
            hospital.getPhone(),
            hospital.getWebsite(),
            businessHoursFor(hospital, lang),
            descriptionFor(hospital, lang),
            sortedNames(hospital.getSpecialties()),
            hospital.getTargetCountries().stream().sorted().toList()
        );
    }

    // 요청 언어의 번역이 비어 있으면(아직 번역 안 된 병원 등) ko로 폴백한다.
    private static String descriptionFor(Hospital hospital, String lang) {
        String translated = switch (lang) {
            case "en" -> hospital.getDescriptionEn();
            case "zh" -> hospital.getDescriptionZh();
            case "ja" -> hospital.getDescriptionJa();
            default -> hospital.getDescriptionKo();
        };
        return translated != null ? translated : hospital.getDescriptionKo();
    }

    private static String businessHoursFor(Hospital hospital, String lang) {
        String translated = switch (lang) {
            case "en" -> hospital.getBusinessHoursEn();
            case "zh" -> hospital.getBusinessHoursZh();
            case "ja" -> hospital.getBusinessHoursJa();
            default -> hospital.getBusinessHoursKo();
        };
        return translated != null ? translated : hospital.getBusinessHoursKo();
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
