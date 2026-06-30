package csh.back.domain.trip.post.dto.request;

public record CreatePostRequest(
        String content,
        String location,
        Boolean isImg
) {
}