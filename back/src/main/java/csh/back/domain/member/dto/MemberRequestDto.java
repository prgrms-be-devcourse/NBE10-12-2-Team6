package csh.back.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 회원 관련 요청 DTO 모음
public class MemberRequestDto {

    // 회원가입 요청 DTO
    public record SignUpRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String name
    ) {}

}
