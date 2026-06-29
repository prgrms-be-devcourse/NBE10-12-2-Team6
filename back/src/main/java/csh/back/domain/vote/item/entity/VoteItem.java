package csh.back.domain.vote.item.entity;

import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.domain.vote.vote.entity.Vote;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//멤버 엔티티
@Getter
@Entity
@Table(name = "trip_place_vote_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteItem extends BaseEntity {

    //FK
    //Join Vote Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id",  nullable = false)
    private Vote Vote;

    //FK
    //Join TripPlace Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id",  nullable = false)
    private TripPlace TripPlace;

    //생성자
    //빌드 사용
    @Builder
    private VoteItem(Vote vote, TripPlace tripPlace) {
        this.Vote = vote;
        this.TripPlace = tripPlace;
    }
}
