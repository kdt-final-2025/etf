package EtfRecommendService.restAPI;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableCaching
public class RestApiCacheConfig {
    // 기본 설정만 해도 됨. 더 세부 설정은 필요에 따라 추가 가능
}

