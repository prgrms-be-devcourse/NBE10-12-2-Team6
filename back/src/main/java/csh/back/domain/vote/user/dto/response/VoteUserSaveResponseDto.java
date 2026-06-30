package csh.back.domain.vote.user.dto.response;

import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.user.entity.VoteUser;
import csh.back.domain.vote.vote.entity.Vote;

public record VoteUserSaveResponseDto(String memberName,
                                      String place) {
    public static VoteUserSaveResponseDto from(VoteUser voteUser) {
        return new VoteUserSaveResponseDto(
                voteUser.getTripMember().getOwner().getName(),
                voteUser.getVoteItem().getTripPlace().getName()
        );
    }
}
