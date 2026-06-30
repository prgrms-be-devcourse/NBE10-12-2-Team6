package csh.back.domain.trip.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePostRequest {
    //업데이트니까 내용이랑 위치만 사진은 차후 추가
    private String content;

    private String location;
}