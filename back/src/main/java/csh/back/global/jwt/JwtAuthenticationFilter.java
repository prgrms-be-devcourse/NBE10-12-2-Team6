package csh.back.global.jwt;

import csh.back.domain.member.dto.response.AuthFilterDto;
import csh.back.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Authorization: Bearer <refreshToken(UUID)> <accessToken(JWT)>
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String[] parts = header.substring(7).split(" ");
            String refreshToken = parts[0];
            String accessToken = parts.length > 1 ? parts[1] : null;

            if (accessToken != null && jwtUtil.isValid(accessToken)) {
                // accessToken 유효 → 인증 처리
                setAuthentication(jwtUtil.getEmail(accessToken), jwtUtil.getMemberId(accessToken));
            } else { // <- 분기점 (백에서 엑세스 토큰 갱신 로직을 수행하는 방식 v1 : Authorization 헤더에 실어서 보내는 방식) <-- 강사님 피드백
                // accessToken 만료 또는 없음 → refreshToken으로 DB 조회 후 새 accessToken 발급
                memberRepository.findByRefreshToken(refreshToken).ifPresent(member -> {
                    String newAccessToken = jwtUtil.generateAccessToken(member.getId(), member.getEmail());
                    response.setHeader("Authorization", "Bearer " + member.getRefreshToken() + " " + newAccessToken);
                    setAuthentication(member.getEmail(), member.getId());
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    // SecurityContextHolder에 인증 정보 등록 - principal: AuthFilterDto, details: memberId
    private void setAuthentication(String email, Long memberId) {
        AuthFilterDto principal = new AuthFilterDto(memberId, email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        authentication.setDetails(memberId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}