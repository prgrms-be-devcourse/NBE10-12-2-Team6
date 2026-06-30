package csh.back.domain.vote.vote.dto.response;

public record VoteFindResponse(
        String place,
        Long count) {
    public static VoteFindResponse from(String place, Long count) {
        return new VoteFindResponse(place, count);
    }
}
