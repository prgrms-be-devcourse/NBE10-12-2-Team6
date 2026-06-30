package csh.back.domain.trip.timeline.repository;

import csh.back.domain.trip.timeline.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeLineRepository extends JpaRepository<TimeLine, Long> {
}