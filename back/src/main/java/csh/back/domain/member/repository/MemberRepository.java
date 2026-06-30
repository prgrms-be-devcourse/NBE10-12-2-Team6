package csh.back.domain.member.repository;

import csh.back.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 레포지토리
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일 중복 체크
    boolean existsByEmail(String email);
}