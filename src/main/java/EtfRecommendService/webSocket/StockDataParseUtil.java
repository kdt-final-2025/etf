package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;

public class StockDataParseUtil {
    public static StockPriceData parseFromDelimitedFields(String[] field){
        if (field.length < 14){
            throw new IllegalArgumentException("필드 부족");
        }
        return StockPriceData.builder()
                .stockCode(field[0])
                .currentPrice(Double.parseDouble(field[2]))
                .dayOverDaySign(field[3])
                .dayOverDayChange(Integer.parseInt(field[4]))
                .dayOverDayRate(Double.parseDouble(field[5]))
                .accumulatedVolume(Long.parseLong(field[13]))
                .build();
    }

    //json 메세지 -> stockpricedata로 변환
    public static StockPriceData parseFromJson(JsonNode json){
        return StockPriceData.builder()
                .stockCode(json.path("stockCode").asText())
                .currentPrice(json.path("currentPrice").asDouble())
                .dayOverDaySign(json.path("dayOverDaySign").asText())
                .dayOverDayChange(json.path("dayOverDayChange").asInt())
                .dayOverDayRate(json.path("dayOverDayRate").asDouble())
                .accumulatedVolume(json.path("accumulatedVolume").asLong())
                .build();
    }
}
