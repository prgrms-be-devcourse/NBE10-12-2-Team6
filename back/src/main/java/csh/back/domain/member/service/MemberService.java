package csh.back.domain.member.service;

import csh.back.domain.member.dto.MemberResponseDto;
import csh.back.domain.member.entity.Member;
import csh.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원가입
    public MemberResponseDto.SignUpResponse signUp(String email, String password, String name) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // Member 객체 생성 후 저장
        Member saved = memberRepository.save(
                Member.builder().email(email).password(password).name(name).build()
        );

        return new MemberResponseDto.SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

}