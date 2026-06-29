package csh.back.domain.trip.place.controller;

import csh.back.domain.trip.place.dto.PlaceRequestDto;
import csh.back.domain.trip.place.dto.PlaceResponseDto;
import csh.back.domain.trip.place.service.PlaceService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trips/")
@RequiredArgsConstructor
public class PlaceV1Controller {
    private final PlaceService placeService;

    @RequestMapping("/{tripId}/wish-places")
    public ResponseData saveWishPlace(@RequestBody PlaceRequestDto.SaveRequest request, @PathVariable Long tripId) {
        PlaceResponseDto.SaveResponse response = placeService.savePlace(
                tripId,
                request.name(),
                request.category(),
                request.address(),
                request.kakaoPlaceId(),
                request.kakaoUrl()
        );
        return new ResponseData(200, response);
    }

}
