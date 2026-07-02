package csh.back.domain.member.repository;

import csh.back.domain.member.entity.Member;
import csh.back.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 로그인 시 기존 토큰 조회 (있으면 갱신, 없으면 신규 저장)
    Optional<RefreshToken> findByMember(Member member);

    // 로그아웃 시 Refresh Token 삭제
    void deleteByMember(Member member);
}