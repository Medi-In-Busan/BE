package com.mediinbusan.backend.hospital.controller;

import com.mediinbusan.backend.common.PageResponse;
import com.mediinbusan.backend.hospital.domain.MedicalSpecialty;
import com.mediinbusan.backend.hospital.dto.HospitalDetailResponse;
import com.mediinbusan.backend.hospital.dto.HospitalListItemResponse;
import com.mediinbusan.backend.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Hospital", description = "의료기관 조회/검색")
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @Operation(summary = "의료기관 검색", description = "keyword는 병원명(제목) 부분일치, specialties는 콤마로 여러 개 넘기면 OR(IN) 조건으로 필터링한다.")
    @GetMapping
    public PageResponse<HospitalListItemResponse> search(
        @Parameter(description = "병원명(제목)에 대한 부분일치 검색어") @RequestParam(required = false) String keyword,
        @Parameter(description = "예: PLASTIC_SURGERY,SKIN_BEAUTY") @RequestParam(required = false) List<MedicalSpecialty> specialties,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return hospitalService.search(keyword, specialties, pageable);
    }

    @Operation(summary = "의료기관 상세 조회", description = "lang(ko/en/zh/ja, 기본값 ko)에 맞는 소개글·영업시간을 반환한다. 해당 언어 번역이 없으면 ko로 폴백한다.")
    @GetMapping("/{regNo}")
    public HospitalDetailResponse getDetail(
        @PathVariable String regNo,
        @RequestParam(defaultValue = "ko") String lang
    ) {
        return hospitalService.getDetail(regNo, lang);
    }
}
