package csh.back.domain.trip.post.service;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.post.dto.request.CreatePostRequest;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.dto.response.TimelinePostsResponse;
import csh.back.domain.trip.post.entity.Post;
import csh.back.domain.trip.post.repository.PostRepository;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TimeLineRepository timeLineRepository;
    private final PostImageService postImageService;

    @Transactional(readOnly = true)
    public PostResponse getPost(Long tripId, Long postId) {
        //값 검사
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        //여행별로 포스트를 구분하고 검증하는 IF문 추가
        if (!post.getTimeLine().getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 게시글이 아닙니다.");
        }

        return PostResponse.from(post);
    }
    //게시글 전체조회
    @Transactional(readOnly = true)
    public List<TimelinePostsResponse> getPosts(Long tripId, Long memberId) {

        List<Post> posts = postRepository.findByTimeLineTripGroupId(tripId);

        return posts.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getTimeLine().getId()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Post> timelinePosts = entry.getValue();
                    TimeLine timeline = timelinePosts.get(0).getTimeLine();
                    return new TimelinePostsResponse(
                            timeline.getId(),
                            timeline.getDayNumber(),
                            timelinePosts.stream()
                                    .map(PostResponse::from)
                                    .toList()
                    );

                })

                .sorted(Comparator.comparing(TimelinePostsResponse::dayNumber))

                .toList();
    }
    // 게시글 수정
    @Transactional
    public void update(Long tripId, Long postId, UpdatePostRequest request) {

        Post post = findAuthorizedPost(tripId, postId);

        post.update(
                request.content(),
                request.location()
        );
    }
    // 게시글 삭제
    @Transactional
    public void delete(Long tripId, Long postId) {

        Post post = findAuthorizedPost(tripId, postId);

        postRepository.delete(post);
    }
    // 게시글 생성
    @Transactional
    public PostResponse create(
            Long tripId,
            Long memberId,
            Long timeLineId,
            MultipartFile image
    ) {

        // 여행 멤버 조회
        TripMember author = tripMemberRepository
                .findByMemberIdAndTripGroupId(memberId, tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행 멤버가 존재하지 않습니다."));
        // 타임라인 조회
        TimeLine timeline = timeLineRepository.findById(timeLineId)
                .orElseThrow(() -> new IllegalArgumentException("타임라인이 존재하지 않습니다."));

        // 해당 여행의 타임라인인지 확인
        if (!timeline.getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 타임라인이 아닙니다.");
        }

        // 이미지 저장
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = postImageService.saveImage(image);
        }

        // 게시글 생성
        Post post = Post.builder()
                .author(author)
                .timeLine(timeline)
                .isImg(image != null && !image.isEmpty())
                .contentUrl(imageUrl)
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }
    private Long getCurrentMemberId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (Long) authentication.getDetails();
    }
    private void validateAuthor(Post post) {

        Long memberId = getCurrentMemberId();

        if (!post.getAuthor().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("작성자만 수정 및 삭제할 수 있습니다.");
        }
    }
    private Post findAuthorizedPost(Long tripId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getTimeLine().getTripGroup().getId().equals(tripId)) {
            throw new IllegalArgumentException("해당 여행의 게시글이 아닙니다.");
        }

        validateAuthor(post);

        return post;
    }
}