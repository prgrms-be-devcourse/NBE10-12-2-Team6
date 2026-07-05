package csh.back.domain.vote.vote.dto.response;

import csh.back.domain.vote.vote.enums.VoteConfirmStatus;

import java.util.List;

public record VoteConfirmResponse(
        VoteConfirmStatus status,
        Long confirmedPlaceId,
        List<Long> tiedPlaceIds
        )
{
    public static VoteConfirmResponse of(VoteConfirmStatus status, Long confirmedPlaceId, List<Long> tiedPlaceIds) {
       return new VoteConfirmResponse(
               status,
               confirmedPlaceId,
               tiedPlaceIds
       );
    }
}
