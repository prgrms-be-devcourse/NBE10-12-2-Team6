package csh.back.domain.vote.user.entity;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.domain.vote.item.entity.VoteItem;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trip_vote_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteUser extends BaseEntity {

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
    private VoteUser(VoteItem VoteItem, TripMember TripMember) {
        this.VoteItem = VoteItem;
        this.TripMember = TripMember;
    }
}
