package csh.back.domain.trip.group.repository;

import csh.back.domain.trip.group.entity.TripGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<TripGroup, Long> {
	List<TripGroup> findAllByOwnerIdOrderByStartDateDesc(Long ownerId);
}
