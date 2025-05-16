package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;


//내부 호출용
//api 파싱 핸들러 - websocketConnectionService에서 받은 원시 데이터 파싱 및 가공 + webSocketBroadcaster로 전달
@Slf4j
@Component
public class KisWebSocketHandler{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final StockDataParser stockDataParseUtil;
    private final WebSocketBroadcaster webSocketBroadcaster;

    public KisWebSocketHandler(StockDataParser stockDataParseUtil, WebSocketBroadcaster webSocketBroadcaster) {
        this.stockDataParseUtil = stockDataParseUtil;
        this.webSocketBroadcaster = webSocketBroadcaster;
    }

    // WebSocketConnectionService 에서 호출됨
    public void handleText(String payload) {
        if (payload.startsWith("{")) {
            handleJsonMessage(payload);
        } else if (payload.contains("|")) {
            handlePipeMessage(payload);
        } else {
            handleUnknownMessage(payload);
        }
    }

    private void handleJsonMessage(String json) {
        log.info("[JSON 메시지] {}", json);
        try {
            JsonNode root = objectMapper.readTree(json);
            String trId = root.path("header").path("tr_id").asText();

            if ("PINGPONG".equals(trId)) {
                log.info("[PINGPONG] {}", root.path("header").path("datetime").asText());
            } else if ("H0STCNT0".equals(trId)) {
                log.info("[SUBSCRIBE SUCCESS] {}", root.path("header").path("tr_key").asText());
            } else {
                JsonNode output = root.path("body").path("output");
                StockPriceData data = stockDataParseUtil.parseFromJson(output);
                log.info("[기타 JSON 데이터] {}", data);

                //전체 목록 - 모든 세션에 전달
                webSocketBroadcaster.broadcast(data);
                //상세 목록 - 요청한 세션에만 전달
                webSocketBroadcaster.broadcastToSubscribers(data);
            }
        } catch (Exception e) {
            log.error("[JSON 파싱 오류] payload={}", json, e);
        }
    }

    private void handlePipeMessage(String pipe) {
        log.info("[PIPE 메시지] {}", pipe);
        String[] parts = pipe.split("\\|");
        if (parts.length >= 4 && parts[3].contains("^")) {
            String[] fields = parts[3].split("\\^");
            try {
                StockPriceData data = stockDataParseUtil.parseFromDelimitedFields(fields);
                log.info("[시세 데이터] {}", data);

                //전체 종목페이지 프론트로 데이터 전송
                webSocketBroadcaster.broadcast(data);
                //상세 목록
                webSocketBroadcaster.broadcastToSubscribers(data);

            } catch (Exception e) {
                log.error("[PIPE 파싱 오류] fields={}", Arrays.toString(fields), e);
            }
        } else {
            log.info("[기타 PIPE 메시지] {}", pipe);
        }
    }

    private void handleUnknownMessage(String msg) {
        log.info("[기타 메시지] {}", msg);
    }
}
