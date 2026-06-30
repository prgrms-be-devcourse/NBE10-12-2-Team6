package csh.back.domain.trip.post.dto.request;

public record UpdatePostRequest(
        String content,
        String location
) {
}