package csh.back.domain.vote.vote.controller;

import csh.back.domain.member.dto.response.AuthFilterDto;
import csh.back.domain.vote.vote.dto.response.VoteFindListResponse;
import csh.back.domain.vote.vote.dto.response.VoteFindResponse;
import csh.back.domain.vote.vote.dto.response.VoteFindUserResponse;
import csh.back.domain.vote.vote.service.VoteService;
import csh.back.global.annotation.ApiV1;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV1
@RestController
@RequiredArgsConstructor
@RequestMapping("/trip/votes")
public class VoteV1Controller {
    private final VoteService voteService;

    @GetMapping("{tripId}/votes")
    public ResponseData<List<VoteFindListResponse>> findVoteList(
            @PathVariable Long tripId,
            @AuthenticationPrincipal AuthFilterDto member) {
        return new ResponseData<>(200, voteService.findVoteList(tripId, member.id()));
    }

    @GetMapping("/{voteId}")
    public ResponseData<List<VoteFindResponse>> findVoteItemAndCount(
            @PathVariable Long voteId,
            @AuthenticationPrincipal AuthFilterDto member) {
        return new ResponseData<>(
                200,
                voteService.findVoteItemAndCount(voteId, member.id())
        );
    }

    @GetMapping("/{voteId}/places/{placeId}")
    public ResponseData<List<VoteFindUserResponse>> findVoteUserThisPlace(
            @PathVariable Long voteId,
            @PathVariable Long placeId,
            @AuthenticationPrincipal AuthFilterDto member) {
        return new ResponseData<>(200, voteService.findUserVoteThisPlace(voteId, placeId, member.id()));
    }


}
