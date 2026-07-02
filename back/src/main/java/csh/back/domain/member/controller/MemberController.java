package csh.back.domain.member.controller;

import csh.back.domain.member.dto.request.LoginRequestDto;
import csh.back.domain.member.dto.request.MemberRequestDto;
import csh.back.domain.member.dto.request.TokenRefreshRequestDto;
import csh.back.domain.member.dto.response.LoginResponseDto;
import csh.back.domain.member.dto.response.MemberResponseDto;
import csh.back.domain.member.dto.response.TokenResponseDto;
import csh.back.domain.member.service.MemberService;
import csh.back.global.annotation.ApiV1;
import csh.back.domain.trip.member.service.TripMemberService;
import csh.back.global.dto.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 회원 관련 요청을 처리하는 컨트롤러
@ApiV1
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberController {
	private final MemberService memberService;
	private final TripMemberService tripMemberService;

	// 회원가입 요청 처리
	@PostMapping("/signup")
	public ResponseData<MemberResponseDto> signUp(
			@RequestBody @Valid MemberRequestDto request) {
		return new ResponseData<>(201, memberService.signUp(request.email(), request.password(), request.name()));
	}

	// 로그인 요청 처리
	@PostMapping("/login")
	public ResponseData<LoginResponseDto> login(
			@RequestBody @Valid LoginRequestDto request) {
		LoginResponseDto response = memberService.login(request.email(), request.password());

		if (request.joinCode() != null) {
			tripMemberService.createJoinMember(request.joinCode(), response.id());
		}
		return new ResponseData<>(200, response);
	}

    // Access Token 재발급 (Refresh Token으로 요청)
    @PostMapping("/refresh")
    public ResponseData<TokenResponseDto> refresh(
            @RequestBody @Valid TokenRefreshRequestDto request) {
        return new ResponseData<>(200, memberService.refresh(request.refreshToken()));
    }
    /*
    // 로그아웃 - JWT 필터가 세팅한 인증 정보에서 memberId를 꺼내 Refresh Token 삭제
    @PostMapping("/logout")
    public ResponseData<Void> logout() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long memberId = (Long) auth.getDetails();

        memberService.logout(memberId);
        return new ResponseData<>(200, null);
    }
    */
}