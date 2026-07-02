package csh.back.domain.trip.post.entity;

import csh.back.domain.trip.member.entity.TripMember;
import csh.back.domain.trip.timeline.entity.TimeLine;
import csh.back.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    //FK
    //Join TripMember Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_member_id", nullable = false)
    private TripMember tripMemberId;

    //FK
    //Join TripTimeline Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id", nullable = false)
    private TimeLine timeLine;

    //이미지 여부
    private Boolean isImg;

    //이미지 URL
    private String contentUrl;

    //포스트 글 내용
    @Column
    private String content;
    //포스트에 포함 될 위치값
    private String location;

    @Builder
    private Post(
            TripMember tripMemberId,
            TimeLine timeLine,
            Boolean isImg,
            String content,
            String location,
            String contentUrl
    ) {
        this.tripMemberId =tripMemberId;
        this.timeLine = timeLine;
        this.isImg = isImg;
        this.content = content;
        this.location = location;
        this.contentUrl = contentUrl;
    }
    public void update(String content, String location) {
        this.content = content;
        this.location = location;
    }
}
