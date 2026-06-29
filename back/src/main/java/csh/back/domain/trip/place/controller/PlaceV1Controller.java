package csh.back.domain.trip.place.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trips/")
@RequiredArgsConstructor
public class PlaceV1Controller {

    @RequestMapping("/{tripId}/wish-places")
    public void saveWishPlace(@RequestBody @PathVariable String tripId) {}

}
