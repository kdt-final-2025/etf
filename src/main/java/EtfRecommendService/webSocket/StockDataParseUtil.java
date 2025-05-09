package EtfRecommendService.webSocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class StockDataParseUtil {

    //실시간 데이터 문자열 배열로 들어올 경우 stockpricedata 객체로 변환
    //역직렬화 (문자열을 java 객체로)
    public StockPriceData parseFromDelimitedFields(String[] field){
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


    //json 형태 데이터를 stockpricedata로 변환
    public StockPriceData parseFromJson(JsonNode json){
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
