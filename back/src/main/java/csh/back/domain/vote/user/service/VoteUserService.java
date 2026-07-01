package csh.back.domain.vote.user.service;

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
//    private final TripMemberRepository tripMemberRepository;

    @Transactional
    public VoteUserSaveResponseDto saveVoteUser(VoteItem voteItem) {
        // 테스트 용도 추후 삭제 예정
        Random rand = new Random();
        long testId = rand.nextLong(1,1000);
        //
//        TripMember tripMember = tripMemberRepository.findById(testId).orElseThrow(RuntimeException::new);

        VoteUser voteUser = VoteUser.builder()
                .vote(voteItem.getVote())
                .voteItem(voteItem)
//                .TripMember(tripMember)
                .build();
        VoteUser saved = voteUserRepository.save(voteUser);
        return VoteUserSaveResponseDto.from(saved);
    }
}
