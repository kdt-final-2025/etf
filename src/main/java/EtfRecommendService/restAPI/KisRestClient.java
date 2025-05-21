package EtfRecommendService.restAPI;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//ETF 종목 하나의 실시간 시세(스냅샷) 를 한국투자증권에 요청해서 가져옴
@Component
@RequiredArgsConstructor
public class KisRestClient {

    private final WebClient webClient;
    private final KoreaInvestAuth koreaInvestAuth; // appkey/appsecret → accessToken 발급

    //여러개 조회 - 하루치 데이터 저장할때 사용
    public List<StockPriceSnapshotDTO> fetchAllSnapshots(List<String> stockCodes) {
        return stockCodes.stream()
                .map(this::fetchSingleSnapshot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    //한개 조회 - 상세페이지나 실시간 조회용
    public Optional<StockPriceSnapshotDTO> fetchSingleSnapshot(String stockCode) {
        String url = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-ccnl";

        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(url)
                            .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                            .queryParam("FID_INPUT_ISCD", stockCode)
                            .build()
                    )
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("authorization", "Bearer " + koreaInvestAuth.getAccessToken())
                    .header("appkey", koreaInvestAuth.getAppKey())
                    .header("appsecret", koreaInvestAuth.getAppSecret())
                    .header("tr_id", "FHKST01010300")  //모의투자용
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || !response.containsKey("output")) {
                return Optional.empty();
            }

            Map<String, String> output = (Map<String, String>) response.get("output");
            return Optional.of(StockPriceSnapshotDTO.fromKisResponse(output));

        } catch (Exception e) {
            System.err.println("❌ 종목 코드 " + stockCode + " 요청 실패: " + e.getMessage());
            return Optional.empty();
        }
    }
}
