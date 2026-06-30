package csh.back.domain.trip.place.dto.request;

public record SaveRequest(String name,
                          String category,
                          String address,
                          String kakaoPlaceId,
                          String kakaoUrl
    ) {

    }