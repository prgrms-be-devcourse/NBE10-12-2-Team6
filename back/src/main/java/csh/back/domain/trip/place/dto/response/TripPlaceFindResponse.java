package csh.back.domain.trip.place.dto.response;

import csh.back.domain.trip.place.entity.TripPlace;

public record TripPlaceFindResponse(Long placeId,
                                    String name,
                                    String theme
                                ) {
        public static TripPlaceFindResponse from(TripPlace tripPlace) {
            return new TripPlaceFindResponse(
                    tripPlace.getId(),
                    tripPlace.getName(),
                    tripPlace.getTheme()
            );
        }
    }


