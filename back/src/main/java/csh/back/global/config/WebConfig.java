package csh.back.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로에 대해 CORS 허용
                .allowedOrigins("http://localhost:3000") // 2. 프론트엔드 주소 허용 (포트 번호 맞춰서 수정!)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true) // 5. 쿠키나 인증 정보(토큰 등)를 포함한 요청 허용
                .maxAge(3600); // 6. 프리플라이트(Preflight) 요청 캐싱 시간 (초 단위)
    }

//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        // @ApiV1이 붙은 컨트롤러는 /api/v1 매핑
//        configurer.addPathPrefix("/api/v1", HandlerTypePredicate.forAnnotation(ApiV1.class));
//    }
}