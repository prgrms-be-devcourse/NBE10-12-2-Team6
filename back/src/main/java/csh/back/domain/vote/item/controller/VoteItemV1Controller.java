package csh.back.domain.vote.item.controller;

import csh.back.domain.vote.item.dto.request.VoteItemSaveRequestDto;
import csh.back.domain.vote.item.dto.response.VoteItemSaveResponseDto;
import csh.back.domain.vote.item.service.VoteItemService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class VoteItemV1Controller {
    private final VoteItemService voteItemService;

    @PostMapping("/votes")
    public ResponseData<VoteItemSaveResponseDto> saveVote(@RequestBody VoteItemSaveRequestDto request) {
        return new ResponseData<>(
                201,
                voteItemService.saveVoteItem(request.voteId(), request.placeId())
        );
    }
}
