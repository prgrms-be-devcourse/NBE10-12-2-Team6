package csh.back.domain.trip.post.controller;

import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts") //기초 URL 설정
public class PostController {
    private final PostService postService;

    @GetMapping("/{postId}")//단건조회
    public PostResponse getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping//게시글 전체조회
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }
    @PutMapping("/{postId}") //게시글 수정
    public void update(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request
    ) {
        postService.update(postId, request);
    }
    @DeleteMapping("/{postId}") //게시글 삭제
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}
