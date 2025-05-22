package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public record MessageTypeClassifier(

) {
    private WebSocketMessageType type;
    private JsonNode root;  // PINGPONG, SUBSCRIBE_SUCCESS, STOCK_PRICE_DATA(JSON)일 때 사용
    private StockPriceData stockPriceData; // STOCK_PRICE_DATA(PIPE, JSON)일 때 사용
}
