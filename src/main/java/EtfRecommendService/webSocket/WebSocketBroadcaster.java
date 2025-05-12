package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//세션 + 브로드캐스팅
//전체 종목 시세는 모든 접속자에게 동일한 데이터 전송
@Component
public class WebSocketBroadcaster {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<WebSocketSession, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //전체 목록 보기
    public void register(WebSocketSession session){
        sessions.add(session);
    }

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
            e.printStackTrace();
        }
    }

    //상세 목록
    public void subscribe(WebSocketSession session, String code){
        userSubscriptions.computeIfAbsent(session,
                k -> ConcurrentHashMap.newKeySet()).add(code);
    }

    //상세 목록
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
            e.printStackTrace();
        }
    }



}
