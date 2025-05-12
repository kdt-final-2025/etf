package EtfRecommendService.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


public class WebSocketConfig implements WebSocketConfigurer {
    private final FrontendWebSocketHandler frontendWebSocketHandler;

    public WebSocketConfig(FrontendWebSocketHandler frontendWebSocketHandler) {
        this.frontendWebSocketHandler = frontendWebSocketHandler;
    }

//    //클라이언트가 구독할 경로
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config){
//        config.enableSimpleBroker("/topic"); //브로커가 메세지 전달
//        config.setApplicationDestinationPrefixes("/app"); //클라이언트가 보낼 prefix
//    }
//
//    //클라이언트가 연결할 엔드포인트
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry){
//        registry
//                .addEndpoint("/ws-stock")  //ws://host/ws-stock
//                .setAllowedOriginPatterns("*")
//                .withSockJS();  //sockJS fallback 지원
//    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(frontendWebSocketHandler, "/ws/stocks")  //ws://localhost:8080/ws/stocks
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
