package csh.back.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDto(
        @NotBlank String refreshToken
) {}