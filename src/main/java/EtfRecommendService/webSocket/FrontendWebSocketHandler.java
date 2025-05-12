package EtfRecommendService.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

//프론트엔드와 웹소켓 연결 처리 부분
// /ws/stocks로 접속하면 브로드캐스터에 등록 또는 해체
@Component
public class FrontendWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketBroadcaster webSocketBroadcaster;

    public FrontendWebSocketHandler(WebSocketBroadcaster webSocketBroadcaster) {
        this.webSocketBroadcaster = webSocketBroadcaster;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        webSocketBroadcaster.register(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        webSocketBroadcaster.unregister(session);
    }

    //특정목록 구독
    public void subscribeToStock(WebSocketSession session, String stockCode){
        webSocketBroadcaster.subscribe(session, stockCode);
    }

    //특정 목록
    public void broadcastStockPriceToSubscribers(StockPriceData data){
        webSocketBroadcaster.broadcastToSubscribers(data);
    }
}
