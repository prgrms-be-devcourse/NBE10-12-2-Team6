package csh.back.domain.vote.vote.controller;

import csh.back.domain.member.dto.response.AuthFilterDto;
import csh.back.domain.trip.timeline.service.TimeLineService;
import csh.back.domain.vote.vote.dto.request.VoteConfirmPlaceRequest;
import csh.back.domain.vote.vote.dto.request.VoteCreateRequest;
import csh.back.domain.vote.vote.dto.response.*;
import csh.back.domain.vote.vote.service.VoteService;
import csh.back.global.annotation.ApiV1;
import csh.back.global.dto.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV1
@RestController
@RequiredArgsConstructor
@RequestMapping("/trips/{tripId}/votes")
public class VoteV1Controller {
    private final VoteService voteService;
    private final TimeLineService timeLineService;

    @GetMapping
    public ResponseData<List<VoteFindListResponse>> findVoteList(
            @PathVariable Long tripId,
            @AuthenticationPrincipal AuthFilterDto member
    ) {
        return new ResponseData<>(200, voteService.findVoteList(tripId, member.id()));
    }

    @PostMapping
    public ResponseData<VoteCreateResponse> createVote(
            @PathVariable Long tripId,
            @RequestBody VoteCreateRequest request,
            @AuthenticationPrincipal AuthFilterDto member
    ) {

        return new ResponseData<>(
                201,
                voteService.wrapperCreateVote(tripId, member.id(), request.timeLineId())
        );
    }

    @GetMapping("/{voteId}/count")
    public ResponseData<VoteFindWithUpdateCountResponse> findVoteItemAndCount(
            @PathVariable Long tripId,
            @PathVariable Long voteId,
            @AuthenticationPrincipal AuthFilterDto member
    ) {
        return new ResponseData<>(
                200,
                voteService.findVoteItemAndCount(tripId, voteId, member.id())
        );
    }

    @GetMapping("/{voteId}/places/{placeId}")
    public ResponseData<List<VoteFindUserResponse>> findVoteUserThisPlace(
            @PathVariable Long tripId,
            @PathVariable Long voteId,
            @PathVariable Long placeId,
            @AuthenticationPrincipal AuthFilterDto member
    ) {
        return new ResponseData<>(200, voteService.findUserVoteThisPlace(tripId, voteId, placeId, member.id()));
    }

    @Operation(summary = "투표 결과 장소 확정")
    @PatchMapping("/{voteId}/confirm")
    public ResponseData<VoteConfirmResponse> confirmVote(
            @PathVariable Long tripId,
            @PathVariable Long voteId,
            @AuthenticationPrincipal AuthFilterDto member
    ) {
        //확정 장소 반영 서비스 호출
        return new ResponseData<>(200, timeLineService.confirmVote(tripId, member.id(), voteId));
    }

//    //Swagger 문서에 확정 장소 반영 API 설명 표시
//    @Operation(summary = "투표 결과 동점인 장소 확정")
//    //특정 여행 모임의 특정 타임라인 시간 구간에 확정 장소를 반영
//    @PatchMapping("/{voteId}/confirm-tie")
//    public ResponseData<VoteConfirmResponse> confirmTiedVote(
//            @PathVariable Long tripId,
//            @PathVariable Long voteId,
//            @AuthenticationPrincipal AuthFilterDto member,
//            @Valid @RequestBody VoteConfirmPlaceRequest request) {
//        //확정 장소 반영 서비스 호출
//        return new ResponseData<>(200, timeLineService.confirmTiedVote(tripId, member.id(), voteId, request.confirmedPlaceId()));
//    }
}
