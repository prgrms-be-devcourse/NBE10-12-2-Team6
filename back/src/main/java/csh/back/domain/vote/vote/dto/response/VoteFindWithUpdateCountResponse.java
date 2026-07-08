package csh.back.domain.vote.vote.dto.response;

import csh.back.domain.trip.place.dto.response.TripPlaceFindResponse;
import csh.back.domain.vote.vote.enums.VoteStatus;

import java.util.List;

public record VoteFindWithUpdateCountResponse(
        List<VoteFindResponse> voteResults,
        List<TripPlaceFindResponse> wishPlaceFindResponses,
        Integer updateCount,
        String voteStatus
) {
    public static VoteFindWithUpdateCountResponse of(
            List<VoteFindResponse> voteFindResponses,
            List<TripPlaceFindResponse> wishPlaceFindResponses,
            int updateCount,
            VoteStatus voteStatus
    ) {
        return new VoteFindWithUpdateCountResponse(voteFindResponses, wishPlaceFindResponses, updateCount, voteStatus.getNickname());
    }
}
