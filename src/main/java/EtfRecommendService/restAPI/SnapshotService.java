package EtfRecommendService.restAPI;

import EtfRecommendService.webSocket.CsvLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//장중 (실시간) 조회용
@Service
@RequiredArgsConstructor
public class SnapshotService {

    private final CsvLoader csvLoader;
    private final KisRestClient kisRestClient;
    private final SnapshotRepository snapshotRepository;

    // DB에서 페이징 조회 (캐시 모드)
    public List<StockPriceSnapshotDTO> fetchFromDb(int page, int size) {
        Page<SnapshotEntity> pageData = snapshotRepository.findAll(PageRequest.of(page, size));
        return pageData.stream()
                .map(entity -> new StockPriceSnapshotDTO(
                        entity.getStockCode(),
                        entity.getCurrentPrice(),
                        entity.getDayOverDaySign(),
                        entity.getDayOverDayChange(),
                        entity.getDayOverDayRate()
                )) // Entity → DTO 변환 메서드
                .toList();
    }

    // REST API 직접 호출 (실시간 조회)
    //이 데이터는 메모리(DB/캐시)에 저장되지 않음. 일회성
    public List<StockPriceSnapshotDTO> fetchFromRest(int page, int size) {
        List<String> codes = csvLoader.getCodes();
        int from = page * size;
        int to = Math.min(from + size, codes.size());

        return codes.subList(from, to).stream()
                .map(kisRestClient::fetchSingleSnapshot)// 단일 조회
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
