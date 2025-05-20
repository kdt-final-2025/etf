package EtfRecommendService.restAPI;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/snapshots")
@RequiredArgsConstructor
public class SnapshotController {
    private final SnapshotService snapshotService;
    private final SnapshotBatchService batchService; // 배치 모드 컨트롤러

    // 장중 모드: 페이지만 실시간 조회
    @GetMapping
    public List<StockPriceSnapshotDTO> getByPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(defaultValue = "false") boolean useCache
    ) {
        if (useCache) {
            return snapshotService.fetchFromDb(page, size);
        }
        return snapshotService.fetchFromRest(page, size);
    }

    // (관리용) 수동 재실행
    @PostMapping("/batch")
    public void runBatch() {
        batchService.fetchAndStoreAll();
    }
}

