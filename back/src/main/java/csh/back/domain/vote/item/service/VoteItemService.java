package csh.back.domain.vote.item.service;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.place.repository.PlaceRepository;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.dto.response.VoteUserSaveResponseDto;
import csh.back.domain.vote.user.service.VoteUserService;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.domain.vote.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.info("장소 아이디 값 : {}", placeId.toString());
        log.info("투표 아이디 값 : {}", voteId.toString());
        Vote vote = voteRepository.getReferenceById(voteId);
        TripPlace tripPlace = placeRepository.findById(placeId).orElseThrow(RuntimeException::new);
        VoteItem voteItem = voteItemRepository.findByTripPlaceId(placeId);
        if (voteItem == null) {
            voteItem = VoteItem
                    .builder()
                    .tripPlace(tripPlace)
                    .vote(vote)
                    .build();
            VoteItem saved = voteItemRepository.save(voteItem);
            return voteUserService.saveVoteUser(saved);
        }
        return voteUserService.saveVoteUser(voteItem);
    }
}
