package EtfRecommendService.restAPI;

import lombok.*;
import java.util.Map;

//한투에 요청한 데이터 받는 부분
@Builder
public record StockPriceSnapshotDTO(
        String stockCode,         // 종목코드
        int currentPrice,         // 현재가
        String dayOverDaySign,    // 등락 부호 (상승/하락 등)
        int dayOverDayChange,     // 전일 대비 가격 차이
        double dayOverDayRate    // 전일 대비 등락률
//        long accumulatedVolume   // 누적 거래량
)
{
    //한투에서 받은 데이터 변환
    //응답이 JSON → Map<String, String> 형태로 오기 때문에 직접 사용하는 타입으로 가공해서 처리해야 함
    public static StockPriceSnapshotDTO fromKisResponse(Map<String, String> data) {
        return new StockPriceSnapshotDTO(
                builder().stockCode,
                Integer.parseInt(data.get("stck_prpr")), //주식현재가
                data.get("prdy_vrss_sign"), //전일대비 부호
                Integer.parseInt(data.get("prdy_vrss")), //전일 대비
                Double.parseDouble(data.get("prdy_ctrt")) //전일 대비 율
//                Long.parseLong(data.get("acml_vol"))  //누적거래량은 주지 않음
        );
    }
}
