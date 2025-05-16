package EtfRecommendService.webSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
        import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    private final FrontendWebSocketHandler frontendWebSocketHandler;

    public WebSocketConfig(FrontendWebSocketHandler frontendWebSocketHandler) {
        this.frontendWebSocketHandler = frontendWebSocketHandler;
    }

    @Value("${websocket.endpoints.stocks}")
    private String websocketEndpoint;

    @Value("${websocket.allowed-origins}")
    private String allowedOrigns;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(frontendWebSocketHandler, websocketEndpoint)  //ws://localhost:8080/ws/stocks
                .addInterceptors(new HttpSessionHandshakeInterceptor()) //기존 http 세션 id
                .setAllowedOrigins(allowedOrigns)  //http://localhost:3000/ 특정 도메인에서만 들어올 수 있도록 설정 바꾸기
                .withSockJS();
    }
}
