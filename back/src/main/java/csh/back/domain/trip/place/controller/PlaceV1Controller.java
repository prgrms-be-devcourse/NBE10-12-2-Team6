package csh.back.domain.trip.place.controller;

import csh.back.domain.trip.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/trips")
@RestController
public class PlaceV1Controller {
    private final PlaceService placeService;

    @GetMapping("/{tripId}/wish-places")
    public void findWishPlaces(@PathVariable Long tripId) {

    }
}
