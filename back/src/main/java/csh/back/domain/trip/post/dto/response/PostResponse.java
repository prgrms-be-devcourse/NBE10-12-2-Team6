package csh.back.domain.trip.post.dto.response;

import csh.back.domain.trip.post.entity.Post;

public record PostResponse(
        Long id,
        String content,
        String location,
        Boolean isImg,
        String contentUrl
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getLocation(),
                post.getIsImg(),
                post.getContentUrl()
        );
    }
}