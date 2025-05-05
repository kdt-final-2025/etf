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
    private String code;
    private double price;
    private String sign;
    private double rate;
    private long volume;

    //데이터 파싱
    public static StockPriceData parseFromFields(String[] fields) {
        StockPriceData d = new StockPriceData();
        d.setCode(fields[0]);
        d.setPrice(Double.parseDouble(fields[1]));
        d.setSign(fields[2]);
        d.setRate(Double.parseDouble(fields[3]));
        d.setVolume(Long.parseLong(fields[4]));
        return d;
    }

    @Override
    public String toString() {
        return String.format("code=%s price=%.2f sign=%s rate=%.2f%% volume=%d",
                code, price, sign, rate, volume);
    }
}
