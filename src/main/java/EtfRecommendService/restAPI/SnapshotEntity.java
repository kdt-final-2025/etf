package EtfRecommendService.restAPI;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "snapshots")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockCode;         // 종목코드
    private int currentPrice;         // 현재가
    private String dayOverDaySign;    // 등락 부호 (상승/하락 등)
    private int dayOverDayChange;     // 전일 대비 가격 차이
    private double dayOverDayRate;    // 전일 대비 등락률
//    private long accumulatedVolume;   // 누적 거래량

    //한투 요청 데이터 - dto - 엔티티에서 가공 - db저장
    public static SnapshotEntity fromSnapshot(StockPriceSnapshotDTO dto) {
        return new SnapshotEntity(
                null,
                dto.stockCode(),
                dto.currentPrice(),
                dto.dayOverDaySign(),
                dto.dayOverDayChange(),
                dto.dayOverDayRate()
//                dto.accumulatedVolume()
        );
    }

}
