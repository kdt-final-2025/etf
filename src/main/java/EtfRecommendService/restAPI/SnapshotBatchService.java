package EtfRecommendService.restAPI;

import EtfRecommendService.webSocket.CsvLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

//장 마감 후 배치용
//CSV 전체 종목 일괄 조회 → DB/캐시 갱신
//15:30 장 마감 이후 (Scheduled 배치 실행)

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotBatchService {

    private final KisRestClient restClient;
    private final SnapshotRepository repo;
    private final CsvLoader csvLoader;
    private RedisTemplate<String, Object> redisTemplate; // 캐시용 - 실시간 조회 속도 향상, 장중엔 사용 안함

    private final Object lock = new Object();
    private boolean running = false;

    //배치 저장
    @Scheduled(cron = "0 30 15 * * MON-FRI") // 평일 장 마감 직후 15:30
    public void fetchAndStoreAll() {
        synchronized (lock) {
            if (running) {
                log.warn("✅ 배치 작업이 이미 실행 중입니다.");
                return;
            }
            running = true;
        }

        log.info("📦 배치 작업 시작");

        try {
            // 1. 기존 DB 삭제
            repo.deleteAll();
            log.info("🧹 기존 Snapshot 데이터 전체 삭제 완료");

            // 2. 전체 종목 코드 가져오기
            List<String> allCodes = csvLoader.getCodes();
            log.info("🔎 총 {}개 종목 코드 로딩 완료", allCodes.size());

            // 3. Snapshot 데이터 수집
            List<StockPriceSnapshotDTO> snapshots = restClient.fetchAllSnapshots(allCodes);
            log.info("📊 총 {}개 종목 Snapshot 수집 완료", snapshots.size());

            // 4. Entity로 변환
            List<SnapshotEntity> entities = snapshots.stream()
                    .map(SnapshotEntity::fromSnapshot)
                    .toList();

            // 5. DB 저장
            repo.saveAll(entities);
            log.info("💾 Snapshot 데이터 DB 저장 완료");

            // 6. Redis 캐시 갱신
            for (StockPriceSnapshotDTO dto : snapshots) {
                String cacheKey = "snapshotCache::" + dto.stockCode();
                redisTemplate.opsForValue().set(cacheKey, dto);
            }
            log.info("⚡ Redis 캐시 갱신 완료");

            log.info("✅ 배치 작업 정상 완료");

        } catch (Exception e) {
            log.error("❌ 배치 작업 중 예외 발생", e);
            // TODO: 슬랙/메일 알림 연동 등 필요시
        } finally {
            synchronized (lock) {
                running = false;
            }
        }
    }
}
