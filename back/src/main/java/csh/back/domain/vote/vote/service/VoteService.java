package csh.back.domain.vote.vote.service;

import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import csh.back.domain.vote.vote.dto.response.VoteFindResponse;
import csh.back.domain.vote.vote.dto.response.VoteFindUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {
    private final VoteUserRepository voteUserRepository;
    private final VoteItemRepository voteItemRepository;

    public List<VoteFindResponse> findVoteItemAndCount(Long voteId) {
        //투표된 장소 목록을 조회해 옴
        List<VoteItem> voteItemList = voteItemRepository.findByVoteIdWithTripPlace(voteId);

        //각 장소에 몇포가 투표 되었는지 카운팅
        Map<Long, Long> countMap = voteUserRepository.countGroupByVoteId(voteId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        //장소의 아이디를 키로 하여 위의 맵에서 횟수를 매핑하여 반환
        return voteItemList.stream()
                .map(vi -> VoteFindResponse.from(
                        vi.getTripPlace().getId(),
                        vi.getTripPlace().getName(),
                        countMap.getOrDefault(vi.getId(), 0L)
                ))
                .toList();
    }

    public List<VoteFindUserResponse> findUserVoteThisPlace(Long voteId, Long placeId) {
        VoteItem voteItem = voteItemRepository.findByVoteIdAndTripPlaceId(voteId, placeId).orElseThrow(RuntimeException::new);
        List<VoteUser> voteUsers = voteUserRepository.findByVoteItemId(voteItem.getId());
        List<VoteFindUserResponse> responses = voteUsers.stream().map(VoteFindUserResponse::from).toList();
        return responses;
    }
}
