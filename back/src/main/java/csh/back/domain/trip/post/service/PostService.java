package csh.back.domain.trip.post.service;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.post.dto.request.CreatePostRequest;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.entity.Post;
import csh.back.domain.trip.post.repository.PostRepository;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TimeLineRepository timeLineRepository;


    @Transactional(readOnly = true)
    public PostResponse getPost(Long tripId, Long postId) {
    //기초 출력
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        //tripID를 통한 여행 식별
        if (!post.getTimeLine().getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 게시글이 아닙니다.");
        }

        return PostResponse.from(post);
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getPosts(Long tripId) {

        return postRepository.findByTimeLineTripGroupId(tripId)
                .stream()
                .map(PostResponse::from)
                .toList();
    }
    @Transactional
    public void update(Long tripId, Long postId, UpdatePostRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getTimeLine().getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 게시글이 아닙니다.");
        }
        post.update(
                request.content(),
                request.location()
        );
    }
    @Transactional
    public void delete(Long tripId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getTimeLine().getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 게시글이 아닙니다.");
        }

        postRepository.delete(post);
    }
    @Transactional
    public PostResponse create(
            Long tripId,
            Long tripMember,
            Long timelineId,
            CreatePostRequest request
    ) {
        //타임라인 체크 메세지
        TripMember tripMember = tripMemberRepository.findById(tripMember)
                .orElseThrow(() -> new IllegalArgumentException("여행 멤버가 존재하지 않습니다."));

        TimeLine timeline = timeLineRepository.findById(timelineId)
                .orElseThrow(() -> new IllegalArgumentException("타임라인이 존재하지 않습니다."));
        //타임라인 인덱스 체크
        if (!timeline.getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 타임라인이 아닙니다.");
        }
        Post post = Post.builder()
                .tripMember(tripMember)
                .timeLine(timeline)
                .content(request.content())
                .location(request.location())
                .isImg(request.isImg())
                .contentUrl(null)
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }
}
