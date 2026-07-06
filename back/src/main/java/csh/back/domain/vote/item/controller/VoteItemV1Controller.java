package csh.back.domain.vote.item.controller;

import csh.back.domain.member.dto.response.AuthFilterDto;
import csh.back.domain.vote.item.dto.request.VoteItemSaveRequestDto;
import csh.back.domain.vote.item.service.VoteItemService;
import csh.back.domain.vote.user.dto.response.VoteUserSaveResponseDto;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/votes/{voteId}")
@RequiredArgsConstructor
public class VoteItemV1Controller {
    private final VoteItemService voteItemService;

    @PostMapping
    public ResponseData<VoteUserSaveResponseDto> saveVote(
            @PathVariable Long tripId,
            @PathVariable Long voteId,
            @RequestBody VoteItemSaveRequestDto request,
            @AuthenticationPrincipal AuthFilterDto member) {
        return new ResponseData<>(
                201,
                voteItemService.saveVoteItem(tripId, member.id(), voteId, request.placeId())
        );
    }
}
