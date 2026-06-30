package csh.back.domain.trip.timeline.service;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.repository.TripGroupRepository;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.timeline.dto.request.TimeLineCreateRequest;
import csh.back.domain.trip.timeline.dto.request.TimeLineUpdateRequest;
import csh.back.domain.trip.timeline.dto.response.TimeLineResponse;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeLineService {

    //DB 접근을 위한 Repository를 가져옴
    private final TimeLineRepository timeLineRepository;
    private final TripGroupRepository tripGroupRepository;
    private final TripMemberRepository tripMemberRepository;

    public TimeLineResponse createTimeLine(Long tripId, Long memberId, TimeLineCreateRequest request) {
        //여행 모임 방장 여부 검증
        validateTripAdmin(tripId, memberId);

        //tripId로 여행 모임 조회
        TripGroup tripGroup = tripGroupRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행 모임을 찾을 수 없습니다."));

        //타임라인 구간 생성
        TimeLine timeLine = TimeLine.builder()
                .tripGroup(tripGroup)
                .dayNumber(request.dayNumber())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        TimeLine savedTimeLine = timeLineRepository.save(timeLine);

        return TimeLineResponse.from(savedTimeLine);
    }

    @Transactional(readOnly = true)
    public List<TimeLineResponse> getTimeLines(Long tripId, Long memberId, int dayNumber) {
        //여행 모임 멤버 검증 여부 추가
        validateTripMember(tripId, memberId);

        //dayNumber 검증
        if (dayNumber < 1) {
            throw new IllegalArgumentException("일차는 1 이상이어야 합니다.");
        }
        //tripId + dayNumber로 목록 조회
        //TimeLineResponse 리스트로 변환
        return timeLineRepository.findByTripGroupIdAndDayNumberOrderByStartTimeAsc(tripId, dayNumber)
                .stream()
                .map(TimeLineResponse::from)
                .toList();

    }

    public TimeLineResponse updateTimeLine(Long tripId, Long timelineId, Long memberId, TimeLineUpdateRequest request) {
        //여행 모임 멤버 여부 검증 추가
        validateTripMember(tripId, memberId);

        //tripId와 timelineId가 모두 일치하는 타임라인 조회
        TimeLine timeLine = timeLineRepository.findByIdAndTripGroupId(timelineId, tripId)
                .orElseThrow(() -> new IllegalArgumentException("타임라인을 찾을 수 없습니다."));

        // 시간 범위 수정
        timeLine.updateTimeRange(request.startTime(), request.endTime());

        // 수정된 타임라인 응답 반환
        return TimeLineResponse.from(timeLine);
    }

    public void deleteTimeLine(Long tripId, Long timelineId, Long memberId) {
        //여행 모임 방장 여부 검증
        validateTripAdmin(tripId, memberId);

        //tripId와 timeLineId가 모두 일치하는 타임라인 조회
        TimeLine timeLine = timeLineRepository.findByIdAndTripGroupId(timelineId,tripId)
                .orElseThrow(() -> new IllegalArgumentException("타임라인을 찾을 수 없습니다."));

        //타임라인 제거
        timeLineRepository.delete(timeLine);
    }

    //여행 모임 멤버 여부 검증
    private void validateTripMember(Long tripId, Long memberId) {
        boolean isMember = tripMemberRepository.existsByTripGroupIdAndOwnerId(tripId, memberId);

        if (!isMember) {
            throw new IllegalArgumentException("여행 모임 멤버만 접근할 수 있습니다.");
        }
    }

    //방장 여부 검증
    private void validateTripAdmin(Long tripId, Long memberId) {
        boolean isAdmin = tripMemberRepository.existsByTripGroupIdAndOwnerIdAndIsAdminTrue(tripId, memberId);

        if (!isAdmin) {
            throw new IllegalArgumentException("여행 모임 방장만 접근할 수 있습니다.");
        }
    }
}
