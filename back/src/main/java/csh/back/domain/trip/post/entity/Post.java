package csh.back.domain.trip.post.entity;

import csh.back.domain.member.entity.Member;
import csh.back.domain.trip.group.entity.TripGroup;
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
    @JoinColumn(name = "member_id", nullable = false)
    private TripMember author;

    //FK
    //Join TripTimeline Table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id", nullable = false)
    private TimeLine timeLine;

    //타입
    //영상인지 이미지인지
    private Boolean isImg;

    //contentUrl
    //이미지 불러오기
    private String contentUrl;


    //생성자
    //빌드 사용
    @Builder
    private Post(TripMember author, TimeLine timeLine, Boolean isImg, String contentUrl) {
        this.author = author;
        this.timeLine = timeLine;
        this.isImg = isImg;
        this.contentUrl = contentUrl;
    }
}
