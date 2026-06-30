package csh.back.domain.member.controller;

import csh.back.domain.member.dto.request.LoginRequestDto;
import csh.back.domain.member.dto.request.MemberRequestDto;
import csh.back.domain.member.dto.response.LoginResponseDto;
import csh.back.domain.member.dto.response.MemberResponseDto;
import csh.back.domain.member.service.MemberService;
import csh.back.global.dto.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 회원 관련 요청을 처리하는 컨트롤러
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberController {
    private final MemberService memberService;

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
        return new ResponseData<>(200, memberService.login(request.email(), request.password()));
    }
}