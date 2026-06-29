package csh.back.domain.trip.place.controller;

import csh.back.domain.trip.place.dto.PlaceResponseDto;
import csh.back.domain.trip.place.service.PlaceService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@RestController
public class PlaceV1Controller {
    private final PlaceService placeService;

    @GetMapping("/{tripId}/wish-places")
    public ResponseData<List<PlaceResponseDto.PlaceFindItem>> findWishPlaces(@PathVariable Long tripId) {
        List<PlaceResponseDto.PlaceFindItem> wishPlaces = placeService.findWishPlaces(tripId);
        return new ResponseData<>(200, wishPlaces);
    }
}
