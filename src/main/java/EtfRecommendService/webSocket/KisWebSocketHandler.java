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
    private final StockDataParser stockDataParser;
    private final StockStompController stompController;
    private final MessageTypeClassifier classifier;
    private final RedisService redisService;

    public KisWebSocketHandler(StockDataParser stockDataParser, StockStompController stompController, MessageTypeClassifier classifier, RedisService redisService) {
        this.stockDataParser = stockDataParser;
        this.stompController = stompController;
        this.classifier = classifier;
        this.redisService = redisService;
    }

    //핸들러는 로깅만 남김
    // classifier 가 어떤 유형의 메시지인지 확인 후 파서로 넘김
    //파서의 결과를 받아서 브로드캐스트
    public void handleText(String payload) {
        WebSocketMessageType type = classifier.classify(payload);

        switch (type) {
            case PINGPONG -> log.info("[PINGPONG] ...");
            case SUBSCRIBE_SUCCESS -> log.info("[SUBSCRIBE SUCCESS] ...");
            case STOCK_PRICE_DATA -> {
                try {
                    StockPriceData data;
                    if (payload.startsWith("{")) {
                        data = stockDataParser.parseFromJsonString(payload);
                    } else {
                        data = stockDataParser.parseFromPipe(payload);
                    }
                    //Redis에 저장
                    redisService.saveStockPriceData(data);
                    stompController.broadcast(data);
                } catch (Exception e) {
                    log.error("[파싱 오류] payload={}", payload, e);
                }
            }
            case UNKNOWN -> log.info("[기타 메시지] {}", payload);
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
