package csh.back.domain.vote.vote.dto.response;

import csh.back.domain.trip.place.dto.response.TripPlaceFindResponse;

import java.util.List;

public record VoteFindWithUpdateCountResponse(
        List<VoteFindResponse> voteResults,
        List<TripPlaceFindResponse> wishPlaceFindResponses,
        Integer updateCount,
        boolean isConfirmed
) {
    public static VoteFindWithUpdateCountResponse of(
            List<VoteFindResponse> voteFindResponses,
            List<TripPlaceFindResponse> wishPlaceFindResponses,
            int updateCount,
            boolean isConfirmed
    ) {
        return new VoteFindWithUpdateCountResponse(voteFindResponses, wishPlaceFindResponses, updateCount, isConfirmed);
    }
}
