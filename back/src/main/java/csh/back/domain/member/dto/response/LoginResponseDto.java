package csh.back.domain.member.dto.response;

import csh.back.domain.member.entity.Member;

public record LoginResponseDto(
        Long id,
        String email,
        String name,
        String accessToken,
        String refreshToken
) {
    public static LoginResponseDto of(Member member, String accessToken, String refreshToken) {
        return new LoginResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                accessToken,
                refreshToken
        );
    }
}