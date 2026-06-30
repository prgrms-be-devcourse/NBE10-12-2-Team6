package csh.back.domain.vote.item.service;

import csh.back.domain.vote.item.repository.VoteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VoteItemService {
    private final VoteItemRepository voteItemRepository;
}
