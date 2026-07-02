package csh.back.domain.trip.timeline.repository;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.timeline.entity.TimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeLineRepository extends JpaRepository<TimeLine, Long> {

    //특정 여행 모임의 특정 일차 타임라인 목록을 시작 시간 기준으로 조회
    List<TimeLine> findByTripGroupIdAndDayNumberOrderByStartTimeAsc(Long tripId, int dayNumber);

    //수정, 삭제하려는 타임라인이 해당 여행 모임에 속하는지 확인하면서 조회
    Optional<TimeLine> findByIdAndTripGroupId(Long timelineId, Long tripId);

    //아래는 도저히 감이 안 잡혀 AI를 적극 활용했습니다.

    //같은 여행 모임, 같은 일차에 이미 겹치는 시간 구간이 있는지 확인
    boolean existsByTripGroupIdAndDayNumberAndStartTimeLessThanAndEndTimeGreaterThan(
            Long tripId,
            Integer dayNumber,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    //수정 시 자기 자신을 제외하고 같은 여행 모임, 같은 일차에 겹치는 시간 구간이 있는지 확인
    boolean existsByTripGroupIdAndDayNumberAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long tripId,
            Integer dayNumber,
            Long timelineId,
            LocalDateTime endTime,
            LocalDateTime startTime

    );
}