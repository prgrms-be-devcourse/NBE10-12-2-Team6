package csh.back.domain.vote.item.controller;

import csh.back.domain.vote.item.service.VoteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class VoteItemV1Controller {
    private final VoteItemService voteItemService;

    @PostMapping("/tripId/votes")
    public void saveVote() {
        return;
    }
}
