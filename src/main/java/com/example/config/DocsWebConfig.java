// src/main/java/com/example/config/DocsWebConfig.java
package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class DocsWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/docs/**")
                .addResourceLocations(
                        "file:build/docs/asciidoc/html5/",  // 항상 갱신된 빌드 폴더 우선
                        "classpath:/static/docs/"           // 패키징된 문서
                )
                .setCacheControl(CacheControl.noStore());  // 캐시 사용 안 함
    }
}
