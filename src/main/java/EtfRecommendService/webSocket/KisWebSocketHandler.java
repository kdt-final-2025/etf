package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

//메세지 핸들러 - 서비스 클래스에서 사용
//메세지 수신 및 처리 흐름 제어
public class KisWebSocketHandler extends TextWebSocketHandler {
    private final String approvalKey;
    private final String trId;
    private final List<String> codes;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public KisWebSocketHandler(String approvalKey, String trId, List<String> codes) {
        this.approvalKey = approvalKey;
        this.trId = trId;
        this.codes = codes;
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
        System.out.println("모든 구독 요청 전송 완료");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // 메시지가 파이프(|) 구분 데이터면 파싱
        if (payload.contains("|")) {
            String[] fields = payload.split("\\|");
            StockPriceData data = StockDataParseUtil.parseFromDelimitedFields(fields);
            System.out.println(data);

        }
        else if (payload.startsWith("{")){
            JsonNode json = objectMapper
                    .readTree(payload)
                    .path("body")
                    .path("output");

            StockPriceData data = StockDataParseUtil.parseFromJson(json);
            System.out.println(data);
        }
        else {
            // 구독 성공 or 기타 메시지 로깅
            System.out.println("[MSG] " + payload);
        }
    }
}
