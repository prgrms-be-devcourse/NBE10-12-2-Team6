package csh.back.domain.trip.post.controller;

import csh.back.domain.trip.post.dto.request.CreatePostRequest;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trips/{tripId}/posts") //기존 /post가 아닌 여행 API 명세서에 따른 하위 URL로 수정
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    @PostMapping("/{tripMemberId}/{timelineId}") //여행 맴버 및 여행 ID 조회를 통한 검증
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    public PostResponse create(

            @Parameter(description = "여행 ID", example = "1")
            @PathVariable Long tripId,

            @Parameter(description = "여행 멤버 ID", example = "1")
            @PathVariable Long tripMemberId,

            @Parameter(description = "타임라인 ID", example = "1")
            @PathVariable Long timelineId,

            @RequestBody CreatePostRequest request
    ) {
        return postService.create(
                tripId, // trip의 하위 개념으로 옮겼으므로 tripID 역시 참조해야함
                tripMemberId, // trip의 하위 개념으로 옮겼으므로 tripMemberId 역시 참조해야함
                timelineId,
                request
        );
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글 단건 조회")
    public PostResponse getPost(
            @PathVariable Long tripId, // trip의 하위 개념으로 옮겼으므로 tripID를 참조
            @PathVariable Long postId
    ) {
        return postService.getPost(tripId, postId);
    }

    @GetMapping
    @Operation(summary = "게시글 전체 조회", description = "모든 게시글을 조회합니다.")
    public List<PostResponse> getPosts(
            @PathVariable Long tripId
    ) {
        return postService.getPosts(tripId);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 내용을 수정합니다.")
    public void update(
            @PathVariable Long tripId,
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        postService.update(tripId, postId, request);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public void delete(
            @PathVariable Long tripId,
            @PathVariable Long postId
    ) {
        postService.delete(tripId, postId);
    }
}