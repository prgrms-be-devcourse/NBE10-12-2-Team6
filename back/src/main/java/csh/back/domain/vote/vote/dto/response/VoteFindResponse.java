package csh.back.domain.vote.vote.dto.response;

public record VoteFindResponse(
        Long placeId,
        String place,
        Long count) {
    public static VoteFindResponse from(Long placeId, String place, Long count) {
        return new VoteFindResponse(placeId, place, count);
    }
}
