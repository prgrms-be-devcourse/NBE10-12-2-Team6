package csh.back.domain.member.controller;

import csh.back.domain.member.dto.request.LoginRequestDto;
import csh.back.domain.member.dto.request.MemberRequestDto;
import csh.back.domain.member.dto.response.LoginResponseDto;
import csh.back.domain.member.dto.response.MemberResponseDto;
import csh.back.domain.member.service.MemberService;
import csh.back.domain.trip.member.service.TripMemberService;
import csh.back.global.annotation.ApiV1;
import csh.back.global.dto.ResponseData;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // 로그인 요청 처리 - Authorization 헤더에 "Bearer <refreshToken(UUID)> <accessToken(JWT)>" 형태로 전달
    @PostMapping("/login")
    public ResponseData<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto request,
            HttpServletResponse response) {
        MemberService.LoginResult result = memberService.login(request.email(), request.password());

        if (request.joinCode() != null) {
            tripMemberService.createJoinMember(request.joinCode(), result.userInfo().id());
        }

        response.setHeader("Authorization", "Bearer " + result.refreshToken() + " " + result.accessToken());
        return new ResponseData<>(200, result.userInfo());
    }
}