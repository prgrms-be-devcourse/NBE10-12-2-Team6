package csh.back.domain.trip.post.controller;

import csh.back.domain.member.dto.response.AuthFilterDto;
import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.repository.TripGroupRepository;
import csh.back.domain.trip.group.support.WithMockLoginUser;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.post.dto.request.UpdatePostRequest;
import csh.back.domain.trip.post.entity.Post;
import csh.back.domain.trip.post.repository.PostRepository;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PostV1ControllerTest {
    //의존성 부여
    @Autowired private MockMvc mvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private TripGroupRepository tripGroupRepository;
    @Autowired private TripMemberRepository tripMemberRepository;
    @Autowired private TimeLineRepository timeLineRepository;
    @Autowired private PostRepository postRepository;


    private static final String BASE_URL = "/api/v1";
    //테스트용 더미 이미지 제거 함수
    @AfterEach
    void cleanUp() {
        File dir = new File("./uploadedimages");
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    @WithMockLoginUser
    void t1() throws Exception {

        // 테스트용 사용자
        Member member = memberRepository.save(
                Member.builder()
                        .email("test@test.com")
                        .password("1234")
                        .name("테스터")
                        .build()
        );
        //테스트용 여행 그룹
        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(member)
                        .name("부산 여행")
                        .region("부산")
                        .joinCode("TEST1")
                        .nights(2)
                        .startDate(LocalDate.of(2026, 7, 1))
                        .endDate(LocalDate.of(2026, 7, 3))
                        .build()
        );
        //테스트용 여행 맴버
        TripMember tripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(member)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );
        //테스트용 타임라인
        TimeLine timeLine = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026, 7, 1, 9, 0))
                        .endTime(LocalDateTime.of(2026, 7, 1, 18, 0))
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .author(tripMember)
                        .timeLine(timeLine)
                        .content("게시글 내용")
                        .location("광안리")
                        .isImg(false)
                        .contentUrl(null)
                        .build()
        );

        // when
        ResultActions resultActions = mvc.perform(
                        get(BASE_URL + "/trips/{tripId}/posts/{postId}",
                                tripGroup.getId(),
                                post.getId())
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("getPost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.timelineId").value(timeLine.getId()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.location").value(post.getLocation()))
                .andExpect(jsonPath("$.isImg").value(post.getIsImg()))
                .andExpect(jsonPath("$.contentUrl").isEmpty());
    }
    @Test
    @DisplayName("게시글 전체 조회")
    @WithMockLoginUser
    void t2() throws Exception {

        // given
        Member member = memberRepository.save(
                Member.builder()
                        .email("test@test.com")
                        .password("1234")
                        .name("테스터")
                        .build()
        );

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(member)
                        .name("부산여행")
                        .region("부산")
                        .joinCode("TEST2")
                        .nights(2)
                        .startDate(LocalDate.of(2026, 7, 1))
                        .endDate(LocalDate.of(2026, 7, 3))
                        .build()
        );

        TripMember tripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(member)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026, 7, 1, 9, 0))
                        .endTime(LocalDateTime.of(2026, 7, 1, 18, 0))
                        .build()
        );

        Post post1 = postRepository.save(
                Post.builder()
                        .author(tripMember)
                        .timeLine(timeline)
                        .content("첫 번째 게시글")
                        .location("광안리")
                        .isImg(false)
                        .build()
        );

        Post post2 = postRepository.save(
                Post.builder()
                        .author(tripMember)
                        .timeLine(timeline)
                        .content("두 번째 게시글")
                        .location("해운대")
                        .isImg(false)
                        .build()
        );

        // when
        ResultActions resultActions = mvc.perform(
                        get(BASE_URL + "/trips/{tripId}/posts", tripGroup.getId())
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("getPosts"))
                .andExpect(status().isOk())

                // 타임라인 정보
                .andExpect(jsonPath("$[0].timelineId").value(timeline.getId()))
                .andExpect(jsonPath("$[0].dayNumber").value(1))

                // 첫 번째 게시글
                .andExpect(jsonPath("$[0].posts[0].id").value(post1.getId()))
                .andExpect(jsonPath("$[0].posts[0].content").value("첫 번째 게시글"))
                .andExpect(jsonPath("$[0].posts[0].location").value("광안리"))

                // 두 번째 게시글
                //여기서부턴 비슷한 케이스 계속 추가의 반복.
                .andExpect(jsonPath("$[0].posts[1].id").value(post2.getId()))
                .andExpect(jsonPath("$[0].posts[1].content").value("두 번째 게시글"))
                .andExpect(jsonPath("$[0].posts[1].location").value("해운대"));
    }
    @Test
    @DisplayName("게시글 생성")
    void t3() throws Exception {

        // given
        Member member = memberRepository.save(
                Member.builder()
                        .email("test@test.com")
                        .password("1234")
                        .name("POST create 테스터")
                        .build()
        );
        AuthFilterDto loginUser =
                new AuthFilterDto(member.getId(), member.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(member)
                        .name("부산여행")
                        .region("부산")
                        .joinCode("TEST3")
                        .nights(2)
                        .startDate(LocalDate.of(2026, 7, 1))
                        .endDate(LocalDate.of(2026, 7, 3))
                        .build()
        );

        tripMemberRepository.save(
                TripMember.builder()
                        .member(member)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026, 7, 1, 9, 0))
                        .endTime(LocalDateTime.of(2026, 7, 1, 18, 0))
                        .build()
        );
        //더미 이미지 생성
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "dummy-image".getBytes()
        );

        // when
        ResultActions resultActions = mvc.perform(
                multipart(BASE_URL + "/trips/{tripId}/posts", tripGroup.getId())
                        .file(image)
                        .param("timelineId", timeline.getId().toString())
                        .param("content", "게시글 내용")
                        .param("location", "광안리")
        ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timelineId").value(timeline.getId()))
                .andExpect(jsonPath("$.content").value("게시글 내용"))
                .andExpect(jsonPath("$.location").value("광안리"))
                .andExpect(jsonPath("$.isImg").value(true))
                .andExpect(jsonPath("$.contentUrl").isNotEmpty());
    }
    @Test
    @DisplayName("게시글 수정(이미지 포함)")
    void t4() throws Exception {

        // given
        Member member = memberRepository.save(
                Member.builder()
                        .email("update@test.com")
                        .password("1234")
                        .name("수정테스터")
                        .build()
        );

        AuthFilterDto loginUser =
                new AuthFilterDto(member.getId(), member.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(member)
                        .name("수정여행")
                        .region("부산")
                        .joinCode("UPDATE1")
                        .nights(2)
                        .startDate(LocalDate.of(2026,7,1))
                        .endDate(LocalDate.of(2026,7,3))
                        .build()
        );

        TripMember tripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(member)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026,7,1,9,0))
                        .endTime(LocalDateTime.of(2026,7,1,18,0))
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .author(tripMember)
                        .timeLine(timeline)
                        .content("기존 내용")
                        .location("광안리")
                        .isImg(true)
                        .contentUrl("/uploadedimages/old.jpg")
                        .build()
        );

        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "new.jpg",
                        "image/jpeg",
                        "new-image".getBytes()
                );

        // when
        ResultActions resultActions =
                mvc.perform(    //바꾼 내용
                                multipart(BASE_URL + "/trips/{tripId}/posts/{postId}",
                                        tripGroup.getId(),
                                        post.getId())
                                        .file(image)
                                        .param("content","수정된 내용")
                                        .param("location","해운대")
                                        .with(request -> {
                                            request.setMethod("PUT");
                                            return request;
                                        })
                        )
                        .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isOk());

        Post updated =
                postRepository.findById(post.getId()).orElseThrow();

        assertThat(updated.getContent())
                .isEqualTo("수정된 내용");

        assertThat(updated.getLocation())
                .isEqualTo("해운대");

        assertThat(updated.getIsImg())
                .isTrue();

        assertThat(updated.getContentUrl())
                //사진은 변경 내용이 없다면 그대로 쓰고, 만약 새 사진이 있다면 old라는 파일로 빼면서 삭제 대상에 넣기
                .isNotEqualTo("/uploadedimages/old.jpg");
    }
    @Test
    @DisplayName("게시글 삭제 시 백엔드에 저장된 이미지도 함께 삭제")
    void t5() throws Exception {

        // given
        Member member = memberRepository.save(
                Member.builder()
                        .email("delete@test.com")
                        .password("1234")
                        .name("삭제테스트")
                        .build()
        );

        AuthFilterDto loginUser =
                new AuthFilterDto(member.getId(), member.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(member)
                        .name("삭제여행")
                        .region("부산")
                        .joinCode("DELETE")
                        .nights(2)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(2))
                        .build()
        );
        //어드민(작성자) 권한 확인
        TripMember tripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(member)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusHours(1))
                        .build()
        );
        // 사진 파일 체크(더미)
        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "delete.jpg",
                        "image/jpeg",
                        "delete-image".getBytes()
                );

        ResultActions createResult =
                mvc.perform(
                        multipart(BASE_URL + "/trips/{tripId}/posts", tripGroup.getId())
                                .file(image)
                                .param("timelineId", timeline.getId().toString())
                                .param("content", "삭제될 게시글")
                                .param("location", "광안리")
                );

        String contentUrl =
                JsonPath.read(
                        createResult.andReturn()
                                .getResponse()
                                .getContentAsString(),
                        "$.contentUrl"
                );

        Long postId = ((Number) JsonPath.read(
                createResult.andReturn()
                        .getResponse()
                        .getContentAsString(),
                "$.id"
        )).longValue();

        File savedImage =
                new File("." + contentUrl);

        assertThat(savedImage.exists()).isTrue();

        // when
        ResultActions resultActions =
                mvc.perform(
                        delete(BASE_URL + "/trips/{tripId}/posts/{postId}",
                                tripGroup.getId(),
                                postId)
                ).andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk());

        assertThat(postRepository.findById(postId)).isEmpty();

        assertThat(savedImage.exists()).isFalse();
    }
    @Test
    @DisplayName("작성자가 아닌 사용자는 게시글를 수정할 수 없어야 함")
    void t6() throws Exception {

        // given
        Member writer = memberRepository.save(
                Member.builder()
                        .email("writer@test.com")
                        .password("1234")
                        .name("작성자")
                        .build()
        );

        Member other = memberRepository.save(
                Member.builder()
                        .email("other@test.com")
                        .password("1234")
                        .name("다른사용자")
                        .build()
        );

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(writer)
                        .name("부산여행")
                        .region("부산")
                        .joinCode("TEST6")
                        .nights(2)
                        .startDate(LocalDate.of(2026, 7, 1))
                        .endDate(LocalDate.of(2026, 7, 3))
                        .build()
        );

        TripMember writerTripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(writer)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        tripMemberRepository.save(
                TripMember.builder()
                        .member(other)
                        .tripGroup(tripGroup)
                        .isAdmin(false)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026, 7, 1, 9, 0))
                        .endTime(LocalDateTime.of(2026, 7, 1, 18, 0))
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .author(writerTripMember)
                        .timeLine(timeline)
                        .content("원본")
                        .location("광안리")
                        .isImg(false)
                        .build()
        );

        // 작성자가 아닌 사용자로 로그인
        AuthFilterDto loginUser =
                new AuthFilterDto(other.getId(), other.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockMultipartFile image =
                new MockMultipartFile(
                        "image",
                        "test.jpg",
                        "image/jpeg",
                        "dummy-image".getBytes()
                );

        // 수정 시도
        ResultActions resultActions =
                mvc.perform(
                                multipart(BASE_URL + "/trips/{tripId}/posts/{postId}",
                                        tripGroup.getId(), //ID 비교
                                        post.getId())
                                        .file(image)
                                        .param("content", "수정내용")
                                        .param("location", "해운대")
                                        .with(req -> {
                                            req.setMethod("PUT");
                                            return req;
                                        })
                        )
                        .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message")
                        .value("작성자만 수정 및 삭제할 수 있습니다."));
    }
    @Test
    @DisplayName("작성자가 아닌 사용자는 게시글를 삭제할 수 없어야 함")
    void t7() throws Exception {

        // given
        Member writer = memberRepository.save(
                Member.builder()
                        .email("writer@test.com")
                        .password("1234")
                        .name("작성자")
                        .build()
        );

        Member other = memberRepository.save(
                Member.builder()
                        .email("other@test.com")
                        .password("1234")
                        .name("다른사용자")
                        .build()
        );

        TripGroup tripGroup = tripGroupRepository.save(
                TripGroup.builder()
                        .owner(writer)
                        .name("부산여행")
                        .region("부산")
                        .joinCode("TEST7")
                        .nights(2)
                        .startDate(LocalDate.of(2026, 7, 1))
                        .endDate(LocalDate.of(2026, 7, 3))
                        .build()
        );

        TripMember writerTripMember = tripMemberRepository.save(
                TripMember.builder()
                        .member(writer)
                        .tripGroup(tripGroup)
                        .isAdmin(true)
                        .build()
        );

        tripMemberRepository.save(
                TripMember.builder()
                        .member(other)
                        .tripGroup(tripGroup)
                        .isAdmin(false)
                        .build()
        );

        TimeLine timeline = timeLineRepository.save(
                TimeLine.builder()
                        .tripGroup(tripGroup)
                        .dayNumber(1)
                        .startTime(LocalDateTime.of(2026, 7, 1, 9, 0))
                        .endTime(LocalDateTime.of(2026, 7, 1, 18, 0))
                        .build()
        );

        Post post = postRepository.save(
                Post.builder()
                        .author(writerTripMember)
                        .timeLine(timeline)
                        .content("원본")
                        .location("광안리")
                        .isImg(false)
                        .build()
        );

        // 작성자가 아닌 사용자로 로그인
        AuthFilterDto loginUser =
                new AuthFilterDto(other.getId(), other.getEmail());

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        ResultActions resultActions =
                mvc.perform(
                                delete(BASE_URL + "/trips/{tripId}/posts/{postId}",
                                        tripGroup.getId(),
                                        post.getId())
                        )
                        .andDo(print());

        // then
        resultActions
                .andExpect(handler().handlerType(PostV1Controller.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message")
                        .value("작성자만 수정 및 삭제할 수 있습니다."));
    }

}