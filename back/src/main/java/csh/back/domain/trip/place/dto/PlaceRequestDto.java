package csh.back.domain.trip.place.dto;

public class PlaceRequestDto {
    public record SaveRequest(String name,
                              String category,
                              String address,
                              String kakaoPlaceId,
                              String kakaoUrl
    ) {

    }
}
