package csh.back.domain.trip.timeline.entity;

import csh.back.domain.trip.group.entity.TripGroup;
import csh.back.domain.trip.place.entity.TripPlace;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//타임라인 엔티티 -> 여행 중 사진 첨부
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trip_timelines")
public class TimeLine extends BaseEntity {

    //FK
    //Join tripGroup Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private TripGroup tripGroup;

    //FK
    //Join TripPlace Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_place_id", nullable = false)
    private TripPlace tripPlace;

    //시작 시간
    private LocalDateTime startTime;

    //끝난 시간
    private LocalDateTime endTime;


    //생성자
    //빌드 사용
    @Builder
    private TimeLine(TripGroup tripGroup, TripPlace tripPlace, LocalDateTime startTime, LocalDateTime endTime) {
        this.tripGroup = tripGroup;
        this.tripPlace = tripPlace;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
