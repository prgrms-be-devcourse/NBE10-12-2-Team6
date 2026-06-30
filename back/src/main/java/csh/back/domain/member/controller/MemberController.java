package csh.back.domain.member.controller;

import csh.back.domain.member.dto.MemberRequestDto;
import csh.back.domain.member.dto.MemberResponseDto;
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
            @RequestBody @Valid MemberRequestDto request) { //이거 Base entity에서 메세지 빼놓은 규격인데 넣어놔서 오류뜨는거같아요~
        return new ResponseData<>(201, memberService.signUp(request.email(), request.password(), request.name()));
    }
}