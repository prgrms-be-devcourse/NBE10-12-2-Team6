package csh.back.domain.trip.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePostRequest(

        @Schema(description = "게시글 내용", example = "부산 여행 시작!")
        String content,

        @Schema(description = "위치", example = "부산 광안리")
        String location,

        @Schema(description = "이미지 여부", example = "true")
        Boolean isImg
) {
}