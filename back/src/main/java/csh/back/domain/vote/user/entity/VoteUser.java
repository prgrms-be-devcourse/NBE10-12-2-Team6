package csh.back.domain.vote.user.entity;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trip_vote_users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_vote_user",
                columnNames = {"vote_id", "member_id"}  // vote_id 기준
        )
)

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteUser extends BaseEntity {

    //FK
    //Join Vote Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id",  nullable = false)
    private Vote Vote;

    //FK
    //Join VoteItem Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_item_id",  nullable = false)
    private VoteItem VoteItem;

    //FK
    //Join TripMember Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",  nullable = false)
    private TripMember TripMember;

    //생성자
    //빌드 사용
    @Builder
    private VoteUser(Vote vote, VoteItem VoteItem, TripMember TripMember) {
        this.Vote = vote;
        this.VoteItem = VoteItem;
        this.TripMember = TripMember;
    }
}
