package csh.back.domain.vote.vote.controller;

import csh.back.domain.vote.vote.dto.response.VoteFindResponse;
import csh.back.domain.vote.vote.service.VoteService;
import csh.back.global.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteV1Controller {
    private final VoteService voteService;

    @GetMapping("/{voteId}")
    public ResponseData<List<VoteFindResponse>> findVoteItemAndCount(@PathVariable Long voteId) {
        return new ResponseData<>(
                200,
                voteService.findVoteItemAndCount(voteId)
        );
    }
}
