package csh.back.domain.trip.place.service;


import csh.back.domain.trip.place.dto.response.PlaceFindItem;
import csh.back.domain.trip.place.dto.response.SaveResponse;
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
//    private final TripGroupRepository tripGroupRepository;

    public List<PlaceFindItem> findWishPlaces(Long tripId) {
        List<TripPlace> tripPlaces = placeRepository.findAllByTripGroup_Id(tripId);
        return tripPlaces
                .stream()
                .map(PlaceFindItem::from)
                .toList();
    }

    @Transactional
    public SaveResponse savePlace(Long tripId,
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
        TripPlace saveResult = placeRepository.save(place);
        SaveResponse response = SaveResponse.from(saveResult);
        return response;
    }
}
