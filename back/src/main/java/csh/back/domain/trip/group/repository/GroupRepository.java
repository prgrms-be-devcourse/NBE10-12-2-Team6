package csh.back.domain.trip.group.repository;

import csh.back.domain.trip.group.entity.TripGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<TripGroup, Long> {
}
