package csh.back.domain.trip.place.service;


import csh.back.domain.trip.group.entity.TripGroup;
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

//    private final TripGroupRepository tripGroupRepository;

    @Transactional
    public PlaceResponseDto.SaveResponse savePlace(Long tripId,
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
        PlaceResponseDto.SaveResponse response = PlaceResponseDto.SaveResponse.from(saveResult);
        return response;
    }
}
