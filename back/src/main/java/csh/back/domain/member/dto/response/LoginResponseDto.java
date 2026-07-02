package csh.back.domain.member.dto.response;

import csh.back.domain.member.entity.Member;

// 로그인 응답 DTO - 토큰은 응답 헤더(Authorization: Bearer <refreshToken(UUID)> <accessToken(JWT)>)로 전달
public record LoginResponseDto(
        Long id,
        String email,
        String name
) {
    public static LoginResponseDto from(Member member) {
        return new LoginResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}