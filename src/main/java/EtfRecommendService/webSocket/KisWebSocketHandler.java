package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.List;

//종목 구독 및 성공, 연결 json 메세지
public class KisWebSocketHandler extends TextWebSocketHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(KisWebSocketHandler.class); //sout 대신 logger 사용, 디버깅용

    private final String approvalKey;
    private final String trId;
    private final List<String> codes;
    private final StockDataParseUtil stockDataParseUtil;

    public KisWebSocketHandler(String approvalKey, String trId, List<String> codes, StockDataParseUtil stockDataParseUtil) {
        this.approvalKey = approvalKey;
        this.trId = trId;
        this.codes = codes;
        this.stockDataParseUtil = stockDataParseUtil;
    }

    //종목 구독(요청)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        for (String code : codes) {
            String subscribe = String.format(
                    "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\",\"tr_id\":\"%s\"},"
                            + "\"body\":{\"input\":{\"tr_id\":\"%s\",\"tr_key\":\"%s\"}}}",
                    approvalKey, trId, trId, code
            );
            session.sendMessage(new TextMessage(subscribe));
            Thread.sleep(20);
        }
        logger.info("모든 구독 요청 전송 완료");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // 메시지가 파이프(|)나 캐릿(^)으로 들어온다면 parsesFromDelemitedFields로 파싱
        if (payload.contains("|")) {
            String[] fields = payload.split("\\|");
            StockPriceData data = stockDataParseUtil.parseFromDelimitedFields(fields);
            System.out.println("[체결 데이터]"+data);

        }

        // JSON 형태 메시지 (구독 성공, 핑퐁 등)로 온다면 parseFormJson으로 파싱
        else if (payload.startsWith("{")) {
            try {
                JsonNode root = objectMapper.readTree(payload);
                String trId = root.path("header").path("tr_id").asText();

                if ("PINGPONG".equals(trId)) {

                    System.out.println("[PINGPONG] " + root.path("header").path("datetime").asText());
                } else if ("H0STCNT0".equals(trId)) {
                    String trKey = root.path("header").path("tr_key").asText();
                    System.out.println("[SUBSCRIBE SUCCESS] " + trKey);
                } else {
                    JsonNode bodyOutput = root.path("body").path("output");
                    StockPriceData data = stockDataParseUtil.parseFromJson(bodyOutput);
                    System.out.println("[기타 JSON 데이터] " + data);
                }

            } catch (Exception e) {
                System.err.println("[JSON 파싱 오류] " + e.getMessage());
                System.err.println("[원본 메시지] " + payload);
            }
        }

        else {
            // 구독 성공 or 기타 메시지 로깅
            System.out.println("[기타 메세지] " + payload);
        }
    }
}
