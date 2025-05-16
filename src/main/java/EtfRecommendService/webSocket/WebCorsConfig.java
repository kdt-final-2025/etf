//package EtfRecommendService.webSocket;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
////rest cors 적용
//@Configuration
//public class WebCorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")                   // REST 엔드포인트 패턴
//                .allowedOrigins("http://localhost:3000") // 프론트 주소
//                .allowedMethods("GET","POST","OPTIONS")
//                .allowCredentials(true);
//    }
//}
//
