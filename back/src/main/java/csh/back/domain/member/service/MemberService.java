package csh.back.domain.member.service;

import csh.back.domain.member.dto.MemberResponseDto;
import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public MemberResponseDto signUp(String email, String password, String name) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // Member 객체 생성
        Member newMember = Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();

        // DB 저장
        Member member = memberRepository.save(newMember);

        return MemberResponseDto.from(member);
    }
}