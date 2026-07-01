package csh.back.domain.trip.timeline.repository;

import csh.back.domain.trip.timeline.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeLineRepository extends JpaRepository<TimeLine, Long> {

    // 특정 여행 모임의 특정 일차 타임라인 목록을 시작 시간 기준으로 조회
    List<TimeLine> findByTripGroupIdAndDayNumberOrderByStartTimeAsc(Long tripId, int dayNumber);

    // 수정, 삭제하려는 타임라인이 해당 여행 모임에 속하는지 확인하면서 조회
    Optional<TimeLine> findByIdAndTripGroupId(Long timelineId, Long tripId);
}