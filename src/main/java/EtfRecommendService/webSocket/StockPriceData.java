package EtfRecommendService.webSocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPriceData {
    private String stockCode; //종목코드 0
    private double currentPrice; //현재가 2
    private String dayOverDaySign; //전일 대비 부호 3
    private int dayOverDayChange; // 전일 대비 가격 4
    private double dayOverDayRate; //전일대비율(등락률) 5
    private long accumulatedVolume; //누적 거래량 13

    //데이터 파싱
    public static StockPriceData parseFromFields(String[] fields) {
        StockPriceData d = new StockPriceData();
        d.setStockCode(fields[0]);
        d.setCurrentPrice(Double.parseDouble(fields[2]));
        d.setDayOverDaySign(fields[3]);
        d.setDayOverDayChange(Integer.parseInt(fields[4]));
        d.setDayOverDayRate(Double.parseDouble(fields[5]));
        d.setAccumulatedVolume(Long.parseLong(fields[13]));
        return d;
    }

    @Override
    public String toString() {
        return String.format("종목코드=%s 체결가=%.2f 전일대비부호=%s 전일대비가격=%d, 전일대비율(등락률)=%.2f%% 누적거래량=%d",
                stockCode, currentPrice, dayOverDaySign, dayOverDayChange, dayOverDayRate, accumulatedVolume);
    }
}
