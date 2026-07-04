package csh.back.domain.vote.vote.dto.response;

import java.util.List;

public record VoteFindWithUpdateCountResponse(
        List<VoteFindResponse> voteResults,
        Integer updateCount
) {
    public static VoteFindWithUpdateCountResponse of(
            List<VoteFindResponse> voteFindResponses,
            int updateCount
    ) {
        return new VoteFindWithUpdateCountResponse(voteFindResponses, updateCount);
    }
}
