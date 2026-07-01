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
@RequestMapping("/posts")
@Tag(name = "Post", description = "게시글 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    public PostResponse create(
            @Parameter(description = "여행 멤버 ID", example = "1")
            @RequestParam Long tripMemberId,

            @Parameter(description = "타임라인 ID", example = "1")
            @RequestParam Long timelineId,

            @RequestBody CreatePostRequest request
    ) {
        return postService.create(
                tripMemberId,
                timelineId,
                request
        );
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글 단건 조회")
    public PostResponse getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId
    ) {
        return postService.getPost(postId);
    }

    @GetMapping
    @Operation(summary = "게시글 전체 조회", description = "모든 게시글을 조회합니다.")
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 내용을 수정합니다.")
    public void update(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,

            @RequestBody UpdatePostRequest request
    ) {
        postService.update(postId, request);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public void delete(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId
    ) {
        postService.delete(postId);
    }
}