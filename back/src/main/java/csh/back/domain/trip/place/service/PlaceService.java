package csh.back.domain.trip.place.service;

import csh.back.domain.trip.place.dto.PlaceResponseDto;
import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public List<PlaceResponseDto.PlaceFindItem> findWishPlaces(Long tripId) {
        List<TripPlace> tripPlaces = placeRepository.findAllByTripGroup_Id(tripId);
        return tripPlaces
                .stream()
                .map(PlaceResponseDto.PlaceFindItem::from)
                .toList();
    }

}

