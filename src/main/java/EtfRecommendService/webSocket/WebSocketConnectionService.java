package EtfRecommendService.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.List;

//웹소켓키+핸들러로 실제 연결, 메세지 송수신
@Service
public class WebSocketConnectionService {

    @Value("${kis.websocket-url}")
    private String apiUrl;

    private final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();

    //trId:실시간 TR ID (e.g. "H0STCNT0")
    // trKey:종목코드
    // 웹소켓 연결
    public void connect(String approvalKey, String trId, List<String> trKeys) {

        client.execute(
                        URI.create(apiUrl + "?approval_key=" + approvalKey),
                        session -> {
                            Flux<WebSocketMessage> sendMessages = Flux.fromIterable(trKeys)
                                    .map(trKey -> session.textMessage(buildPayload(approvalKey, trId, trKey)));

                            Mono<Void> sendAll = session.send(sendMessages);

                            Mono<Void> receive = session.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .doOnNext(this::handleMessage)
                                    .then();

                            return sendAll.then(receive);
                        }
                )
                .doOnError(err -> System.err.println("웹소켓 에러: " + err.getMessage()))
                .subscribe();
    }

    private String buildPayload(String approvalKey, String trId, String trKey) {
        return String.format(
                "{\"header\":{\"approval_key\":\"%s\",\"custtype\":\"P\",\"tr_type\":\"1\",\"content-type\":\"utf-8\",\"tr_id\":\"%s\"},"
                        + "\"body\":{\"input\":{\"tr_id\":\"%s\",\"tr_key\":\"%s\"}}}",
                approvalKey, trId, trId, trKey
        );
    }

    //서버로 수신한 데이터 처리
    private void handleMessage(String txt) {
        if (txt.contains("|")) {
            String[] fields = txt.split("\\|");
            StockPriceData data = StockPriceData.parseFromFields(fields);
            System.out.println(data);
        } else {
            System.out.println("[info]" + txt);
        }
    }
}
