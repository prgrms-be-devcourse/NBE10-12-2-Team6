package csh.back.global.init;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.group.repository.GroupRepository;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.member.repository.TripMemberRepository;
import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.trip.place.repository.PlaceRepository;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.trip.timeline.repository.TimeLineRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitData {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final TripMemberRepository tripMemberRepository;
    private final PlaceRepository placeRepository;
    private final TimeLineRepository timeLineRepository;

    @PostConstruct
    @Transactional
    public void init() {
        //1. 테스트용 계정 생성
        Member member = Member.builder()
                .email("test@test.com")
                .password("1234")
                .name("테스터")
                .build();

        memberRepository.save(member);
        //2. 테스트용 여행지 데이터 생성
        TripGroup group = TripGroup.builder()
                .owner(member)
                .name("부산 여행")
                .region("부산")
                .nights(2)
                .joinUrl("join-url")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .build();

        groupRepository.save(group);
        //테스트용 관리자 생성
        TripMember tripMember = TripMember.builder()
                .owner(member)
                .tripGroup(group)
                .isAdmin(true)
                .build();

        tripMemberRepository.save(tripMember);

        TripPlace place = TripPlace.builder()
                .tripGroup(group)
                .name("광안리")
                .theme("바다")
                .address("부산")
                .kakaoPlaceId("123")
                .kakaoMapUrl("url")
                .build();

        placeRepository.save(place);

        TimeLine timeline = TimeLine.builder()
                .tripGroup(group)
                .tripPlace(place)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        timeLineRepository.save(timeline);
    }
}