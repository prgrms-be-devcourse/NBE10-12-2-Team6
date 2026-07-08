package csh.back.domain.vote.vote.entity;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.vote.vote.enums.VoteConfirmStatus;
import csh.back.domain.vote.vote.enums.VoteStatus;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;


@Getter
@Entity
@Table(name = "trip_place_votes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

    //FK
    //Join TripGroup Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripGroup tripGroup;

    //FK
    //Join TripTimeline Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id",  nullable = false)
    private TimeLine timeLine;

    //FK
    //Join TripMember Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",  nullable = false)
    private TripMember tripMember;

    //만료시간
    private LocalDateTime expireTime;

    @Enumerated(STRING)
    private VoteStatus status;


    //생성자
    //빌드 사용
    @Builder
    private Vote(TripGroup tripGroup, TimeLine timeLine, TripMember tripMember, LocalDateTime expireTime) {
        this.tripGroup = tripGroup;
        this.timeLine = timeLine;
        this.tripMember = tripMember;
        this.expireTime = expireTime;
        this.status = VoteStatus.PENDDING;
    }

    public void updateVoteConfirmed() {
        this.status = VoteStatus.CONFIRMED;
    }
}
