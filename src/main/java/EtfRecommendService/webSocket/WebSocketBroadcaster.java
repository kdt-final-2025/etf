package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//세션 + 브로드캐스팅
//전체 종목 시세는 모든 접속자에게 동일한 데이터 전송
@Slf4j
@Component
public class WebSocketBroadcaster {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public WebSocketBroadcaster(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //연결
    public void register(WebSocketSession session){
        sessions.add(session);
    }

    //연결 끊기
    public void unregister(WebSocketSession session){
        sessions.remove(session);
    }

    //전체 목록
    public void broadcast(StockPriceData data){
        try{
            String json = objectMapper.writeValueAsString(data);
            TextMessage message = new TextMessage(json);
            for (WebSocketSession session:sessions){
                if (session.isOpen()){
                    session.sendMessage(message);
                }
            }
        } catch (IOException e){
            log.error("전체목록 메시지 브로드캐스팅 중 오류 발생",e);
        }
    }

    //subscribe,broadcastToSubscribers 둘은 항상 쌍으로 동작
    //상세 목록(종목 상세)
    // 어떤 세션이 해당 종목을 보고싶다는 정보 요청만 저장
    public void subscribe(WebSocketSession session, String code){
        userSubscriptions
                .computeIfAbsent(session, k -> ConcurrentHashMap.newKeySet())
                .add(code);
    }

    //사용자별 + 관심 목록 좋아요 (현재는 상세보기 페이지 전용)
    //들어온 종목 데이터의 종목코드 기준으로 해당 데이터 찾아서 세션에 전달
    //subscribe로 들어온 요청을 처리하는 곳
    public void broadcastToSubscribers(StockPriceData data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            TextMessage message = new TextMessage(json);
            for (Map.Entry<WebSocketSession, Set<String>> entry : userSubscriptions.entrySet()) {
                WebSocketSession session = entry.getKey();
                Set<String> codes = entry.getValue();
                if (session.isOpen() && codes.contains(data.stockCode())) {
                    session.sendMessage(message);
                }
            }
        } catch (IOException e) {
            log.error("상세목록 메시지 브로드캐스트 중 오류 발생",e);
        }
    }

    //상세목록 페이지 나가기
    public void unsubscribe(WebSocketSession session, String code) {
        Set<String> codes = userSubscriptions.get(session);
        if (codes != null) {
            codes.remove(code);
            if (codes.isEmpty()) {
                userSubscriptions.remove(session);
            }
        }
    }

}
