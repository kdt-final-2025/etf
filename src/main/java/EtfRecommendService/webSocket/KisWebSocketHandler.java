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
public class KisWebSocketHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final StockDataParser stockDataParser;
    private final StockStompController stompController;

    public KisWebSocketHandler(StockDataParser stockDataParser, StockStompController stompController) {
        this.stockDataParser = stockDataParser;
        this.stompController = stompController;
    }

    //핸들러는 로깅만 남기고, 모든 메시지를 파서로 넘김
    //파서의 결과를 받아서 브로드캐스트
    public void handleText(String payload) {
        log.info("[WebSocket 메시지] {}", payload);
        try {
            StockDataParser.WebSocketMessage message = stockDataParser.parseAndClassify(payload);
            switch (message.type) {
                case PINGPONG:
                    log.info("[PINGPONG] {}", message.root.path("header").path("datetime").asText());
                    break;
                case SUBSCRIBE_SUCCESS:
                    log.info("[SUBSCRIBE SUCCESS] {}", message.root.path("header").path("tr_key").asText());
                    break;
                case STOCK_PRICE_DATA:
                    log.info("[시세 데이터] {}", message.stockPriceData);
                    log.debug("[STOMP BROADCAST] sending to /topic/stocks/" + message.stockPriceData.stockCode());
                    stompController.broadcast(message.stockPriceData); //브로드캐스트
                    break;
                case UNKNOWN:
                    log.info("[기타 메시지] {}", payload);
                    break;
            }
        } catch (Exception e) {
            log.error("[메시지 파싱 오류] payload={}", payload, e);
        }
    }


//    // WebSocketConnectionService 에서 호출됨
//    public void handleText(String payload) {
//        if (payload.startsWith("{")) {
////            stockDataParseUtil.
//                    handleJsonMessage(payload);
//        } else if (payload.contains("|")) {
//            handlePipeMessage(payload);
//        } else {
//            handleUnknownMessage(payload);
//        }
//    }
//
////    시세 데이터가 들어오면, 파서에게 파싱을 위임
////    파싱된 데이터를 STOMP로 브로드캐스트
//    private void handleJsonMessage(String json) {
//        log.info("[JSON 메시지] {}", json);
//        try {
//            JsonNode root = objectMapper.readTree(json);
//            String trId = root.path("header").path("tr_id").asText();
//
//            if ("PINGPONG".equals(trId)) {
//                log.info("[PINGPONG] {}",
//                        root.path("header").path("datetime").asText());
//            } else if ("H0STCNT0".equals(trId)) {
//                log.info("[SUBSCRIBE SUCCESS] {}",
//                        root.path("header").path("tr_key").asText());
//            } else {
//                StockPriceData data = stockDataParseUtil.parseFromJsonString(json);
//                log.info("[기타 JSON 데이터] {}", data);
//                log.debug("[STOMP BROADCAST] sending to /topic/stocks/" + data.stockCode());
//                stompController.broadcast(data);
//            }
//        } catch (Exception e) {
//            log.error("[JSON 파싱 오류] payload={}", json, e);
//        }
//    }
//
//    private void handlePipeMessage(String pipe) {
//        log.info("[PIPE 메시지] {}", pipe);
//        try {
//            StockPriceData data = stockDataParseUtil.parseFromPipe(pipe);
//            log.info("[시세 데이터] {}", data);
//            stompController.broadcast(data);
//        } catch (Exception e) {
//            log.error("[PIPE 파싱 오류] pipe={}", pipe, e);
//        }
//    }
//
//    private void handleUnknownMessage(String msg) {
//        log.info("[기타 메시지] {}", msg);
//    }
}
