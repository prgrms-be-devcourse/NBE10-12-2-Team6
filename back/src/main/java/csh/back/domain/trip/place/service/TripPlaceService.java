package csh.back.domain.trip.place.service;


import csh.back.domain.trip.place.dto.response.TripPlaceFindResponse;
import csh.back.domain.trip.place.dto.response.TripPlaceSaveResponse;
import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.place.repository.TripPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TripPlaceService {
    private final TripPlaceRepository tripPlaceRepository;
//    private final TripGroupRepository tripGroupRepository;

    public List<TripPlaceFindResponse> findWishPlaces(Long tripId) {
        List<TripPlace> tripPlaces = tripPlaceRepository.findAllByTripGroupId(tripId);
        return tripPlaces
                .stream()
                .map(TripPlaceFindResponse::from)
                .toList();
    }

    @Transactional
    public TripPlaceSaveResponse savePlace(Long tripId,
                                           String name,
                                           String theme,
                                           String address,
                                           String kakaoPlaceId,
                                           String kakaoUrl) {
    //        TripGroup tripGroup = tripGroupRepository.findById(tripId).orElseThrow(RuntimeException::new);
        TripPlace place = TripPlace
                .builder()
    //                .tripGroup(tripGroup)
                .name(name)
                .theme(theme)
                .address(address)
                .kakaoPlaceId(kakaoPlaceId)
                .kakaoMapUrl(kakaoUrl)
                .build();
        TripPlace saveResult = tripPlaceRepository.save(place);
        TripPlaceSaveResponse response = TripPlaceSaveResponse.from(saveResult);
        return response;
    }
}
