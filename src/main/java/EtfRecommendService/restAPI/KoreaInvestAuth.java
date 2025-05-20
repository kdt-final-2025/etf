package EtfRecommendService.restAPI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

//appKey, appSecret으로 인증 토큰을 발급받고 저장 - 웹소켓 접근 키랑 다름
//엑세스 토큰 발급 시점부터 24시간 유효, 하루동안 재사용 가능
@Component
@AllArgsConstructor
public class KoreaInvestAuth {

    @Value("${kis.appkey}")
    private String appKey;

    @Value("${kis.secretkey}")
    private String secretKey;

    //HTTP 통신을 위한 도구로 RESTful API 웹 서비스와의 상호작용을 쉽게 외부 도메인에서 데이터를 가져오거나 전송할 때 사용되는 스프링 프레임워크의 클래스
    private final WebClient webClient;

    private String cachedToken;
    private long tokenExpiryTime;

    //synchronized 멀티스레드 환경에서 토큰 갱신 충돌 방지
    public synchronized  String getAccessToken() {
        long now = System.currentTimeMillis();

        // 토큰 유효기간 2분 여유 두고 체크
        if (cachedToken != null && now < tokenExpiryTime - (2 * 60 * 1000)) {
            return cachedToken;
        }

        String url = "https://openapivts.koreainvestment.com:29443/oauth2/token";

        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "appkey", appKey,
                "secretkey", secretKey
        );

        Map<String, Object> response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("❌ 토큰 발급 실패: " + response);
        }

        cachedToken = (String) response.get("access_token");
        //24시간 후 만료시간 계산
        tokenExpiryTime = now + (24 * 60 * 60 * 1000);

        return cachedToken;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return secretKey;
    }
}

