package EtfRecommendService.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Value("${websocket.endpoints.stocks}")
//    private String websocketEndpoint;

    @Value("${websocket.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 subscribe 할 prefix
        config.enableSimpleBroker("/topic");
        // 클라이언트가 send 할 때 사용할 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS fallback 지원
        registry.addEndpoint("/ws/stocks")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS();
    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry
//                .addHandler(frontendWebSocketHandler, websocketEndpoint)  //ws://localhost:8080/ws/stocks
//                .addInterceptors(new HttpSessionHandshakeInterceptor()) //기존 http 세션 id
//                .setAllowedOrigins(allowedOrigins)//http://localhost:3000/ 특정 도메인에서만 들어올 수 있도록 설정 바꾸기
////                .withSockJS()
////                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js") //호환성 문제
////                .setSessionCookieNeeded(false)
////                .setWebSocketEnabled(true)  // WebSocket 전송 활성화
////                .setDisconnectDelay(30 * 1000)
//        ;
//    }
}
