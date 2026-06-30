package csh.back.domain.trip.place.dto.response;

import csh.back.domain.trip.place.entity.TripPlace;

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