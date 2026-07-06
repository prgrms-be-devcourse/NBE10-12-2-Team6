package csh.back.domain.trip.post.controller;

import csh.back.domain.trip.post.dto.request.CreatePostRequest;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.service.PostService;
import csh.back.global.annotation.ApiV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import csh.back.domain.trip.post.dto.response.TimelinePostsResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@ApiV1
@RequestMapping("/trips/{tripId}/posts")
@Tag(name = "Post", description = "게시글 API")
public class PostV1Controller {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 생성", description = "사진이 포함된 게시글을 생성합니다.")
    public PostResponse create(

            @PathVariable Long tripId,

            @RequestParam Long timelineId,

            @ModelAttribute CreatePostRequest request,

            @RequestParam(value = "image", required = false)
            MultipartFile image
    ) {
        return postService.create(tripId, timelineId, request, image);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 조회", description = "게시글 단건 조회")
    public PostResponse getPost(
            @PathVariable Long tripId,
            @PathVariable Long postId
    ) {
        return postService.getPost(tripId, postId);
    }

    @GetMapping
    @Operation(summary = "게시글 전체 조회", description = "타임라인별 전체 게시글을 조회합니다.")
    public List<TimelinePostsResponse> getPosts(
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