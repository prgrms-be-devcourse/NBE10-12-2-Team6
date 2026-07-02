package csh.back.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // Access Token 생성 (만료시간: 30분)
    public String generateAccessToken(Long memberId, String email) {
        return buildToken(memberId, email, accessExpiration);
    }

    // Refresh Token 생성 (만료시간: 7일)
    public String generateRefreshToken(Long memberId, String email) {
        return buildToken(memberId, email, refreshExpiration);
    }

    // 토큰 생성 공통 로직
    private String buildToken(Long memberId, String email, long expiration) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)              // 토큰 주인 (이메일)
                .claim("memberId", memberId) // 추가 데이터 (회원 ID)
                .issuedAt(now)               // 생성 시간
                .expiration(new Date(now.getTime() + expiration)) // 만료 시간
                .signWith(secretKey)         // 서명
                .compact();
    }

    // 토큰에서 클레임(데이터) 추출 - 만료/위조된 토큰이면 예외 발생
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 유효성 검증 (만료, 위조 여부 확인)
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰에서 회원 ID 추출 (Integer로 역직렬화될 수 있어 Number로 변환)
    public Long getMemberId(String token) {
        return ((Number) parseClaims(token).get("memberId")).longValue();
    }
}