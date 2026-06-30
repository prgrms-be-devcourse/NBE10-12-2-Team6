package csh.back.domain.member.dto.response;

import csh.back.domain.member.entity.Member;

// 로그인 응답 DTO
public record LoginResponseDto(
        Long id,
        String email,
        String name
) {
    // 엔티티 -> DTO 변환
    public static LoginResponseDto from(Member member) {
        return new LoginResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName()
        );
    }
}