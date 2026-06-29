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
}
