package csh.back.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 토큰 소유자 (회원당 1개만 허용)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // Refresh Token 값
    @Column(nullable = false)
    private String token;

    @Builder
    private RefreshToken(Member member, String token) {
        this.member = member;
        this.token = token;
    }

    // Access Token 만료 시 새 토큰으로 갱신
    public void updateToken(String token) {
        this.token = token;
    }
}