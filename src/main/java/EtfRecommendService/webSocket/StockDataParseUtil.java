package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class StockDataParseUtil {
    public static StockPriceData parseFromDelimitedFields(String[] field){
        if (field.length < 5){
            throw new IllegalArgumentException("필드 부족");
        }
        return StockPriceData.builder()
                .code(field[0])
                .price(Double.parseDouble(field[1]))
                .sign(field[2])
                .rate(Double.parseDouble(field[3]))
                .volume(Long.parseLong(field[4]))
                .build();
    }

    //json 메세지 -> stockpricedata로 변환
    public static StockPriceData parseFromJson(JsonNode json){
        return StockPriceData.builder()
                .code(json.path("code").asText())
                .price(json.path("price").asDouble())
                .sign(json.path("sign").asText())
                .rate(json.path("rate").asDouble())
                .volume(json.path("volume").asLong())
                .build();
    }
}
