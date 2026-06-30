package csh.back.domain.trip.post.service;

import csh.back.domain.trip.post.dto.request.CreatePostRequest;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.entity.Post;
import csh.back.domain.trip.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    //create 기능은 일단 보류
    //private final TripMemberRepository tripMemberRepository;
    //private final TimeLineRepository timeLineRepository;


    //게시글 조회
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
    //지금 당장 조회하면 넣어놓은 기초값이나 테스팅 데이터가 없어서 게시물이 없는 상태만 나옴
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        return PostResponse.from(post);
    }
    //게시글 전체조회
    @Transactional(readOnly = true)
    public List<PostResponse> getPosts() {

        return postRepository.findAll()
                .stream()
                .map(PostResponse::from)
                .toList();
    }
    //게시글 수정 (임시)
    @Transactional
    public void update(Long postId, UpdatePostRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        post.update(
                request.content(),
                request.location()
        );
    }
    //게시글 삭제(임시)
    @Transactional
    public void delete(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        postRepository.delete(post);
    }

}