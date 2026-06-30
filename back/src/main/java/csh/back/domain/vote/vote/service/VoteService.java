package csh.back.domain.vote.vote.service;

import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import csh.back.domain.vote.vote.dto.response.VoteFindResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteUserRepository voteUserRepository;
    private final VoteItemRepository voteItemRepository;

    public List<VoteFindResponse> findVoteItemAndCount(Long voteId) {
        List<VoteFindResponse> responseList = new ArrayList<>();
        List<VoteItem> voteItemList = voteItemRepository.findByVoteId(voteId);

        for(VoteItem voteItem : voteItemList) {
            Long voteCount = voteUserRepository.countByVoteItemId(voteItem.getId());
            String place = voteItem.getTripPlace().getName();
            responseList.add(VoteFindResponse.from(place, voteCount));
        }

        return responseList;
    }
}
