package csh.back.domain.member.dto.response;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) {}