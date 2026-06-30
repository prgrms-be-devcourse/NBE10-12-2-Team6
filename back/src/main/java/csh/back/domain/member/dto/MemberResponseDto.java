package csh.back.domain.member.dto;

public class MemberResponseDto {

    public record SignUpResponse(
            Long id,
            String email,
            String name
    ) {}
}