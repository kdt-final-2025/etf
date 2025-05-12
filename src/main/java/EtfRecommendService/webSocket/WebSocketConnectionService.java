package EtfRecommendService.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.net.URI;
import java.util.List;

//한국투자 api에 클라이언트로 연결 + 메세지 받아오는 역할
//ReactorNettyWebSocketClient를 이용
// 웹소켓키, id, 종목코드 받아서 웹소켓 연결
@Service
public class WebSocketConnectionService {

    @Value("${kis.websocket-url}")
    private String apiUrl;

    private final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();

    private final KisWebSocketHandler kisWebSocketHandler;
    private final StockDataParseUtil stockDataParseUtil;

    public WebSocketConnectionService(KisWebSocketHandler kisWebSocketHandler, StockDataParseUtil stockDataParseUtil) {
        this.kisWebSocketHandler = kisWebSocketHandler;

        this.stockDataParseUtil = stockDataParseUtil;
    }

    //trId:실시간 TR ID ("H0STCNT0")
    // trKey:종목코드
    // 웹소켓 연결- 메세지 전송 + 윈시 데이터 받음
    public void connect(String approvalKey, String trId, List<String> trKeys) {
        System.out.println("connect() 호출됨");
        System.out.println("approvalKey: " + approvalKey);
        System.out.println("trId: " + trId);
        System.out.println("trKeys: " + trKeys);

        client.execute(
                        URI.create(apiUrl + "?approval_key=" + approvalKey),
                        session -> {
                            //여러 종목 구독 요청 20ms 간격으로 전송
                            Flux<WebSocketMessage> sendMessages = Flux.fromIterable(trKeys)
                                    .delayElements(Duration.ofMillis(20))
                                    .map(trKey -> buildPayload(approvalKey,trId,trKey))
                                    .map(session::textMessage);

                            Mono<Void> sendAll = session.send(sendMessages);

                            //원시 데이터 받음 - 파싱은 핸들러에 맡김
                            Mono<Void> receive = session.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .doOnNext(kisWebSocketHandler::handleText)
                                    .then();

                            return sendAll.then(receive);
                        }
                )
                .doOnError(err -> System.err.println("웹소켓 에러: " + err.getMessage()))
                .subscribe();
    }


    //한투 api로 보내는 구독 요청 메세지
    private String buildPayload(String approvalKey, String trId, String trKey) {
        return String.format(
                "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\",\"tr_id\":\"%s\"},"
                        + "\"body\":{\"input\":{\"tr_id\":\"%s\",\"tr_key\":\"%s\"}}}",
                approvalKey, trId, trId, trKey
        );
    }

//    //reactor netty 클라이언트로 받은 원시 데이터 처리
//    private void handleMessage(String txt){
//        if (txt.startsWith("{")){
//            System.out.println("[json 메세지]"+txt);
//        } else if (txt.contains("|")) {
//            System.out.println("[pipe 메세지]"+txt);
//            String[] parts = txt.split("\\|");
//
//            if (parts.length >= 4 && parts[3].contains("^")){
//                String[] fields = parts[3].split("\\^");
//
//                try {
//                    StockPriceData data = stockDataParseUtil.parseFromDelimitedFields(fields);
//                    System.out.println("[시세 데이터]"+ data);
//                }catch (Exception e){
//                    System.out.println("시세 데이터 파싱 오류:"+ Arrays.toString(fields));  //문자열로 바꿔주는 유틸
//                    e.printStackTrace();
//                }
//            }
//            else {
//                System.out.println("[구독 응당 등 기타 pipe 메세지]"+txt);
//            }
//        }
//        else {
//            System.out.println("[기타 메세지]"+txt);
//        }
//    }
}
