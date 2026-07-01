package csh.back.domain.trip.group.entity;

import csh.back.domain.member.entity.Member;
import csh.back.domain.trip.group.dto.request.TripGroupModifyRequest;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//tripGroups 엔티티
@Getter
@Entity
@Table(name = "tripGroups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripGroup extends BaseEntity {
    //방장
    @ManyToOne(fetch = FetchType.LAZY)
    //FK
    //join member Table
    @JoinColumn(name ="member_id", nullable = false)
    private Member owner;

    //여행 그룹 이름
    private String name;

    //지역 이름
    private String region;

    //nights
    private int nights;

    //초대 링크
    @Column(unique = true)
    private String joinCode;

    //여행 시작일
    private LocalDate startDate;

    //여행 마무리 날짜
    private LocalDate endDate;

    //투표 시작 상태(초기 생성은 false)
    @ColumnDefault("false")
    private boolean isVote;

    //생성자
    //buider 사용
    @Builder
        private TripGroup(Member owner, String name, String region, int nights, String joinCode, LocalDate startDate, LocalDate endDate) {
            this.owner = owner;
            this.name = name;
            this.region = region;
            this.nights = nights;
            this.joinCode = joinCode;
            this.startDate = startDate;
            this.endDate = endDate;
    }

    //name 수정 메서드
    public void modify(TripGroupModifyRequest request) {
        if (request.name() != null && !request.name().isBlank()) this.name = request.name();
        if (request.region() != null && !request.region().isBlank()) this.region = request.region();
        if (request.startDate() != null && !request.startDate().isBlank()) this.startDate = LocalDate.parse(request.startDate());
        if (request.endDate() != null && !request.endDate().isBlank()) this.endDate = LocalDate.parse(request.endDate());
    }
}
