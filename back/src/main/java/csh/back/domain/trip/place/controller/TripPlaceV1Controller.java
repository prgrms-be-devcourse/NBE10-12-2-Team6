package csh.back.domain.trip.place.controller;

import csh.back.domain.trip.place.dto.request.TripPlaceSaveRequest;
import csh.back.domain.trip.place.dto.response.TripPlaceFindResponse;
import csh.back.domain.trip.place.dto.response.TripPlaceSaveResponse;
import csh.back.domain.trip.place.service.TripPlaceService;

import csh.back.global.annotation.ApiV1;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiV1
@RequestMapping("/trips")
@RequiredArgsConstructor
@RestController
public class TripPlaceV1Controller {
    private final TripPlaceService tripPlaceService;

    @GetMapping("/{tripId}/wish-places")
    public ResponseData<List<TripPlaceFindResponse>> findWishPlaces(@PathVariable Long tripId) {
        return new ResponseData<>(
                200,
                tripPlaceService.findWishPlaces(tripId)
        );
    }


    @PostMapping("/{tripId}/wish-places")
    public ResponseData<TripPlaceSaveResponse> saveWishPlace(@RequestBody TripPlaceSaveRequest request, @PathVariable Long tripId) {
        return new ResponseData(
                200,
                tripPlaceService.savePlace(
                        tripId,
                        request.name(),
                        request.category(),
                        request.address(),
                        request.kakaoPlaceId(),
                        request.kakaoMapUrl()
                )
        );
    }
}
