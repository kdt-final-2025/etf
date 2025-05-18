//package EtfRecommendService.webSocket;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
////프론트엔드와 웹소켓 연결 처리 부분
//// /ws/stocks로 접속하면 브로드캐스터에 등록 또는 해체
//@Slf4j
//@Component
//public class FrontendWebSocketHandler extends TextWebSocketHandler {
//    private final WebSocketBroadcaster webSocketBroadcaster;
//    //clientId 기준으로 연결 관리
//    private final Map<String, WebSocketSession> clientSessions = new ConcurrentHashMap<>();
//
//    public FrontendWebSocketHandler(WebSocketBroadcaster webSocketBroadcaster) {
//        this.webSocketBroadcaster = webSocketBroadcaster;
//    }
//
////    //유저 구분 - 어떤걸로 쓸지 미정
////    // uri에 clientId 없으면 로그인 안한 유저->uuid 생성해 사용자가 특정 요청 처리할 수 있도록함
////    private String getClientIdFromUri(WebSocketSession session){
////        String query = session.getUri().getQuery();
////        if (query == null) return null;
////        for (String param : query.split("&")){
////            String[] keyValue = param.split("=");
////            if (keyValue.length == 2 && "clientId".equals(keyValue[0])){
////                return keyValue[1];
////            }
////        }
////        return null;
////    }
//
//    //after~ : textwebsockethandler에서 제공하는 메소드, @override 필요
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session){
////        //프론트가 넘긴 clientId 없으면 null
////        String clientId = getClientIdFromUri(session);
////        if (clientId == null){
////            clientId = UUID.randomUUID().toString(); //로그인 여부 상관없이 항상 uuid 사용
////        }
//        //http 세션 id
//        String clientId = (String) session.getAttributes().get("HTTP.SESSION.ID");
//        //없으면 웹소켓 세션id를 clientId로
//        if (clientId == null){
//            clientId = session.getId();
//        }
//
//        //세션 등록 : 클라이언트 세션 관리하는 맵에 저장
//        clientSessions.put(clientId,session);
//        //웹소켓 세션 등록
//        webSocketBroadcaster.register(session);
//        log.info("웹소켓 연결 clientId ={}", clientId);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
//        //연결 종료 시 클라이언트 세션에서 해당 세션 제거
//        clientSessions.entrySet()
//                .removeIf(entry ->
//                        entry.getValue().equals(session));
//        webSocketBroadcaster.unregister(session);
//        log.info("웹소켓 연결 해제 : sessionId={}",session.getId());
//    }
//
//    //전체 목록는 KisWebSocketHandler에 있는 WebSocketBroadcaster.broadcast()를 통해 모든 세션에 전파
//    //핸들러가 따로 호출 안해도 됨
//
//    //프론트에서 종목 상세조회 요청 시 해당 종목을 구독자로 등록
//    public void subscribeToStock(WebSocketSession session, String stockCode){
//        webSocketBroadcaster.subscribe(session, stockCode);
//    }
//
//    //해당 종목을 구독 중인 유저에게만 시세 데이터 전송
//    public void broadcastStockPriceToSubscribers(StockPriceData data){
//        webSocketBroadcaster.broadcastToSubscribers(data);
//    }
//
//    //상세페이지 나가기
//    public void unsubscribeFromStock(WebSocketSession session, String stockCode) {
//        webSocketBroadcaster.unsubscribe(session, stockCode);
//    }
//
//    //내부에서 자동 호출
//    //프론트가 종목 상세 클릭했을때 백엔드가 종목코드 전송, 해당 세션을 종목 구독자로 등록
//    //상세페이지 나가면 알아서 종목 구독 제거
//    // 핸들러 = 메세지 해석 후 어떤 동작할지 결정하는 역할
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//
//        if (payload.startsWith("SUBSCRIBE|")) {
//            String stockCode = payload.split("\\|")[1];
//            subscribeToStock(session, stockCode);
//            log.info("상세조회 요청: {} - {}", session.getId(),stockCode);
//        }
//        else if (payload.startsWith("UNSUBSCRIBE|")) {
//            String stockCode = payload.split("\\|")[1];
//            unsubscribeFromStock(session, stockCode);
//        }
//    }
//}
