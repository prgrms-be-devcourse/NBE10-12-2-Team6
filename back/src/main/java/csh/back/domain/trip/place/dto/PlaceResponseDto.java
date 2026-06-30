package csh.back.domain.trip.place.dto;

import csh.back.domain.trip.place.entity.TripPlace;

public class PlaceResponseDto {
    public record PlaceFindItem(Long placeId,
                                String name,
                                String theme
                                ) {
        public static PlaceFindItem from(TripPlace tripPlace) {
            return new PlaceFindItem(
                    tripPlace.getId(),
                    tripPlace.getName(),
                    tripPlace.getTheme()
            );
        }
    }

    public record SaveResponse(Long id,
                               String name,
                               String category,
                               String address
    ) {
        public static SaveResponse from(TripPlace tripPlace) {
            return new SaveResponse(
                    tripPlace.getId(),
                    tripPlace.getName(),
                    tripPlace.getTheme(),
                    tripPlace.getAddress()
            );
        }
    }
}
