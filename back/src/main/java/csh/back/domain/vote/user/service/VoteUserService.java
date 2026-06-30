package csh.back.domain.vote.user.service;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.item.repository.VoteItemRepository;
import csh.back.domain.vote.user.dto.response.VoteUserSaveResponseDto;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.user.repository.VoteUserRepository;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.domain.vote.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VoteUserService {

    private final VoteRepository voteRepository;
    private final VoteUserRepository voteUserRepository;
    private final VoteItemRepository voteItemRepository;
    private final TripMemberRepository tripMemberRepository;

    @Transactional
    public VoteUserSaveResponseDto saveVoteUser(Vote vote, VoteItem voteItem) {
        TripMember tripMember = tripMemberRepository.findById(1L).orElseThrow(RuntimeException::new);

        VoteUser voteUser = VoteUser.builder()
                .vote(vote)
                .VoteItem(voteItem)
                .TripMember(tripMember)
                .build();
        VoteUser saved = voteUserRepository.save(voteUser);
        return VoteUserSaveResponseDto.from(saved);
    }
}
