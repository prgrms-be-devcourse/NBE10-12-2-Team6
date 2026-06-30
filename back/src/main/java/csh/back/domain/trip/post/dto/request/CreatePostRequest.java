package csh.back.domain.trip.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostRequest {
    //글내용, 위치, 사진인지
    private String content;
    private String location;
    private Boolean isImg;
}