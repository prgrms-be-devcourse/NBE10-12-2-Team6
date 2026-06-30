package csh.back.domain.vote.item.service;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.place.repository.PlaceRepository;
import csh.back.domain.vote.item.dto.response.VoteItemSaveResponseDto;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.dto.response.VoteUserSaveResponseDto;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.service.VoteUserService;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.domain.vote.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VoteItemService {
    private final VoteItemRepository voteItemRepository;
    private final VoteRepository voteRepository;
    private final PlaceRepository placeRepository;
    private final VoteUserService voteUserService;

    @Transactional
    public VoteUserSaveResponseDto saveVoteItem(Long voteId, Long placeId) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(RuntimeException::new);
        TripPlace tripPlace = placeRepository.findById(placeId).orElseThrow(RuntimeException::new);
        VoteItem voteItem = VoteItem
                .builder()
                .tripPlace(tripPlace)
                .vote(vote)
                .build();
        VoteItem saved = voteItemRepository.save(voteItem);
        return voteUserService.saveVoteUser(vote, saved);
    }
}
