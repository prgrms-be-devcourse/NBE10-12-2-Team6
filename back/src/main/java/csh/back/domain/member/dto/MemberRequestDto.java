package csh.back.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 회원가입 요청 DTO
public record MemberRequestDto(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name
) {}