package csh.back.global.config;

import csh.back.global.annotation.ApiV1;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // @ApiV1이 붙은 컨트롤러는 /api/v1 매핑
        configurer.addPathPrefix("/api/v1", HandlerTypePredicate.forAnnotation(ApiV1.class));
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //업로드 이미지 주소 따오는 로직
        registry.addResourceHandler("/uploadedimages/**")
                .addResourceLocations("file:uploadedimages/");
    }
}