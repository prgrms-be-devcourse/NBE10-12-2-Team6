package csh.back.domain.trip.place.repository;

import csh.back.domain.trip.place.entity.TripPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<TripPlace, Long> {
    List<TripPlace> findAllByTripGroup_Id(Long tripId);
}
