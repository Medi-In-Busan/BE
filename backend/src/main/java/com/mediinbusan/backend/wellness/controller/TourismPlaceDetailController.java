package com.mediinbusan.backend.wellness.controller;

import com.mediinbusan.backend.wellness.dto.TourismPlaceMatchResponse;
import com.mediinbusan.backend.wellness.service.BusanTourismCodes;
import com.mediinbusan.backend.wellness.service.TourismPlaceMatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wellness/tourism")
public class TourismPlaceDetailController {
    private final TourismPlaceMatchService service;

    public TourismPlaceDetailController(TourismPlaceMatchService service) {
        this.service = service;
    }

    @GetMapping("/matched-place")
    public TourismPlaceMatchResponse getMatchedPlace(
        @RequestParam String title,
        @RequestParam BusanTourismCodes.District district
    ) {
        return service.find(title, district);
    }
}
