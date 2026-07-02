package csh.back.domain.vote.user.service;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.user.dto.response.VoteUserSaveResponseDto;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VoteUserService {

    private final VoteUserRepository voteUserRepository;
    private final TripMemberRepository tripMemberRepository;

    private final Integer DEFAULT_UPDATE_COUNT = 0;

    @Transactional
    public VoteUserSaveResponseDto saveVoteUser(VoteItem voteItem) {
        // 테스트 용도 추후 삭제 예정
        Random rand = new Random();
        long testId = rand.nextLong(1,1000);
        // 여행참여자를 찾고
        TripMember tripMember = tripMemberRepository.findById(testId).orElseThrow(RuntimeException::new);
        VoteUser voteUser = voteUserRepository.findByVoteIdAndTripMemberId(voteItem.getVote().getId(), testId)
                .orElse(null);
        if(voteUser == null) {
            voteUser = VoteUser.builder()
                    .vote(voteItem.getVote())
                    .voteItem(voteItem)
                    .tripMember(tripMember)
                    .updateCount(DEFAULT_UPDATE_COUNT)
                    .build();
            VoteUser saved = voteUserRepository.save(voteUser);
            return VoteUserSaveResponseDto.from(saved);
        }
        voteUser.updatePlaceAndCount(voteUser, voteItem)



    }
}
