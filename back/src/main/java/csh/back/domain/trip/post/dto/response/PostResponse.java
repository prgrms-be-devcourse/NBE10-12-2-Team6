package csh.back.domain.trip.post.dto.response;

import csh.back.domain.trip.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {

    private Long id;
    private String content;
    private String location;
    private Boolean isImg;
    private String contentUrl;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent()) //아직 값 전달 못받음
                .location(post.getLocation()) //아직 값 전달 못받음
                .isImg(post.getIsImg())
                .contentUrl(post.getContentUrl())
                .build();
    }
}