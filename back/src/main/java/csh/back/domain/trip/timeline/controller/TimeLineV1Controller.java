package csh.back.domain.trip.timeline.controller;

import csh.back.domain.trip.timeline.dto.request.TimeLineAllCreateRequest;
import csh.back.domain.trip.timeline.dto.request.TimeLineConfirmPlaceRequest;
import csh.back.domain.trip.timeline.dto.request.TimeLineCreateRequest;
import csh.back.domain.trip.timeline.dto.request.TimeLineUpdateRequest;
import csh.back.domain.trip.timeline.dto.response.TimeLineResponse;
import csh.back.domain.trip.timeline.service.TimeLineService;
import csh.back.global.dto.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Swagger에서 여행 타임라인 API 그룹으로 표시
@Tag(name = "여행 타임라인", description = "단건 여행 타임라인 시간 구간 API")
@RequiredArgsConstructor
//공통 URL 경로 설정
@RequestMapping("/api/v1/trips/{tripId}/timelines")
//JSON 응답을 반환하는 REST API 컨트롤러
@RestController
public class TimeLineV1Controller {

    //타임라인 관련 비즈니스 로직을 처리하는 Service
    private final TimeLineService timeLineService;

    //Swagger 문서에 타임라인 생성 API 설명 표시
    @Operation(summary = "타임라인 시간 구간 단건 생성")
    //타임라인 시간 구간 등록
    @PostMapping
    public ResponseData<TimeLineResponse> createTimeLine(
            @PathVariable Long tripId,
            @RequestParam Long memberId,
            @Valid @RequestBody TimeLineCreateRequest request) {
        //로그인 기능 연동 후 memberId는 인증 정보에서 가져오도록 변경
        //ResponseData로 감싸서 201 Created 반환
        return new ResponseData<>(201, timeLineService.createTimeLine(tripId, memberId, request));
    }

    //타임라인 시간 구간 일괄 생성
    @Tag(name = "여행 타임라인", description = "일광 여행 타임라인 시간 구간 API")
    //Swagger 문서에 타임라인 생성 API 설명 표시
    @Operation(summary = "타임라인 시간 구간 일괄 생성")
    @PostMapping("/batch")
    public ResponseData<List<TimeLineResponse>> createAllTimeLines(
            @PathVariable Long tripId,
            @RequestParam Long memberId,
            @Valid @RequestBody TimeLineAllCreateRequest request) {

        //로그인 기능 연동 후 memberId는 인증 정보에서 가져오도록 변경
        //ResponseData로 감싸서 201 Created 반환
        return new ResponseData<>(201, timeLineService.createAllTimeLines(tripId, memberId, request));
    }

    //Swagger 문서에 일차별 타임라인 목록 조회 API 설명 표시
    @Operation(summary = "일차별 타임라인 시간 구간 목록 조회")
    //특정 여행 모임의 특정 일차 타임라인 목록 조회
    @GetMapping
    public ResponseData<List<TimeLineResponse>> getTimeLines(
            @PathVariable Long tripId,
            @RequestParam Long memberId,
            @RequestParam int dayNumber) {
        //로그인 기능 연동 후 memberId는 인증 정보에서 가져오도록 변경
        //ResponseData로 감싸서 200 OK 반환
        return new ResponseData<>(200, timeLineService.getTimeLines(tripId, memberId, dayNumber));
    }

    //Swagger 문서에 타임라인 수정 API 설명 표시
    @Operation(summary = "타임라인 시간 구간 수정")
    //특정 타임라인 시간 구간 수정
    @PatchMapping("/{timelineId}")
    public ResponseData<TimeLineResponse> updateTimeLine(
            @PathVariable Long tripId,
            @PathVariable Long timelineId,
            @RequestParam Long memberId,
            @Valid @RequestBody TimeLineUpdateRequest request) {
        //로그인 기능 연동 후 memberId는 인증 정보에서 가져오도록 변경
        return new ResponseData<>(200, timeLineService.updateTimeLine(tripId, timelineId, memberId, request));
    }

    //Swagger 문서에 확정 장소 반영 API 설명 표시
    @Operation(summary = "확정 장소 반영")
    //특정 여행 모임의 특정 타임라인 시간 구간에 확정 장소를 반영
    @PatchMapping("/{timelineId}/confirm-place")
    public ResponseData<TimeLineResponse> confirmTimeLinePlace(
            @PathVariable Long tripId,
            @PathVariable Long timelineId,
            @RequestParam Long memberId,
            @Valid @RequestBody TimeLineConfirmPlaceRequest request) {

        //확정 장소 반영 서비스 호출
        return new ResponseData<>(200, timeLineService.confirmTimeLinePlace(tripId, timelineId, memberId, request));
    }

    //Swagger 문서에 타임라인 삭제 API 설명 표시
    @Operation(summary = "타임라인 시간 구간 삭제")
    //특정 타임라인 시간 구간 삭제
    @DeleteMapping("/{timelineId}")
    public ResponseData<Void> deleteTimeLine(
            @PathVariable Long tripId,
            @PathVariable Long timelineId,
            @RequestParam Long memberId) {
        //로그인 기능 연동 후 memberId는 인증 정보에서 가져오도록 변경
        timeLineService.deleteTimeLine(tripId, timelineId, memberId);

        return new ResponseData<>(200, null);
    }
}