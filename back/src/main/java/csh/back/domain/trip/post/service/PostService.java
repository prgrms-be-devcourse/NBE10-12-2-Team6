package csh.back.domain.trip.post.service;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.service.TripGroupService;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.member.validator.TripMemberValidator;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.dto.response.PostResponse;
import csh.back.domain.trip.post.dto.response.PostsDailyResponse;
import csh.back.domain.trip.post.entity.Post;
import csh.back.domain.trip.post.repository.PostRepository;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TimeLineRepository timeLineRepository;
    private final PostImageService postImageService;
    private final TripMemberValidator tripMemberValidator;
    private final TripGroupService tripGroupService;

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
    public List<PostsDailyResponse> getPosts(Long tripId, Long memberId) {
        tripMemberValidator.validMember(tripId, memberId);

        TripGroup tripGroup = tripGroupService.findTripGroupById(tripId);

        List<TripMember> tripMembers = tripMemberRepository.findByTripGroupId(tripGroup.getId());

        List<Post> posts = postRepository.findWithTimeLineAndPlaceByAuthorIdIn(tripMembers);

        Map<LocalDate, List<Post>> map = posts.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getCreatedAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        return map.entrySet().stream()
                .map(entry -> new PostsDailyResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(PostsDailyResponse.PostSummary::from)
                                .toList()
                ))
                .toList();
    }
    // 게시글 수정
    @Transactional
    public void update(
            Long tripId,
            Long postId,
            UpdatePostRequest request,
            MultipartFile image
    ) {

        Post post = findAuthorizedPost(tripId, postId);

        // 기존 상태
        Boolean isImg = post.getIsImg();
        String imageUrl = post.getContentUrl();

        // 이미지가 새로 들어온 경우
        if (image != null && !image.isEmpty()) {

            // 기존 이미지 삭제 (있을 때만)
            if (Boolean.TRUE.equals(post.getIsImg())
                    && post.getContentUrl() != null) {

                postImageService.deleteImage(post.getContentUrl());
            }

            // 새 이미지 저장
            imageUrl = postImageService.saveImage(image);
            isImg = true;

        } else {
            // 이미지 변경 없으면 그대로 유지
            isImg = post.getIsImg();
            imageUrl = post.getContentUrl();
        }

        // 최종 반영
        post.update(
                request.content(),
                request.location(),
                isImg,
                imageUrl
        );
    }
    // 게시글 삭제
    @Transactional
    public void delete(Long tripId, Long postId) {

        Post post = findAuthorizedPost(tripId, postId);

        // 삭제된 포스트에 이미지가 있으면 포스트 삭제할때 서버에 저장된 이미지 파일도 같이 삭제
        if (Boolean.TRUE.equals(post.getIsImg())
                && post.getContentUrl() != null) {

            postImageService.deleteImage(post.getContentUrl());
        }

        // DB 삭제
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
        tripMemberValidator.validMember(tripId, memberId);

        // 여행 멤버 조회
        TripMember author = tripMemberRepository
                .findByMemberIdAndTripGroupId(memberId, tripId)
                .orElseThrow(() -> new IllegalArgumentException("여행 멤버가 존재하지 않습니다."));
        // 타임라인 조회
        TimeLine timeLine = null;
        if(timeLineId != null) timeLine = timeLineRepository.findById(timeLineId).orElse(null);


        // 이미지 저장
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = postImageService.saveImage(image);
        }

        // 게시글 생성
        Post post = Post.builder()
                .author(author)
                .timeLine(timeLine)
                .isImg(image != null && !image.isEmpty())
                .contentUrl(imageUrl)
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }
    private Long getCurrentMemberId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthFilterDto loginUser =
                (AuthFilterDto) authentication.getPrincipal();

        return loginUser.id();
    }
    private void validateAuthor(Post post) {

        Long memberId = getCurrentMemberId();

        if (!post.getAuthor().getMember().getId().equals(memberId)) {
            throw new NonMemberException("작성자만 수정 및 삭제할 수 있습니다.");
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