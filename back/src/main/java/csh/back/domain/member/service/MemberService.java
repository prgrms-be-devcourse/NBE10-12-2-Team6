package csh.back.domain.member.service;

import csh.back.domain.member.dto.response.LoginResponseDto;
import csh.back.domain.member.dto.response.MemberResponseDto;
import csh.back.domain.member.dto.response.TokenResponseDto;
import csh.back.domain.member.entity.Member;
import csh.back.domain.member.entity.RefreshToken;
import csh.back.domain.member.repository.MemberRepository;
import csh.back.domain.member.repository.RefreshTokenRepository;
import csh.back.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        // DB 저장
        Member member = memberRepository.save(newMember);

        return MemberResponseDto.from(member);
    }

    // 로그인
    @Transactional
    public LoginResponseDto login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getEmail());

        // Refresh Token DB 저장 (이미 있으면 갱신, 없으면 신규 저장)
        refreshTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        rt -> rt.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder().member(member).token(refreshToken).build()
                        )
                );

        return LoginResponseDto.of(member, accessToken, refreshToken);
    }

    // Refresh Token으로 Access Token 재발급
    @Transactional
    public TokenResponseDto refresh(String refreshToken) {
        // 토큰 유효성 검증 (만료·위조 여부)
        if (!jwtUtil.isValid(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Long memberId = jwtUtil.getMemberId(refreshToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // DB에 저장된 토큰과 일치하는지 확인 (탈취된 토큰 재사용 방지)
        RefreshToken saved = refreshTokenRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        String newAccessToken = jwtUtil.generateAccessToken(member.getId(), member.getEmail());
        // Refresh Token도 새로 발급해 DB 갱신 (토큰 로테이션)
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getEmail());
        saved.updateToken(newRefreshToken);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
    /*
    // 로그아웃 - Refresh Token을 DB에서 삭제해 재발급 차단
    @Transactional
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // Refresh Token 삭제 (이미 로그아웃된 경우 무시)
        refreshTokenRepository.findByMember(member)
                .ifPresent(rt -> refreshTokenRepository.deleteByMember(member));
    }
    */
}